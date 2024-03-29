package com.inzapp.jsonToSqlParser.core.crudParser;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InsertParser extends JsonManager {
    /**
     * insert statement for return
     */
    private Insert insert = new Insert();

    /**
     * head method of insert parser
     *
     * @param json not converted json object
     * @return converted insert statement
     */
    public Insert parse(JSONObject json) {
        setJson(json);
        addTable();
        addColumns();
        addValues();
        return this.insert;
    }

    /**
     * add table to insert statement
     */
    private void addTable() {
        // table
        List<String> tables = getFromJson(JsonKey.FROM);
        if (tables != null) {
            String tableName = tables.get(0);
            this.insert.setTable(new Table(tableName));
        }
    }

    /**
     * add columns to insert statement
     */
    private void addColumns() {
        // columns
        List<String> columns = getFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            columns.forEach(column -> columnList.add(new Column(column)));
            this.insert.setColumns(columnList);
        }
    }

    /**
     * add values to insert statement
     */
    private void addValues() {
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
            this.insert.setItemsList(expressionList);
        }
    }
}
