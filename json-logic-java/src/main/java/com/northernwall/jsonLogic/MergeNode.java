/*
 * Copyright 2017 Richard.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.northernwall.jsonLogic;

import com.google.gson.JsonArray;

import java.util.Map;

/**
 *
 * @author Richard
 */
class MergeNode extends MultiNode {

    MergeNode(Node left, Node right) {
        super(left, right, " merge ");
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException {

        JsonArray mergedArray=new JsonArray();

        for (Node node : nodes)
        {
            Result result = node.eval(data);
            if (result.isString())
                mergedArray.add(result.getStringValue());
            else if(result.isDouble())
                mergedArray.add(result.getDoubleValue());
            else if(result.isBoolean())
                mergedArray.add(result.getBooleanValue());
            else if(result.isArray())
                unwind(mergedArray,result.getArrayValue());

        }

        return new Result(mergedArray);
    }

    @Override
    boolean isConstant() {
        return false;
    }

    private void unwind(JsonArray total,JsonArray temp){

        for(int i=0;i<temp.size();i++)
            if(temp.get(i) instanceof JsonArray)
                unwind(total,(JsonArray) temp.get(i));
            else
                total.add(temp.get(i));
    }


}
