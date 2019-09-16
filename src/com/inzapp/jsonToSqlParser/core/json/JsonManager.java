package com.inzapp.jsonToSqlParser.core.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonManager {
    /**
     * root json object
     */
    private JSONObject json;

    /**
     * get root json object
     *
     * @return this.json
     */
    protected JSONObject getJson() {
        return this.json;
    }

    /**
     * set root json object
     *
     * @param json root json object
     */
    protected void setJson(JSONObject json) {
        this.json = json;
    }

    /**
     * get value of json using json key
     *
     * @param jsonKey com.inzapp.jsonToSqlParser.config.JsonKey
     * @return all json value is json array type. so it converted to java list type
     */
    protected List<String> getFromJson(String jsonKey) {
        try {
            JSONArray jsonArray = (JSONArray) this.json.get(jsonKey);
            return convertJsonArrayToList(jsonArray);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * convert json array to java list type
     *
     * @param jsonArray json value from json key
     * @return converted java list from json array
     */
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
