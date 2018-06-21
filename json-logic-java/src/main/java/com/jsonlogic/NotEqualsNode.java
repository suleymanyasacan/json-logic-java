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
class NotEqualsNode extends BinaryNode {

    NotEqualsNode(Node left, Node right) {
        super(left, right, "!=");
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException {
        Result leftResult = left.eval(data);
        Result rightResult = right.eval(data);
        
        
        if(leftResult.isNull())
            return new Result(!rightResult.isNull());
        else if(rightResult.isNull())
            return new Result(!leftResult.isNull());
        
        
        if(rightResult.isBoolean() && !rightResult.getBooleanValue())
            return new Result(false);
        
        if (leftResult.isBoolean() && rightResult.isBoolean()) {
            return new Result(leftResult.getBooleanValue() != rightResult.getBooleanValue());
        }
        if (leftResult.isDouble() && rightResult.isDouble()) {
            return new Result(leftResult.getDoubleValue() != rightResult.getDoubleValue());
        }

        if(leftResult.isDouble()&&rightResult.isString())
        {
            Double temp=new Double(rightResult.getStringValue());

            return new Result(!leftResult.getDoubleValue().equals(temp));
        }

        if(rightResult.isDouble()&&leftResult.isString())
        {
            Double temp=new Double(leftResult.getStringValue());

            return new Result(!rightResult.getDoubleValue().equals(temp));
        }
        
        if(leftResult.isString())
            return new Result(!leftResult.getStringValue().equals(rightResult.getStringValue()));

        return null;
    }

    @Override
    boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }
    
}
