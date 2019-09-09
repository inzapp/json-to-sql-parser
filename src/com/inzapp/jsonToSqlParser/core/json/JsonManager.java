package com.inzapp.jsonToSqlParser.core.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonManager {
    private JSONObject json;

    protected void injectJson(JSONObject json) {
        this.json = json;
    }

    protected List<String> getFromJson(String jsonKey) {
        try {
            JSONArray jsonArray = (JSONArray) this.json.get(jsonKey);
            return convertJsonArrayToList(jsonArray);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> convertJsonArrayToList(JSONArray jsonArray) {
        try {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i)
                list.add((String) jsonArray.get(i));
            return list;
        } catch (Exception e) {
            return null;
        }
    }
}
