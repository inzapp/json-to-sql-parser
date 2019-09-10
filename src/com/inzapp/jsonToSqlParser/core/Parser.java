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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Parser extends JsonManager {
    public String parse(JSONObject json) {
        injectJson(json);

        Statement statement;
        String crud = getListFromJson(JsonKey.CRUD).get(0);
        switch (crud) {
            case JsonKey.INSERT:
                statement = getInsert();
                break;

            case JsonKey.SELECT:
                if (getJsonObjectFromJson(JsonKey.UNION + 1) != null || getJsonObjectFromJson(JsonKey.UNION_ALL + 1) != null)
                    statement = getUnionSelect();
                else statement = getSelect();
                break;

            case JsonKey.UPDATE:
                statement = getUpdate();
                break;

            case JsonKey.DELETE:
                statement = getDelete();
                break;

            default:
                System.out.println("unknown crud : " + crud);
                return null;
        }

        return statement.toString();
    }

    private Insert getInsert() {
        Insert insert = new Insert();

        // table
        List<String> tables = getListFromJson(JsonKey.FROM);
        if (tables != null) {
            String tableName = tables.get(0);
            insert.setTable(new Table(tableName));
        }

        // columns
        List<String> columns = getListFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            columns.forEach(column -> columnList.add(new Column(column)));
            insert.setColumns(columnList);
        }

        // values
        List<String> values = getListFromJson(JsonKey.VALUE);
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

    private Select getUnionSelect() {
        List<JSONObject> unions = new ArrayList<>();
        int idx = 1;
        while (true) {
            int exceptionCnt = 0;
            try {
                JSONObject union = getJsonObjectFromJson(JsonKey.UNION + idx++);
                union.put("key", JsonKey.UNION);
                union.put("sql", new Parser().parse(union));
                unions.add(union);
            } catch (Exception e) {
                --idx;
                ++exceptionCnt;
            }

            try {
                JSONObject unionAll = getJsonObjectFromJson(JsonKey.UNION_ALL + idx++);
                unionAll.put("key", JsonKey.UNION_ALL);
                unionAll.put("sql", new Parser().parse(unionAll));
                unions.add(unionAll);
            } catch (Exception e) {
                --idx;
                ++exceptionCnt;
            }

            if (exceptionCnt == 2)
                break;
        }

        System.out.println("union size : " + unions.size());

        List<Boolean> brackets = new ArrayList<>();
        List<SelectBody> selectBodies = new ArrayList<>();
        List<SetOperation> setOperations = new ArrayList<>();
        unions.forEach(union -> {
            brackets.add(true);

            try {
                String sql = (String) union.get("sql");
                SelectBody selectBody = new SelectBody() {
                    @Override
                    public void accept(SelectVisitor selectVisitor) {
                        // empty
                    }

                    @Override
                    public String toString() {
                        return sql;
                    }
                };
                selectBodies.add(selectBody);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String setOperationStr = (String) union.get("key");
                SetOperationList.SetOperationType setOperationType = SetOperationList.SetOperationType.UNION;
                SetOperation setOperation = new SetOperation(setOperationType) {
                    @Override
                    public String toString() {
                        return setOperationStr;
                    }
                };
                setOperations.add(setOperation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        SetOperationList setOperationList = new SetOperationList();
        setOperationList.setBracketsOpsAndSelects(brackets, selectBodies, setOperations);
        Select select = new Select();
        select.setSelectBody(setOperationList);
        return new Select();
    }

    private Select getSelect() {
        Select select = new Select();
        PlainSelect plainSelect = new PlainSelect();

        // distinct
        List<String> distincts = getListFromJson(JsonKey.DISTINCT);
        if (distincts != null)
            plainSelect.setDistinct(new Distinct(true));

        // columns
        List<String> columns = getListFromJson(JsonKey.COLUMN);
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
        List<String> table = getListFromJson(JsonKey.FROM);
        if (table != null) {
            FromItem fromItem = new FromItem() {
                @Override
                public void accept(FromItemVisitor fromItemVisitor) {
                    // empty
                }

                @Override
                public Alias getAlias() {
                    List<String> aliases = getListFromJson(JsonKey.FROM_ALIAS);
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
        List<String> wheres = getListFromJson(JsonKey.WHERE);
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
        List<String> groupBys = getListFromJson(JsonKey.GROUP_BY);
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
        List<String> orderBys = getListFromJson(JsonKey.ORDER_BY);
        if (orderBys != null) {
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
        while (true) {
            List<String> joins = getListFromJson(JsonKey.JOIN + idx++);
            if (joins != null) {
                joins.forEach(joinStr -> {
                    Join join = new Join() {
                        @Override
                        public String toString() {
                            return joinStr;
                        }
                    };
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
        List<String> columns = getListFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            columns.forEach(column -> columnList.add(new Column(column)));
            update.setColumns(columnList);
        }

        // tables
        List<String> tables = getListFromJson(JsonKey.FROM);
        if (tables != null) {
            List<Table> tableList = new ArrayList<>();
            tables.forEach(table -> tableList.add(new Table(table)));
            update.setTables(tableList);
        }

        // values
        List<String> values = getListFromJson(JsonKey.VALUE);
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
        List<String> wheres = getListFromJson(JsonKey.WHERE);
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
        List<String> tables = getListFromJson(JsonKey.FROM);
        if (tables != null)
            delete.setTable(new Table(tables.get(0)));

        // where
        List<String> wheres = getListFromJson(JsonKey.WHERE);
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
