package com.inzapp.jsonToSqlParser.core.crudParser;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import org.json.JSONObject;

import java.util.List;

public class DeleteParser extends JsonManager {
    private Delete delete = new Delete();

    public Delete parse(JSONObject json) {
        setJson(json);
        addTable();
        addWhere();
        return delete;
    }

    private void addTable() {
        // table
        List<String> tables = getFromJson(JsonKey.FROM);
        if (tables != null)
            this.delete.setTable(new Table(tables.get(0)));
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

            this.delete.setWhere(whereExpression);
        }
    }
}
