package com.inzapp.jsonToSqlParser.core;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.crudParser.*;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.statement.Statement;
import org.json.JSONObject;

public class Parser extends JsonManager {
    /**
     * head method of Parser
     * parse json object to sql string
     *
     * @param json not converted json object
     * @return converted sql string
     */
    public String parse(JSONObject json) {
        return parse(json, false);
    }

    /**
     * only used for parsing union select
     *
     * @param json not converted json object
     * @param exceptUnion if true, parse without union statement
     *                    else, parse with union statement
     * @return parsed sql string
     */
    public String parse(JSONObject json, boolean exceptUnion) {
        setJson(json);
        Statement statement;
        String crud = getFromJson(JsonKey.CRUD).get(0);
        switch (crud) {
            case JsonKey.INSERT:
                statement = new InsertParser().parse(json);
                break;

            case JsonKey.SELECT:
                statement = exceptUnion ? new SelectParser().parse(json) : new UnionSelectParser().parse(json);
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
