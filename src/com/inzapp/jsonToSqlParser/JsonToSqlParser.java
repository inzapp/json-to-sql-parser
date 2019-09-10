package com.inzapp.jsonToSqlParser;

import com.inzapp.jsonToSqlParser.config.Config;
import com.inzapp.jsonToSqlParser.core.Parser;
import com.inzapp.sqlToJsonParser.SqlToJsonParser;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

public class JsonToSqlParser extends Parser {
    public static void main(String[] args) {
        String inputFileName = Config.INPUT_FILE_NAME;
        String outputFileName = Config.OUTPUT_FILE_NAME;
        if (args != null && args.length == 2) {
            inputFileName = args[0];
            outputFileName = args[1];
        }

        JsonToSqlParser jsonToSqlParser = new JsonToSqlParser();
        JSONObject json = jsonToSqlParser.readJsonFromFile(inputFileName);
        if(json == null) {
            System.out.println("failed to load json");
            return;
        }

        String sql = jsonToSqlParser.parse(json);
        if(sql == null) {
            System.out.println("parse failure");
            return;
        }

        try {
            System.out.println("input json\n");
            System.out.println(json.toString(4));
            System.out.println();

            System.out.println("output sql\n");
            System.out.println(sql);
            sql = new SqlToJsonParser().parse(sql);
            System.out.println();
        }catch(Exception e) {
            e.printStackTrace();
        }

        jsonToSqlParser.saveFile(sql, outputFileName);
        System.out.println("parse success");
    }

    private JSONObject readJsonFromFile(String fileName) {
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
            return new JSONObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveFile(String sql, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(sql.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}