package com.inzapp.jsonToSqlParser;

import com.inzapp.jsonToSqlParser.config.Config;
import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.Parser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
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
        if (json == null) {
            System.out.println("failed to load json");
            return;
        }

        String sql = jsonToSqlParser.parse(json);
        if (sql == null) {
            System.out.println("parse failure");
            return;
        }
        sql = jsonToSqlParser.removeOuterBracket(sql);
//        sql = jsonToSqlParser.changeLine(sql);

        try {
            System.out.println("input json\n");
            System.out.println(json.toString(4));
            System.out.println();

            System.out.println("output sql\n");
            System.out.println(sql);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }

        jsonToSqlParser.saveFile(sql, outputFileName);
        System.out.println("parse success");
    }

    public String parse(String jsonString) {
        try {
            String sql = new Parser().parse(new JSONObject(jsonString));
            sql = removeOuterBracket(sql);
//            sql = changeLine(sql);
            // TODO : auto line change
            CCJSqlParserUtil.parse(sql); // query execution test
            return sql;
        } catch (Exception e) {
            return null;
        }
    }

    private String removeOuterBracket(String sql) {
        if (sql.charAt(0) == '(' && sql.charAt(sql.length() - 1) == ')') {
            StringBuilder sb = new StringBuilder(sql);
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } else return sql;
    }

    private String changeLine(String sql) {
        sql = sql.replaceAll(JsonKey.SELECT, JsonKey.SELECT + '\n');
        sql = sql.replaceAll(JsonKey.UPDATE, JsonKey.UPDATE + '\n');
//        sql = sql.replaceAll(JsonKey.DELETE, JsonKey.DELETE + '\n');

        sql = sql.replaceAll("FROM", "\nFROM\n");
        sql = sql.replaceAll("WHERE", "\nWHERE\n");
        sql = sql.replaceAll("AND", "AND\n");
        sql = sql.replaceAll(",", ",\n");
        sql = sql.replaceAll("\\(", "\\(\n");
        sql = sql.replaceAll("\\)", "\\)\n");
        return sql;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}