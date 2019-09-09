package com.inzapp.jsonToSqlParser.core;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.json.JSONObject;

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

        // add table
        List<String> tables = getFromJson(JsonKey.FROM);
        if (tables != null) {
            String tableName = tables.get(0);
            insert.setTable(new Table(tableName));
        }

        // add columns
        List<String> columns = getFromJson(JsonKey.COLUMN);
        if (columns != null) {
            List<Column> columnList = new ArrayList<>();
            columns.forEach(column -> columnList.add(new Column(column)));
            insert.setColumns(columnList);
        }

        // add values
        List<String> values = getFromJson(JsonKey.VALUE);
        if(values != null) {
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
        return null;
    }

    private Update getUpdate() {
        return null;
    }

    private Delete getDelete() {
        return null;
    }
}
