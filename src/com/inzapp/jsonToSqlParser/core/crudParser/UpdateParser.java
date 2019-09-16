package com.inzapp.jsonToSqlParser.core.crudParser;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateParser extends JsonManager {
    /**
     * update statement for return
     */
    private Update update = new Update();

    /**
     * head method of update parser
     *
     * @param json not converted json object
     * @return converted update statement
     */
    public Update parse(JSONObject json) {
        setJson(json);
        addColumns();
        addTables();
        addValues();
        addWhere();
        return update;
    }

    /**
     * add columns to update statement
     */
    private void addColumns() {
        // columns
        List<String> columns = getFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            columns.forEach(column -> columnList.add(new Column(column)));
            this.update.setColumns(columnList);
        }
    }

    /**
     * add tables to update statement
     */
    private void addTables() {
        // tables
        List<String> tables = getFromJson(JsonKey.FROM);
        if (tables != null) {
            List<Table> tableList = new ArrayList<>();
            tables.forEach(table -> tableList.add(new Table(table)));
            this.update.setTables(tableList);
        }
    }

    /**
     * add values to update statement
     */
    private void addValues() {
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
            this.update.setExpressions(expressionList);
        }
    }

    /**
     * add where to update statement
     */
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

            this.update.setWhere(whereExpression);
        }
    }
}
