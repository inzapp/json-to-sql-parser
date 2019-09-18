package com.inzapp.jsonToSqlParser;

import com.inzapp.jsonToSqlParser.config.Config;
import com.inzapp.jsonToSqlParser.core.Parser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

public class JsonToSqlParser extends Parser {
    /**
     * entry point in execution jar file
     *
     * @param args [0] : input file name, default is "input.json"
     *             [1] : output file name, default is "output.txt"
     */
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

        String sql = new JsonToSqlParser().parse(json.toString());
        if (sql == null) {
            System.out.println("parse failure");
            return;
        }

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

    /**
     * head method as java library
     *
     * @param jsonString json string from sql
     * @return converted sql string
     */
    public String parse(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            String sql = new Parser().parse(json);
            CCJSqlParserUtil.parse(sql); // query execution test
            return sql;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * read json string from file and convert is to json object
     *
     * @param fileName input file name, default is "input.json"
     * @return converted json object
     */
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

    /**
     * save converted sql string to file
     *
     * @param sql      converted sql from parser
     * @param fileName output file name, default is "output.txt"
     */
    private void saveFile(String sql, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(sql.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}