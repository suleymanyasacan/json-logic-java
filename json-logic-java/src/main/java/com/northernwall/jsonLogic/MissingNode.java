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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Richard
 */
class MissingNode extends MultiNode {

    MissingNode(Node left, Node right) {
        super(left, right, " missing ");
    }

    MissingNode() {
        super(null, null, " merge ");

        nodes=new ArrayList<>();
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException {

        JsonArray missingOnes=new JsonArray();

        for (Node node : nodes)
        {
            Result result = node.eval(data);
            if (result.isString())
            {
                if(!data.containsKey(result.getStringValue()))
                    missingOnes.add(result.getStringValue());
            }
            else if(result.isArray())
            {
                for(int i=0;i<result.getArrayValue().size();i++)
                    if(!data.containsKey(result.getArrayValue().get(i)))
                        missingOnes.add(result.getArrayValue().get(i));
            }
            else
                throw new EvaluationException("ffff");




        }

        return new Result(missingOnes);
    }

    @Override
    boolean isConstant() {
        return false;
    }




}
