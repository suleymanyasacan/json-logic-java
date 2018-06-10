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

import com.google.gson.JsonObject;

import java.util.Map;

/**
 *
 * @author Richard
 */
class NoneNode extends BinaryNode {

    NoneNode(Node left, Node right) {
        super(left, right, "none");
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException{
        Result leftResult = left.eval(data);

        if (!leftResult.isArray())
            throw new EvaluationException("");

        StringBuilder sb=new StringBuilder();

        right.treeToString(sb);

        if(sb.toString().contains("{\"var\":\"\"}"))
        {
            for(int i=0;i<leftResult.getArrayValue().size();i++)
            {
                String afterReplace=sb.toString().replace("{\"var\":\"\"}",leftResult.getArrayValue().get(i).getAsString());

                try {

                    Result r=new JsonLogic().apply(afterReplace,"");

                    if(r.getBooleanValue())
                        return new Result(false);

                }catch (Exception ex){
                    ex.printStackTrace();

                }


            }

            return new Result(true);
        }else {
            for(int i=0;i<leftResult.getArrayValue().size();i++)
            {
                try {
                    JsonObject jojo=leftResult.getArrayValue().get(i).getAsJsonObject();

                    Result r=new JsonLogic().apply(sb.toString(),jojo.toString());

                    if(r.getBooleanValue())
                        return new Result(false);

                }catch (Exception ex){
                    ex.printStackTrace();

                }

            }

            return new Result(true);
        }
    }

    @Override
    boolean isConstant() {
        return false;
    }

}
