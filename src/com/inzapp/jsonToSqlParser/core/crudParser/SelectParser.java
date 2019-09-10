package com.inzapp.jsonToSqlParser.core.crudParser;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.statement.select.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectParser extends JsonManager {
    private Select select = new Select();
    private PlainSelect plainSelect = new PlainSelect();

    private void addDistinct() {
        // distinct
        List<String> distincts = getFromJson(JsonKey.DISTINCT);
        if (distincts != null)
            this.plainSelect.setDistinct(new Distinct(true));
    }

    private void addColumns() {
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
            this.plainSelect.setSelectItems(selectItemList);
        }
    }

    private void addTable() {
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
            this.plainSelect.setFromItem(fromItem);
        }
    }

    private void addWhere() {
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
            this.plainSelect.setWhere(whereExpression);
        }
    }

    private void addGroupBy() {
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
            this.plainSelect.setGroupByElement(groupByElement);
        }
    }

    private void addOrderBy() {
        // order by
        List<String> orderBys = getFromJson(JsonKey.ORDER_BY);
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
            this.plainSelect.setOrderByElements(orderByElementList);
        }
    }

    private void addJoins() {
        // joins
        int idx = 1;
        List<Join> joinList = new ArrayList<>();
        while (true) {
            List<String> joins = getFromJson(JsonKey.JOIN + idx++);
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
        this.plainSelect.setJoins(joinList.size() == 0 ? null : joinList);
    }

    public Select parse(JSONObject json) {
        injectJson(json);
        addDistinct();
        addColumns();
        addTable();
        addWhere();
        addGroupBy();
        addOrderBy();
        addJoins();
        this.select.setSelectBody(this.plainSelect);
        return this.select;
    }
}
