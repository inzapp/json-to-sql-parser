# JSON to SQL parser
org.json.JSONObject to SQL parser using [**JsqlParser**](https://github.com/JSQLParser/JSqlParser)<br>
Interact with [**SqlToJsonParser**](https://github.com/inzapp/sql-to-json-parser)<br>

## Download
https://github.com/inzapp/json-to-sql-parser/releases

## Usage
Run as default file name
Default input file name : input.json
Default output file name : output.txt
```bash
$ java -jar json-to-sql-parser.jar
```

Run as specified file name
```bash
$ java -jar json-to-sql-parser.jar yourInputFileName yourOutputFileName
```

In Java
```java
JsonToSqlParser jsonToSqlParser = new JsonToSqlParser();
String sql = jsonToSqlParser.parse(jsonObject);
```

## Select
input
```json
{
  "CRUD": ["SELECT"],
  "COLUMN": ["*"],
  "TABLE": ["TAB"]
}
```

output
```sql
SELECT * FROM TAB
```

## Insert
input
```json
{
  "CRUD": ["INSERT"],
  "TABLE": ["TABLENAME"],
  "VALUE": ["'TESTVALUE'"]
}
```

output
```sql
INSERT INTO TABLENAME VALUES ('TESTVALUE')
```

## Update
input
```json
{
  "CRUD": ["UPDATE"],
  "COLUMN": ["COLNAME"],
  "TABLE": ["TABLENAME"],
  "VALUE": ["1"],
  "WHERE": ["CONDITION = 2"]
}
```

output
```sql
UPDATE TABLENAME SET COLNAME = 1 WHERE CONDITION = 2
```

## Delete
input
```json
{
  "CRUD": ["DELETE"],
  "TABLE": ["TABLE"],
  "WHERE": ["CONDITION = 'ALL'"]
}
```

output
```sql
DELETE FROM TABLE WHERE CONDITION = 'ALL'
```

## Sub Query
input
```json
{
    "CRUD": ["SELECT"],
    "COLUMN": [
      "A",
      "B"
    ],
    "TABLE": ["(SELECT A, B FROM FROMTABLE WHERE SUBCONDITION = 'SUBCONDITION')"],
    "TABLE SUB QUERY 1": ["(SELECT A, B FROM FROMTABLE WHERE FROMCONDITION = 'FROMCONDITION')"],
    "TABLE SUB QUERY ANALYSE 1": {
        "CRUD": ["SELECT"],
        "COLUMN": [
          "A",
          "B"
        ],
        "TABLE": ["FROMTABLE"],
        "WHERE": ["FROMCONDITION = 'FROMCONDITION'"]
    },
    "WHERE": ["C = (SELECT C FROM WHERETABLE WHERE WHERECONDITION = 'WHERECONDITION')"],
    "WHERE SUB QUERY 1": ["(SELECT C FROM WHERETABLE WHERE WHERECONDITION = 'WHERECONDITION')"],
    "WHERE SUB QUERY ANALYSE 1": {
        "CRUD": ["SELECT"],
        "COLUMN": ["C"],
        "TABLE": ["WHERETABLE"],
        "WHERE": ["WHERECONDITION = 'WHERECONDITION'"]
    },
    "ORDER_BY": ["A"]
}
```

output
```sql
SELECT A, B FROM 
(
    SELECT A, B 
    FROM FROMTABLE 
    WHERE SUBCONDITION = 'SUBCONDITION'
) 
WHERE C = 
(
    SELECT C 
    FROM WHERETABLE 
    WHERE WHERECONDITION = 'WHERECONDITION'
) ORDER BY A
```

## Join and Alias
input
```json
{
  "CRUD": ["SELECT"],
  "COLUMN": [
    "A.a",
    "C.b",
    "E.c"
  ],
  "JOIN 1": ["LEFT OUTER JOIN table_resource C ON A.select_id = C.select_id"],
  "JOIN 2": [
    "INNER JOIN item D ON A.id = D.id",
    "INNER JOIN table_item E ON D.c = E.c"
  ],
  "JOIN ALIAS 1": ["C"],
  "JOIN ALIAS 2": [
    "D",
    "E"
  ],
  "TABLE": ["(SELECT A.a, A.select_id, B.id FROM table A INNER JOIN joinTable B ON A.id = B.id INNER JOIN joinTable2 C ON B.id2 = C.id2 WHERE A.yn = 'Y' AND C.id2 = 'id' AND A.select_id = (SELECT select_id FROM selector_table WHERE c_name = 'con_name' AND gateway = 'gateway' AND CONTAINER = 'container')) A"],
  "TABLE ALIAS": ["A"],
  "TABLE SUB QUERY 1": ["(SELECT A.a, A.select_id, B.id FROM table A INNER JOIN joinTable B ON A.id = B.id INNER JOIN joinTable2 C ON B.id2 = C.id2 WHERE A.yn = 'Y' AND C.id2 = 'id' AND A.select_id = (SELECT select_id FROM selector_table WHERE c_name = 'con_name' AND gateway = 'gateway' AND CONTAINER = 'container'))"],
  "TABLE SUB QUERY ANALYSE 1": {
    "CRUD": ["SELECT"],
    "COLUMN": [
      "A.a",
      "A.select_id",
      "B.id"
    ],
    "JOIN 1": ["INNER JOIN joinTable B ON A.id = B.id"],
    "JOIN 2": ["INNER JOIN joinTable2 C ON B.id2 = C.id2"],
    "JOIN ALIAS 1": ["B"],
    "JOIN ALIAS 2": ["C"],
    "TABLE": ["table A"],
    "TABLE ALIAS": ["A"],
    "WHERE": ["A.yn = 'Y' AND C.id2 = 'id' AND A.select_id = (SELECT select_id FROM selector_table WHERE c_name = 'con_name' AND gateway = 'gateway' AND CONTAINER = 'container')"],
    "WHERE SUB QUERY 1": ["(SELECT select_id FROM selector_table WHERE c_name = 'con_name' AND gateway = 'gateway' AND CONTAINER = 'container')"],
    "WHERE SUB QUERY ANALYSE 1": {
      "CRUD": ["SELECT"],
      "COLUMN": ["select_id"],
      "TABLE": ["selector_table"],
      "WHERE": ["c_name = 'con_name' AND gateway = 'gateway' AND CONTAINER = 'container'"]
    }
  }
}
```

output
```sql
SELECT A.a, C.b, E.c 
FROM 
(
    SELECT A.a, A.select_id, B.id 
    FROM 
    table A 
    INNER JOIN joinTable B ON A.id = B.id 
    INNER JOIN joinTable2 C ON B.id2 = C.id2 
    WHERE A.yn = 'Y' AND 
    C.id2 = 'id' AND 
    A.select_id = 
    (
        SELECT select_id 
        FROM selector_table 
        WHERE c_name = 'con_name' AND 
        gateway = 'gateway' AND 
        CONTAINER = 'container'
    )
) A 
LEFT OUTER JOIN table_resource C ON A.select_id = C.select_id 
INNER JOIN item D ON A.id = D.id 
INNER JOIN table_item E ON D.c = E.c
```

## Union
input
```json
{
  "CRUD": ["SELECT"],
  "COLUMN": ["*"],
  "TABLE": ["(SELECT A, B, C FROM SUBQUERYTABLE WHERE CONDITION IN ('A', 'B', 'C') AND CONDITION IN (SELECT CONDITION FROM ANOTHER UNION SELECT CONDITION FROM UNIONTABLE))"],
  "TABLE SUB QUERY 1": ["(SELECT A, B, C FROM SUBQUERYTABLE WHERE CONDITION IN ('A', 'B', 'C') AND CONDITION IN (SELECT CONDITION FROM ANOTHER UNION SELECT CONDITION FROM UNIONTABLE))"],
  "TABLE SUB QUERY ANALYSE 1": {
    "CRUD": ["SELECT"],
    "COLUMN": [
      "A",
      "B",
      "C"
    ],
    "TABLE": ["SUBQUERYTABLE"],
    "WHERE": ["CONDITION IN ('A', 'B', 'C') AND CONDITION IN (SELECT CONDITION FROM ANOTHER UNION SELECT CONDITION FROM UNIONTABLE)"],
    "WHERE SUB QUERY 1": ["(SELECT CONDITION FROM ANOTHER UNION SELECT CONDITION FROM UNIONTABLE)"],
    "WHERE SUB QUERY ANALYSE 1": {
      "CRUD": ["SELECT"],
      "COLUMN": ["CONDITION"],
      "TABLE": ["ANOTHER"],
      "UNION 1": ["SELECT CONDITION FROM UNIONTABLE"],
      "UNION ANALYSE 1": {
        "CRUD": ["SELECT"],
        "COLUMN": ["CONDITION"],
        "TABLE": ["UNIONTABLE"]
      }
    }
  },
  "UNION ALL 1": ["SELECT DISTINCT VAL FROM ((SELECT FIELD1 AS VAL FROM TABLE1 WHERE CONDITION1 = 'CONDITION1') UNION ALL (SELECT FIELD2 FROM TABLE1 WHERE CONDITION2 = 'CONDITION2') UNION ALL (SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3') UNION ALL (SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3')) T"],
  "UNION ALL ANALYSE 1": {
    "CRUD": ["SELECT"],
    "DISTINCT": ["TRUE"],
    "COLUMN": ["VAL"],
    "TABLE": ["((SELECT FIELD1 AS VAL FROM TABLE1 WHERE CONDITION1 = 'CONDITION1') UNION ALL (SELECT FIELD2 FROM TABLE1 WHERE CONDITION2 = 'CONDITION2') UNION ALL (SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3') UNION ALL (SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3')) T"],
    "TABLE ALIAS": ["T"],
    "TABLE SUB QUERY 1": ["((SELECT FIELD1 AS VAL FROM TABLE1 WHERE CONDITION1 = 'CONDITION1') UNION ALL (SELECT FIELD2 FROM TABLE1 WHERE CONDITION2 = 'CONDITION2') UNION ALL (SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3') UNION ALL (SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3'))"],
    "TABLE SUB QUERY ANALYSE 1": {
      "CRUD": ["SELECT"],
      "COLUMN": ["FIELD1 AS VAL"],
      "TABLE": ["TABLE1"],
      "UNION ALL 1": ["SELECT FIELD2 FROM TABLE1 WHERE CONDITION2 = 'CONDITION2'"],
      "UNION ALL 2": ["SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3'"],
      "UNION ALL 3": ["SELECT FIELD3 FROM TABLE3 WHERE CONDITION3 = 'CONDITION3'"],
      "UNION ALL ANALYSE 1": {
        "CRUD": ["SELECT"],
        "COLUMN": ["FIELD2"],
        "TABLE": ["TABLE1"],
        "WHERE": ["CONDITION2 = 'CONDITION2'"]
      },
      "UNION ALL ANALYSE 2": {
        "CRUD": ["SELECT"],
        "COLUMN": ["FIELD3"],
        "TABLE": ["TABLE3"],
        "WHERE": ["CONDITION3 = 'CONDITION3'"]
      },
      "UNION ALL ANALYSE 3": {
        "CRUD": ["SELECT"],
        "COLUMN": ["FIELD3"],
        "TABLE": ["TABLE3"],
        "WHERE": ["CONDITION3 = 'CONDITION3'"]
      },
      "WHERE": ["CONDITION1 = 'CONDITION1'"]
    }
  }
}
```

output
```sql
(
    SELECT *
    FROM
    (
        SELECT A, B, C
        FROM SUBQUERYTABLE
        WHERE CONDITION IN ('A', 'B', 'C')
        AND CONDITION IN
        (
            SELECT CONDITION
            FROM ANOTHER
            UNION
            SELECT CONDITION
            FROM UNIONTABLE
        )
    )
)
UNION ALL
(
    SELECT DISTINCT VAL
    FROM
    (
        (
            SELECT FIELD1 AS VAL
            FROM TABLE1
            WHERE CONDITION1 = 'CONDITION1'
        )
        UNION ALL
        (
            SELECT FIELD2
            FROM TABLE1
            WHERE CONDITION2 = 'CONDITION2'
        )
        UNION ALL
        (
            SELECT FIELD3
            FROM TABLE3
            WHERE CONDITION3 = 'CONDITION3'
        )
        UNION ALL
        (
            SELECT FIELD3
            FROM TABLE3
            WHERE CONDITION3 = 'CONDITION3'
        )
    ) T
)
```
