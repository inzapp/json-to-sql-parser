package com.inzapp.jsonToSqlParser.core;

import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import org.json.JSONObject;

public class DeParser extends JsonManager {
    public String deParse(JSONObject json) {
        return "sql";
    }
}
