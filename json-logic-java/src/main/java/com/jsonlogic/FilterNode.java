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
package com.jsonlogic;

import com.google.gson.JsonArray;

import java.util.Map;

/**
 *
 * @author Richard
 */
class FilterNode extends BinaryNode {

    FilterNode(Node left, Node right) {
        super(left, right, "filter");
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException{
        Result leftResult = left.eval(data);

        if (!leftResult.isArray())
            throw new EvaluationException("");

        StringBuilder sb=new StringBuilder();

        right.treeToString(sb);

        
        JsonArray results=new JsonArray();

        for(int i=0;i<leftResult.getArrayValue().size();i++)
        {
            //String afterReplace=sb.toString().replace("{\"var\":\"\"}",leftResult.getArrayValue().get(i).getAsDouble()+"");

            try {
                Result r=new JsonLogic().apply(sb.toString(),leftResult.getArrayValue().get(i).toString());

                if(r.getBooleanValue())
                    results.add(leftResult.getArrayValue().get(i));

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return new Result(results);
        

    }

    @Override
    boolean isConstant() {
        return false;
    }

}
