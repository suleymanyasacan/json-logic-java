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
import java.util.Map;

/**
 *
 * @author Richard
 */
class SubstrNode extends MultiNode {

    SubstrNode() {
        super(null, null, " substr ");

        nodes=new ArrayList<>();
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException {

        Result leftResult = nodes.get(0).eval(data);
        Result rightResult = nodes.get(1).eval(data);

        if(!leftResult.isString()||!rightResult.isDouble())
            throw new EvaluationException("");

        if(nodes.size()==2)
        {
            if(rightResult.getDoubleValue()>=0)
                return new Result(leftResult.getStringValue()
                        .substring(rightResult.getDoubleValue().intValue()));
            else
                return new Result(leftResult.getStringValue()
                        .substring(leftResult.getStringValue().length()+rightResult.getDoubleValue().intValue()));
        }else {

            Result thirdResult=nodes.get(2).eval(data);

            if(!thirdResult.isDouble())
                throw new EvaluationException("");

            return new Result(leftResult.getStringValue()
                    .substring(rightResult.getDoubleValue().intValue(),thirdResult.getDoubleValue().intValue()+1));
        }
    }
}
