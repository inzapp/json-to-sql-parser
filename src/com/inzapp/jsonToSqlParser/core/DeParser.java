package com.inzapp.jsonToSqlParser.core;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class DeParser extends JsonManager {
    public String deParse(JSONObject json) {
        injectJson(json);

        Statement statement;
        String crud = getFromJson(JsonKey.CRUD).get(0);
        switch (crud) {
            case JsonKey.INSERT:
                statement = getInsert();
                break;

            case JsonKey.SELECT:
                statement = getSelect();
                break;

            case JsonKey.UPDATE:
                statement = getUpdate();
                break;

            case JsonKey.DELETE:
                statement = getDelete();
                break;

            default:
                System.out.println("unknown crud");
                return null;
        }

        return statement == null ? null : statement.toString();
    }

    private Insert getInsert() {
        Insert insert = new Insert();

        // table
        List<String> tables = getFromJson(JsonKey.FROM);
        if (tables != null) {
            String tableName = tables.get(0);
            insert.setTable(new Table(tableName));
        }

        // columns
        List<String> columns = getFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            columns.forEach(column -> columnList.add(new Column(column)));
            insert.setColumns(columnList);
        }

        // values
        List<String> values = getFromJson(JsonKey.VALUE);
        if (values != null) {
            List<Expression> expressions = new ArrayList<>();
            values.forEach(value -> {
                Expression expression = new Expression() {
                    @Override
                    public SimpleNode getASTNode() {
                        return null;
                    }

                    @Override
                    public void setASTNode(SimpleNode simpleNode) {
                        // empty
                    }

                    @Override
                    public void accept(ExpressionVisitor expressionVisitor) {
                        // empty
                    }

                    @Override
                    public String toString() {
                        return value;
                    }
                };
                expressions.add(expression);
            });

            ExpressionList expressionList = new ExpressionList(expressions);
            insert.setItemsList(expressionList);
        }

        return insert;
    }

    private Select getSelect() {
        Select select = new Select();
        PlainSelect plainSelect = new PlainSelect();

        // distinct
        List<String> distincts = getFromJson(JsonKey.DISTINCT);
        if (distincts != null)
            plainSelect.setDistinct(new Distinct(true));

        // columns
        List<String> columns = getFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<SelectItem> selectItemList = new ArrayList<>();
            columns.forEach(column -> {
                SelectItem selectItem = new SelectItem() {
                    @Override
                    public void accept(SelectItemVisitor selectItemVisitor) {
                        // empty
                    }

                    @Override
                    public SimpleNode getASTNode() {
                        return null;
                    }

                    @Override
                    public void setASTNode(SimpleNode simpleNode) {
                        // empty
                    }

                    @Override
                    public String toString() {
                        return column;
                    }
                };
                selectItemList.add(selectItem);
            });
            plainSelect.setSelectItems(selectItemList);
        }

        // table
        List<String> table = getFromJson(JsonKey.FROM);
        if (table != null) {
            FromItem fromItem = new FromItem() {
                @Override
                public void accept(FromItemVisitor fromItemVisitor) {
                    // empty
                }

                @Override
                public Alias getAlias() {
                    List<String> aliases = getFromJson(JsonKey.FROM_ALIAS);
                    return aliases == null ? null : new Alias(aliases.get(0));
                }

                @Override
                public void setAlias(Alias alias) {
                    // empty
                }

                @Override
                public Pivot getPivot() {
                    return null;
                }

                @Override
                public void setPivot(Pivot pivot) {
                    // empty
                }

                @Override
                public String toString() {
                    return table.get(0);
                }
            };
            plainSelect.setFromItem(fromItem);
        }

        // where
        List<String> wheres = getFromJson(JsonKey.WHERE);
        if (wheres != null) {
            Expression whereExpression = new Expression() {
                @Override
                public void accept(ExpressionVisitor expressionVisitor) {
                    // empty
                }

                @Override
                public SimpleNode getASTNode() {
                    return null;
                }

                @Override
                public void setASTNode(SimpleNode simpleNode) {
                    // empty
                }

                @Override
                public String toString() {
                    return wheres.get(0);
                }
            };
            plainSelect.setWhere(whereExpression);
        }

        // group by
        List<String> groupBys = getFromJson(JsonKey.GROUP_BY);
        if (groupBys != null) {
            GroupByElement groupByElement = new GroupByElement();
            groupBys.forEach(groupBy -> {
                Expression groupByExpression = new Expression() {
                    @Override
                    public void accept(ExpressionVisitor expressionVisitor) {
                        // empty
                    }

                    @Override
                    public SimpleNode getASTNode() {
                        return null;
                    }

                    @Override
                    public void setASTNode(SimpleNode simpleNode) {
                        // empty
                    }

                    @Override
                    public String toString() {
                        return groupBy;
                    }
                };
                groupByElement.addGroupByExpression(groupByExpression);
            });
            plainSelect.setGroupByElement(groupByElement);
        }

        // order by
        List<String> orderBys = getFromJson(JsonKey.ORDER_BY);
        if(orderBys != null) {
            List<OrderByElement> orderByElementList = new ArrayList<>();
            orderBys.forEach(orderBy -> {
                OrderByElement orderByElement = new OrderByElement();
                Expression orderByExpression = new Expression() {
                    @Override
                    public void accept(ExpressionVisitor expressionVisitor) {
                        // empty
                    }

                    @Override
                    public SimpleNode getASTNode() {
                        return null;
                    }

                    @Override
                    public void setASTNode(SimpleNode simpleNode) {
                        // empty
                    }

                    @Override
                    public String toString() {
                        return orderBys.get(0);
                    }
                };
                orderByElement.setExpression(orderByExpression);
                orderByElementList.add(orderByElement);
            });
            plainSelect.setOrderByElements(orderByElementList);
        }

        // joins
        int idx = 1;
        List<Join> joinList = new ArrayList<>();
        while(true) {
            List<String> joins = getFromJson(JsonKey.JOIN + idx++);
            if(joins != null) {
                joins.forEach(joinStr -> {
                    Join join = new Join();
                    FromItem rightItem = new FromItem() {
                        @Override
                        public void accept(FromItemVisitor fromItemVisitor) {
                            // empty
                        }

                        @Override
                        public Alias getAlias() {
                            return null;
                        }

                        @Override
                        public void setAlias(Alias alias) {
                            // empty
                        }

                        @Override
                        public Pivot getPivot() {
                            return null;
                        }

                        @Override
                        public void setPivot(Pivot pivot) {
                            // empty
                        }

                        @Override
                        public String toString() {
                            System.out.printf("[join] [%s]\n", joinStr);
                            return joinStr;
                        }
                    };
                    join.setRightItem(rightItem);
                    joinList.add(join);
                });
            } else break;
        }
        plainSelect.setJoins(joinList.size() == 0 ? null : joinList);

        select.setSelectBody(plainSelect);
        return select;
    }

    private Update getUpdate() {
        Update update = new Update();

        // columns
        List<String> columns = getFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            columns.forEach(column -> columnList.add(new Column(column)));
            update.setColumns(columnList);
        }

        // tables
        List<String> tables = getFromJson(JsonKey.FROM);
        if (tables != null) {
            List<Table> tableList = new ArrayList<>();
            tables.forEach(table -> tableList.add(new Table(table)));
            update.setTables(tableList);
        }

        // values
        List<String> values = getFromJson(JsonKey.VALUE);
        if (values != null) {
            List<Expression> expressionList = new ArrayList<>();
            values.forEach(value -> {
                Expression expression = new Expression() {
                    @Override
                    public void accept(ExpressionVisitor expressionVisitor) {
                        // empty
                    }

                    @Override
                    public SimpleNode getASTNode() {
                        return null;
                    }

                    @Override
                    public void setASTNode(SimpleNode simpleNode) {
                        // empty
                    }

                    @Override
                    public String toString() {
                        return value;
                    }
                };

                expressionList.add(expression);
            });
            update.setExpressions(expressionList);
        }

        // where
        List<String> wheres = getFromJson(JsonKey.WHERE);
        if (wheres != null) {
            Expression whereExpression = new Expression() {
                @Override
                public void accept(ExpressionVisitor expressionVisitor) {
                    // empty
                }

                @Override
                public SimpleNode getASTNode() {
                    return null;
                }

                @Override
                public void setASTNode(SimpleNode simpleNode) {
                    // empty
                }

                @Override
                public String toString() {
                    return wheres.get(0);
                }
            };

            update.setWhere(whereExpression);
        }

        return update;
    }

    private Delete getDelete() {
        Delete delete = new Delete();

        // table
        List<String> tables = getFromJson(JsonKey.FROM);
        if (tables != null)
            delete.setTable(new Table(tables.get(0)));

        // where
        List<String> wheres = getFromJson(JsonKey.WHERE);
        if (wheres != null) {
            Expression whereExpression = new Expression() {
                @Override
                public void accept(ExpressionVisitor expressionVisitor) {
                    // empty
                }

                @Override
                public SimpleNode getASTNode() {
                    return null;
                }

                @Override
                public void setASTNode(SimpleNode simpleNode) {
                    // empty
                }

                @Override
                public String toString() {
                    return wheres.get(0);
                }
            };

            delete.setWhere(whereExpression);
        }

        return delete;
    }
}
