package org.babyfish.jimmer.sql.middle;

import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.AffectedTable;
import org.babyfish.jimmer.sql.common.AbstractMutationTest;
import org.babyfish.jimmer.sql.event.TriggerType;
import org.babyfish.jimmer.sql.model.middle.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SaveTest extends AbstractMutationTest {

    @Test
    public void testSaveShop() {
        Shop shop = ShopDraft.$.produce(draft -> {
            draft.setId(1L);
            draft.setVipCustomers(
                    Collections.singletonList(
                            ImmutableObjects.makeIdOnly(Customer.class, 2L)
                    )
            );
            draft.setOrdinaryCustomers(
                    Arrays.asList(
                            ImmutableObjects.makeIdOnly(Customer.class, 1L),
                            ImmutableObjects.makeIdOnly(Customer.class, 4L)
                    )
            );
        });
        executeAndExpectResult(
                getSqlClient().getEntities().saveCommand(shop),
                ctx -> {
                    ctx.statement(it -> {
                        it.sql(
                                "delete from shop_customer_mapping " +
                                        "where " +
                                        "--->shop_id = ? " +
                                        "and " +
                                        "--->customer_id not in (?, ?) " +
                                        "and " +
                                        "--->type = ?"
                        );
                        it.variables(1L, 1L, 4L, "ORDINARY");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "merge into shop_customer_mapping(shop_id, customer_id, deleted_millis, type) " +
                                        "key(shop_id, customer_id, deleted_millis, type) values(?, ?, ?, ?)"
                        );
                        it.batchVariables(0, 1L, 1L, 0L, "ORDINARY");
                        it.batchVariables(1, 1L, 4L, 0L, "ORDINARY");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "delete from shop_customer_mapping " +
                                        "where " +
                                        "--->shop_id = ? " +
                                        "and " +
                                        "--->customer_id <> ? " +
                                        "and " +
                                        "--->type = ?"
                        );
                        it.variables(1L, 2L, "VIP");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "merge into shop_customer_mapping(shop_id, customer_id, deleted_millis, type) " +
                                        "key(shop_id, customer_id, deleted_millis, type) values(?, ?, ?, ?)"
                        );
                        it.batchVariables(0, 1L, 2L, 0L, "VIP");
                    });
                    ctx.rowCount(AffectedTable.of(ShopProps.VIP_CUSTOMERS), 2);
                    ctx.rowCount(AffectedTable.of(ShopProps.ORDINARY_CUSTOMERS), 4);
                    ctx.entity(it -> {
                       it.original(
                               "{\"id\":1,\"vipCustomers\":[{\"id\":2}],\"ordinaryCustomers\":[{\"id\":1},{\"id\":4}]}"
                       );
                       it.modified(
                               "{\"id\":1,\"vipCustomers\":[{\"id\":2}],\"ordinaryCustomers\":[{\"id\":1},{\"id\":4}]}"
                       );
                    });
                }
        );
    }

    @Test
    public void testSaveShopWithTrigger() {
        Shop shop = ShopDraft.$.produce(draft -> {
            draft.setId(1L);
            draft.setVipCustomers(
                    Collections.singletonList(
                            ImmutableObjects.makeIdOnly(Customer.class, 2L)
                    )
            );
            draft.setOrdinaryCustomers(
                    Arrays.asList(
                            ImmutableObjects.makeIdOnly(Customer.class, 1L),
                            ImmutableObjects.makeIdOnly(Customer.class, 4L)
                    )
            );
        });
        JSqlClient sqlClient = getSqlClient(it -> it.setTriggerType(TriggerType.TRANSACTION_ONLY));
        List<String> events = new ArrayList<>();
        sqlClient.getTriggers().addAssociationListener(e -> {
            StringBuilder builder = new StringBuilder();
            builder
                    .append(e.getImmutableProp().getDeclaringType().getJavaClass().getSimpleName())
                    .append('.')
                    .append(e.getImmutableProp().getName())
                    .append(": ");
            builder.append(e.getSourceId());
            if (e.getDetachedTargetId() != null) {
                builder.append(" - ").append(e.getDetachedTargetId());
            }
            if (e.getAttachedTargetId() != null) {
                builder.append(" + ").append(e.getAttachedTargetId());
            }
            String text = builder.toString();
            int index = Collections.binarySearch(events, text);
            if (index < 0) {
                events.add(-index - 1, text);
            } else {
                events.add(index + 1, text);
            }
        });
        executeAndExpectResult(
                sqlClient
                        .getEntities()
                        .saveCommand(shop),
                ctx -> {
                    ctx.statement(it -> {
                        it.sql("select tb_1_.ID, tb_1_.NAME from SHOP tb_1_ where tb_1_.ID = ?");
                        it.variables(1L);
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "select customer_id " +
                                        "from shop_customer_mapping " +
                                        "where shop_id = ? and type = ?"
                        );
                        it.variables(1L, "ORDINARY");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "delete from shop_customer_mapping " +
                                        "where shop_id = ? and customer_id = ? and type = ?"
                        );
                        it.batchVariables(0, 1L, 2L, "ORDINARY");
                        it.batchVariables(1, 1L, 3L, "ORDINARY");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "insert into shop_customer_mapping(shop_id, customer_id, deleted_millis, type) " +
                                        "values(?, ?, ?, ?)"
                        );
                        it.variables(1L, 1L, 0L, "ORDINARY");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "select customer_id from shop_customer_mapping where shop_id = ? and type = ?"
                        );
                        it.variables(1L, "VIP");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "delete from shop_customer_mapping " +
                                        "where shop_id = ? and customer_id = ? and type = ?"
                        );
                        it.variables(1L, 1L, "VIP");
                    });
                    ctx.statement(it -> {
                        it.sql(
                                "insert into shop_customer_mapping(shop_id, customer_id, deleted_millis, type) " +
                                        "values(?, ?, ?, ?)"
                        );
                        it.variables(1L, 2L, 0L, "VIP");
                    });
                    ctx.rowCount(AffectedTable.of(ShopProps.VIP_CUSTOMERS), 2);
                    ctx.rowCount(AffectedTable.of(ShopProps.ORDINARY_CUSTOMERS), 3);
                    ctx.entity(it -> {
                        it.original(
                                "{\"id\":1,\"vipCustomers\":[{\"id\":2}],\"ordinaryCustomers\":[{\"id\":1},{\"id\":4}]}"
                        );
                        it.modified(
                                "{\"id\":1,\"vipCustomers\":[{\"id\":2}],\"ordinaryCustomers\":[{\"id\":1},{\"id\":4}]}"
                        );
                    });
                }
        );
        Assertions.assertEquals(
                "[Customer.ordinaryShops: 1 + 1, " +
                        "Customer.ordinaryShops: 2 - 1, " +
                        "Customer.ordinaryShops: 3 - 1, " +
                        "Customer.vipShops: 1 - 1, " +
                        "Customer.vipShops: 2 + 1, " +
                        "Shop.ordinaryCustomers: 1 + 1, " +
                        "Shop.ordinaryCustomers: 1 - 2, " +
                        "Shop.ordinaryCustomers: 1 - 3, " +
                        "Shop.vipCustomers: 1 + 2, " +
                        "Shop.vipCustomers: 1 - 1]",
                events.toString()
        );
    }
}
