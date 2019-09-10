package com.inzapp.jsonToSqlParser.core;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.crudParser.*;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.statement.Statement;
import org.json.JSONObject;

public class Parser extends JsonManager {
    public String parse(JSONObject json) {
        injectJson(json);

        Statement statement;
        String crud = getFromJson(JsonKey.CRUD).get(0);
        switch (crud) {
            case JsonKey.INSERT:
                statement = new InsertParser().parse(json);
                break;

            case JsonKey.SELECT:
                if (getFromJson(JsonKey.UNION + 1) != null || getFromJson(JsonKey.UNION_ALL + 1) != null)
                    statement = new UnionSelectParser().parse(json);
                else statement = new SelectParser().parse(json);
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

    public String parse(JSONObject json, boolean exceptUnion) {
        injectJson(json);

        Statement statement;
        String crud = getFromJson(JsonKey.CRUD).get(0);
        switch (crud) {
            case JsonKey.INSERT:
                statement = new InsertParser().parse(json);
                break;

            case JsonKey.SELECT:
                statement = new SelectParser().parse(json);
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
