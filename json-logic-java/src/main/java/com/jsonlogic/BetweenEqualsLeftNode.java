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
class BetweenEqualsLeftNode extends BinaryNode {

    protected Node middle;

    BetweenEqualsLeftNode(Node left, Node middle, Node right) {
        super(left, right, "<=");
        this.middle=middle;
    }

    @Override
    boolean isConstant() {
        return left.isConstant() && right.isConstant()&&middle.isConstant();
    }

    @Override
    Result eval(Map<String, Result> data) throws EvaluationException {
        Result leftResult = left.eval(data);
        Result middleResult = middle.eval(data);
        Result rightResult = right.eval(data);
        if (leftResult.isDouble() && rightResult.isDouble()&&middleResult.isDouble()) {
            return new Result(leftResult.getDoubleValue() <= middleResult.getDoubleValue()
            &&middleResult.getDoubleValue()<rightResult.getDoubleValue());
        }
        return null;
    }


    @Override
    void reduce() throws EvaluationException {
        if (left.isConstant()) {
            if (!(left instanceof ConstantNode)) {
                left = new ConstantNode(left.eval(null));
            }
        }

        if (right.isConstant()) {
            if (!(right instanceof ConstantNode)) {
                right = new ConstantNode(right.eval(null));
            }
        }

        if (middle.isConstant()) {
            if (!(middle instanceof ConstantNode)) {
                middle = new ConstantNode(middle.eval(null));
            }
        }
    }



}
