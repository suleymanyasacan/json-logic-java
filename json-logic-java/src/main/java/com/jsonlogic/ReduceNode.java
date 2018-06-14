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

import java.util.Map;

/**
 *
 * @author Richard
 */
class ReduceNode extends MultiNode {

    ReduceNode(Node left, Node right) {
        super(left, right, "reduce");
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException{

        if(nodes.size()<2||nodes.size()>3)
            throw new EvaluationException("too many or too few elements in reduce");
        Result leftResult = nodes.get(0).eval(data);

        if (!leftResult.isArray())
            throw new EvaluationException("");

        StringBuilder sb=new StringBuilder();

        nodes.get(1).treeToString(sb);

        if(!sb.toString().contains("{\"var\":\"current\"}")||!sb.toString().contains("{\"var\":\"accumulator\"}"))
            throw new EvaluationException("reduce operation missing current or accumulator");

        Double accumulator=0.0;

        if(nodes.size()==3)
            if(!nodes.get(2).eval(data).isDouble())
                throw new EvaluationException("accumulator initial value must be numeric");
            else
                accumulator=nodes.get(2).eval(data).getDoubleValue();

        for(int i=0;i<leftResult.getArrayValue().size();i++)
        {
            String afterReplace=sb.toString()
                    .replace("{\"var\":\"current\"}",leftResult.getArrayValue().get(i).getAsDouble()+"")
                    .replace("{\"var\":\"accumulator\"}",accumulator+"")
                    ;

            try {
                Result r=new JsonLogic().apply(afterReplace,"");

                if(!r.isDouble())
                    throw new EvaluationException("no double value on map result");

                accumulator= r.getDoubleValue();

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return new Result(accumulator);
    }

    @Override
    boolean isConstant() {
        return false;
    }

}
