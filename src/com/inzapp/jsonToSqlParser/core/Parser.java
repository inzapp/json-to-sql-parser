package com.inzapp.jsonToSqlParser.core;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.crudParser.*;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.statement.Statement;
import org.json.JSONObject;

public class Parser extends JsonManager {
    public String parse(JSONObject json) {
        return parse(json, false);
    }

    public String parse(JSONObject json, boolean exceptUnion) {
        setJson(json);
        Statement statement;
        String crud = getFromJson(JsonKey.CRUD).get(0);
        switch (crud) {
            case JsonKey.INSERT:
                statement = new InsertParser().parse(json);
                break;

            case JsonKey.SELECT:
                if(exceptUnion) statement = new SelectParser().parse(json);
                else statement = new UnionSelectParser().parse(json);
                break;

            case JsonKey.UPDATE:
                statement = new UpdateParser().parse(json);
                break;

            case JsonKey.DELETE:
                statement = new DeleteParser().parse(json);
                break;

            default:
                System.out.println("unknown crud : " + crud);
                return null;
        }

        return statement.toString();
    }
}
