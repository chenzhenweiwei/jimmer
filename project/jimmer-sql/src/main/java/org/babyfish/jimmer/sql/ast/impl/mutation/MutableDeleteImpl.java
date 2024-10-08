package org.babyfish.jimmer.sql.ast.impl.mutation;

import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.PropId;
import org.babyfish.jimmer.runtime.ImmutableSpi;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.impl.AbstractMutableStatementImpl;
import org.babyfish.jimmer.sql.ast.impl.Ast;
import org.babyfish.jimmer.sql.ast.impl.AstContext;
import org.babyfish.jimmer.sql.ast.impl.AstVisitor;
import org.babyfish.jimmer.sql.ast.impl.query.MutableRootQueryImpl;
import org.babyfish.jimmer.sql.ast.impl.query.UseTableVisitor;
import org.babyfish.jimmer.sql.ast.impl.table.StatementContext;
import org.babyfish.jimmer.sql.ast.impl.table.TableImplementor;
import org.babyfish.jimmer.sql.ast.mutation.DeleteMode;
import org.babyfish.jimmer.sql.ast.mutation.MutableDelete;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.TableEx;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.babyfish.jimmer.sql.ast.tuple.Tuple3;
import org.babyfish.jimmer.sql.event.TriggerType;
import org.babyfish.jimmer.sql.runtime.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class MutableDeleteImpl
        extends AbstractMutableStatementImpl
        implements MutableDelete {

    private final MutableRootQueryImpl<TableEx<?>> deleteQuery;

    private boolean isDissociationDisabled;

    public MutableDeleteImpl(JSqlClientImplementor sqlClient, ImmutableType immutableType) {
        super(sqlClient, immutableType);
        deleteQuery = new MutableRootQueryImpl<>(
                new StatementContext(ExecutionPurpose.delete(QueryReason.CANNOT_DELETE_DIRECTLY)),
                sqlClient,
                immutableType
        );
    }

    public MutableDeleteImpl(JSqlClientImplementor sqlClient, TableProxy<?> table) {
        super(sqlClient, table);
        deleteQuery = new MutableRootQueryImpl<>(
                new StatementContext(ExecutionPurpose.delete(QueryReason.CANNOT_DELETE_DIRECTLY)),
                sqlClient,
                table
        );
    }

    @Override
    public <T extends Table<?>> T getTable() {
        return deleteQuery.getTable();
    }

    @Override
    public TableImplementor<?> getTableImplementor() {
        return deleteQuery.getTableImplementor();
    }

    @Override
    public AbstractMutableStatementImpl getParent() {
        return null;
    }

    @Override
    public StatementContext getContext() {
        return deleteQuery.getContext();
    }

    @Override
    public MutableDelete where(Predicate... predicates) {
        deleteQuery.where(predicates);
        return this;
    }

    @Override
    public void whereByFilter(TableImplementor<?> tableImplementor, List<Predicate> predicates) {
        deleteQuery.whereByFilter(tableImplementor, predicates);
    }

    @Override
    public MutableDelete disableDissociation() {
        isDissociationDisabled = true;
        return this;
    }

    @Override
    public Integer execute(Connection con) {
        return getSqlClient()
                .getConnectionManager()
                .execute(con, this::executeImpl);
    }

    @Override
    protected void onFrozen(AstContext astContext) {
        deleteQuery.freeze(astContext);
    }

    @SuppressWarnings("unchecked")
    private Integer executeImpl(Connection con) {

        JSqlClientImplementor sqlClient = getSqlClient();
        TableImplementor<?> table = getTableImplementor();

        AstContext astContext = new AstContext(sqlClient);
        applyVirtualPredicates(astContext);
        applyGlobalFilters(astContext, getContext().getFilterLevel(), null);

        deleteQuery.freeze(astContext);
        astContext.pushStatement(deleteQuery);
        try {
            AstVisitor visitor = new UseTableVisitor(astContext);
            for (Predicate predicate : deleteQuery.unfrozenPredicates()) {
                ((Ast) predicate).accept(visitor);
            }
        } finally {
            astContext.popStatement();
        }

        boolean binLogOnly = sqlClient.getTriggerType() == TriggerType.BINLOG_ONLY;
        DissociationInfo info = sqlClient.getEntityManager().getDissociationInfo(table.getImmutableType());
        boolean directly = table.isEmpty(it -> astContext.getTableUsedState(it) == TableUsedState.USED) &&
                           binLogOnly &&
                           (isDissociationDisabled || info == null || info.isDirectlyDeletable(sqlClient.getMetadataStrategy()));

        if (directly) {
            SqlBuilder builder = new SqlBuilder(astContext);
            astContext.pushStatement(this);
            try {
                renderDirectly(builder);
                Tuple3<String, List<Object>, List<Integer>> sqlResult = builder.build();
                return sqlClient.getExecutor().execute(
                        new Executor.Args<>(
                                getSqlClient(),
                                con,
                                sqlResult.get_1(),
                                sqlResult.get_2(),
                                sqlResult.get_3(),
                                ExecutionPurpose.delete(QueryReason.NONE),
                                null,
                                PreparedStatement::executeUpdate
                        )
                );
            } finally {
                astContext.popStatement();
            }
        }

        List<Object> ids = null;
        Collection<ImmutableSpi> rows = null;
        if (binLogOnly) {
            ids = deleteQuery
                    .select(table.get(table.getImmutableType().getIdProp()))
                    .distinct()
                    .execute(con);
            if (ids.isEmpty()) {
                return 0;
            }
        } else {
            rows = (List<ImmutableSpi>) deleteQuery
                    .select(table)
                    .execute(con);
            if (rows.isEmpty()) {
                return 0;
            }
        }
        Deleter deleter = new Deleter(
                table.getImmutableType(),
                new DeleteCommandImpl.OptionsImpl(sqlClient, con),
                con,
                binLogOnly ? null : new MutationTrigger(),
                new HashMap<>()
        );
        if (ids != null) {
            deleter.addIds(ids);
        } else {
            deleter.addRows(rows);
        }
        return deleter.execute().getTotalAffectedRowCount();
    }

    private void renderDirectly(SqlBuilder builder) {
        Predicate predicate = deleteQuery.getPredicate(builder.getAstContext());
        TableImplementor<?> table = getTableImplementor();
        builder.sql("delete");
        if (getSqlClient().getDialect().isDeletedAliasRequired()) {
            builder.sql(" ").sql(table.getAlias());
        }
        builder
                .from()
                .sql(table.getImmutableType().getTableName(getSqlClient().getMetadataStrategy()))
                .sql(" ")
                .sql(table.getAlias());
        if (predicate != null) {
            builder.enter(SqlBuilder.ScopeType.WHERE);
            ((Ast) predicate).renderTo(builder);
            builder.leave();
        }
    }
}
