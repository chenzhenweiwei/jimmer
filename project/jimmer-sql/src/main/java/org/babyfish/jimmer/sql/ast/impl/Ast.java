package org.babyfish.jimmer.sql.ast.impl;

import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.Selection;
import org.babyfish.jimmer.sql.ast.embedded.AbstractTypedEmbeddedPropExpression;
import org.babyfish.jimmer.sql.ast.impl.render.AbstractSqlBuilder;
import org.babyfish.jimmer.sql.ast.impl.table.RootTableResolver;
import org.babyfish.jimmer.sql.ast.impl.table.TableProxies;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.PropExpressionImplementor;
import org.jetbrains.annotations.NotNull;

public interface Ast {

    void accept(@NotNull AstVisitor visitor);

    void renderTo(@NotNull AbstractSqlBuilder<?> builder);

    boolean hasVirtualPredicate();

    Ast resolveVirtualPredicate(AstContext ctx);

    static Ast from(Selection<?> selection, RootTableResolver resolver) {
        if (selection instanceof Table<?>) {
            return TableProxies.resolve((Table<?>) selection, resolver);
        }
        return AbstractTypedEmbeddedPropExpression.unwrap(selection);
    }

    static Ast of(Expression<?> expr) {
        if (expr instanceof PropExpressionImplementor<?>) {
            return (Ast) ((PropExpressionImplementor<?>)expr).unwrap();
        }
        return (Ast) expr;
    }
}
