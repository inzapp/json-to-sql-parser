package com.inzapp.jsonToSqlParser;

import com.inzapp.jsonToSqlParser.config.Config;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

public class JsonToSqlParser {
    public static void main(String[] args) {
        String inputFileName = Config.INPUT_FILE_NAME;
        String outputFileName = Config.OUTPUT_FILE_NAME;
        if (args != null && args.length == 2) {
            inputFileName = args[0];
            outputFileName = args[1];
        }

        JSONObject json = readJsonFromFile(inputFileName);
    }

    private static JSONObject readJsonFromFile(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;

                sb.append(line).append('\n');
            }

            String jsonString = sb.toString();
            JSONObject json = new JSONObject(jsonString);
            return json;
        } catch (Exception e) {
            return null;
        }
    }
}