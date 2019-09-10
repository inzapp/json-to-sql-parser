package com.inzapp.jsonToSqlParser.core.crudParser;

import com.inzapp.jsonToSqlParser.config.JsonKey;
import com.inzapp.jsonToSqlParser.core.Parser;
import com.inzapp.jsonToSqlParser.core.json.JsonManager;
import net.sf.jsqlparser.statement.select.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnionSelectParser extends JsonManager {
    private List<Boolean> brackets = new ArrayList<>();
    private List<SelectBody> selectBodies = new ArrayList<>();
    private List<SetOperation> setOperations = new ArrayList<>();

    public Select parse(JSONObject json) {
        injectJson(json);
        addMainJsonSelectBody();
        addUnionBodies();
        return convertSelect();
    }

    private void addMainJsonSelectBody() {
        // add select body of default json except union
        String mainJsonSelectBodySql = new Parser().parse(getJson(), true);
        SelectBody mainJsonSelectBody = new SelectBody() {
            @Override
            public void accept(SelectVisitor selectVisitor) {
                // empty
            }

            @Override
            public String toString() {
                return mainJsonSelectBodySql;
            }
        };
        this.selectBodies.add(mainJsonSelectBody);
    }

    private void addUnionBodies(){
        int idx = 1;
        while (true) {
            this.brackets.add(true);
            List<String> unions = getFromJson(JsonKey.UNION + idx);
            List<String> unionAlls = getFromJson(JsonKey.UNION_ALL + idx);
            if (unions == null && unionAlls == null)
                break;

            SetOperation setOperation = new SetOperation(SetOperationList.SetOperationType.UNION) {
                @Override
                public String toString() {
                    return unions != null ? JsonKey.UNION : JsonKey.UNION_ALL;
                }
            };

            SelectBody selectBody = new SelectBody() {
                @Override
                public void accept(SelectVisitor selectVisitor) {
                    // empty
                }

                @Override
                public String toString() {
                    return unions != null ? unions.get(0) : unionAlls.get(0);
                }
            };

            this.setOperations.add(setOperation);
            this.selectBodies.add(selectBody);
            ++idx;
        }
    }

    private Select convertSelect() {
        SetOperationList setOperationList = new SetOperationList();
        setOperationList.setBracketsOpsAndSelects(this.brackets, this.selectBodies, this.setOperations);
        Select select = new Select();
        select.setSelectBody(setOperationList);
        return select;
    }
}
