/*
 * Copyright 2017 Richard Thurston.
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.StringReader;

/**
 * JsonLogic is used to parse and evaluate 'JsonLogic' expressions.
 *
 * @author Richard
 */
public class JsonLogic {

    static final Result TRUE_RESULT = new Result(true);
    static final Result FALSE_RESULT = new Result(false);
    static final ConstantNode TRUE_NODE = new ConstantNode(TRUE_RESULT);
    static final ConstantNode FALSE_NODE = new ConstantNode(FALSE_RESULT);

    private final Gson gson;

    public JsonLogic() {
        gson = new Gson();
    }

    /**
     * Equivalent to "parse(rule).evaluate(data);"
     *
     * @param rule A String containing a JsonLogic expression
     * @param data A String containing JSON
     * @return
     * @throws com.northernwall.jsonLogic.ParseException
     */
    public Result apply(String rule, String data) throws ParseException, EvaluationException {
        return parse(rule).evaluate(data);
    }

    /**
     * Parses the rules into a reusable tree which can be evaluated many times.
     *
     * @param rule A String containing a JsonLogic expression
     * @return
     * @throws com.northernwall.jsonLogic.ParseException
     */
    public JsonLogicTree parse(String rule) throws ParseException {
        return new JsonLogicTree(parse(gson.newJsonReader(new StringReader(rule))), gson);
    }

    private Node parse(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {
            JsonToken token = jsonReader.peek();
            switch (token) {
                case BEGIN_OBJECT:
                    jsonReader.beginObject();
                    String operation = jsonReader.nextName();
                    switch (operation) {
                        case "==":
                            tree = parseEquals(jsonReader);
                            break;
                        case "===":
                            tree = parseStrictEquals(jsonReader);
                            break;
                        case "!=":
                            tree = parseNotEquals(jsonReader);
                            break;
                        case "!==":
                            tree = parseStrictNotEquals(jsonReader);
                            break;
                        case ">":
                            tree = parseGreaterThan(jsonReader);
                            break;
                        case ">=":
                            tree = parseGreaterThanEquals(jsonReader);
                            break;
                        case "<":
                            tree = parseLessThan(jsonReader);
                            break;
                        case "<=":
                            tree = parseLessThanEquals(jsonReader);
                            break;
                        case "and":
                            tree = parseAnd(jsonReader);
                            break;
                        case "or":
                            tree = parseOr(jsonReader);
                            break;
                        case "!":
                            tree = parseNot(jsonReader);
                            break;
                        case "if":
                            tree = parseIf(jsonReader);
                            break;
                        case "max":
                            tree = parseMax(jsonReader);
                            break;
                        case "min":
                            tree = parseMin(jsonReader);
                            break;
                        case "+":
                            tree = parseAddition(jsonReader);
                            break;
                        case "*":
                            tree = parseMultiplication(jsonReader);
                            break;
                        case "-":
                            tree = parseSubtraction(jsonReader);
                            break;
                        case "/":
                            tree = parseDivision(jsonReader);
                            break;
                        case "%":
                            tree = parseModulo(jsonReader);
                            break;
                        case "var":
                            tree = parseVar(jsonReader);
                            break;
                        case "missing":
                            tree = parseMissing(jsonReader);
                            break;
                        case "missing_some":
                            tree = parseMissingSome(jsonReader);
                            break;
                        case "merge":
                            tree = parseMerge(jsonReader);
                            break;
                        case "cat":
                            tree = parseCat(jsonReader);
                            break;
                        case "in":
                            tree = parseIn(jsonReader);
                            break;
                        case "all":
                            tree = parseAll(jsonReader);
                            break;
                        case "some":
                            tree = parseSome(jsonReader);
                            break;
                        case "map":
                            tree = parseMap(jsonReader);
                            break;
                        case "filter":
                            tree = parseFilter(jsonReader);
                            break;
                        case "reduce":
                            tree = parseReduce(jsonReader);
                            break;
                        case "none":
                            tree = parseNone(jsonReader);
                            break;
                        case "substr":
                            tree = parseSubstr(jsonReader);
                            break;
                        case "log":
                            tree = parseLog(jsonReader);
                            break;
                        default:
                            throw new ParseException("Unknown operation '" + operation + "'");
                    }
                    jsonReader.endObject();
                    break;
                case NUMBER:
                    tree = new ConstantNode(new Result(jsonReader.nextLong()));
                    break;
                case STRING:
                    tree = new ConstantNode(new Result(jsonReader.nextString()));
                    break;
                case BOOLEAN:
                    if (jsonReader.nextBoolean()) {
                        tree = TRUE_NODE;
                    } else {
                        tree = FALSE_NODE;
                    }
                    break;
                case BEGIN_ARRAY:
                    jsonReader.beginArray();
                    tree = parseArrayNode(jsonReader);
                    jsonReader.endArray();

            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseEquals(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new EqualsNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseStrictEquals(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new StrictEqualsNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseStrictNotEquals(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new StrictNotEqualsNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseNotEquals(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new NotEqualsNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseGreaterThan(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {
            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new GreaterThanNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseGreaterThanEquals(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {
            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new GreaterThanEqualsNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseLessThan(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {
            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();

                Node node1=parse(jsonReader);
                Node node2=parse(jsonReader);

                tree = new LessThanNode(node1, node2);
                try {
                    jsonReader.endArray();
                }catch (IllegalStateException e){
                    Node node3=parse(jsonReader);

                    jsonReader.endArray();

                    tree=new BetweenNode(node1,node2,node3);
                }

            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseLessThanEquals(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {
            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();

                Node node1=parse(jsonReader);
                Node node2=parse(jsonReader);

                tree = new LessThanEqualsNode(node1, node2);
                try {
                    jsonReader.endArray();
                }catch (IllegalStateException e){
                    Node node3=parse(jsonReader);

                    jsonReader.endArray();

                    tree=new BetweenEqualsLeftNode(node1,node2,node3);
                }



            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseAnd(JsonReader jsonReader) throws ParseException {
        AndNode andNode = null;
        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        andNode = new AndNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            andNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        break;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return andNode;
    }

    private Node parseOr(JsonReader jsonReader) throws ParseException {
        OrNode orNode = null;
        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        orNode = new OrNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            orNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        break;
                    default:
                        throw new ParseException("Expecting an array of two boolean expressions");
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return orNode;
    }

    private Node parseNot(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        tree = new NotNode(parse(jsonReader));
                        jsonReader.endArray();
                        break;
                    case BOOLEAN:
                        tree = new NotNode(parse(jsonReader));
                        break;
                    default:
                        throw new ParseException("Expecting a boolean or an array of one boolean");
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseIf(JsonReader jsonReader) throws ParseException {
        IfNode ifNode = null;
        try {
            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                ifNode = new IfNode(parse(jsonReader), parse(jsonReader), parse(jsonReader));
                JsonToken a=jsonReader.peek();
                while (jsonReader.peek() != JsonToken.END_ARRAY) {
                    ifNode.addConditionNode(parse(jsonReader), parse(jsonReader));
                }
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return ifNode;
    }

    private Node parseMax(JsonReader jsonReader) throws ParseException {
        MaxNode maxNode = null;
        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        maxNode = new MaxNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            maxNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        break;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return maxNode;
    }

    private Node parseMin(JsonReader jsonReader) throws ParseException {
        MinNode node = null;
        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        node = new MinNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            node.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        break;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return node;
    }

    private Node parseAddition(JsonReader jsonReader) throws ParseException {

        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        AdditionNode additionNode=null;
                        jsonReader.beginArray();
                        additionNode = new AdditionNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            additionNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        return additionNode;
                    case STRING:
                        return new AdditionCastingOverloadNode(parse(jsonReader));

//                        StringBuilder builder = new StringBuilder();
//                        node.treeToString(builder);
//                        System.out.println("Before: " + builder.toString());
//                        //Result result = node.eval(data);
//                        System.out.println("After: " + builder.toString());

                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return null;
    }


    private Node parseMultiplication(JsonReader jsonReader) throws ParseException {

        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        MultiplicationNode multiplicationNode = new MultiplicationNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            multiplicationNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        return multiplicationNode;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return null;
    }


    private Node parseSubtraction(JsonReader jsonReader) throws ParseException {

        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();

                        Node node1=parse(jsonReader);
                        Node node2=null;

                        try {
                            node2=parse(jsonReader);

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                        Node tree = null;
                        if(node2!=null)
                            tree= new SubtractionNode(node1, node2);
                        else
                            tree= new SubtractionNegatingOverloadNode(node1);

                        jsonReader.endArray();
                        return tree;


//                        SubtractionNode subtractionNode=null;
//                        jsonReader.beginArray();
//                        subtractionNode = new SubtractionNode(parse(jsonReader), parse(jsonReader));
//                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
//                            subtractionNode.add(parse(jsonReader));
//                        }
//                        jsonReader.endArray();
//                        return subtractionNode;
                    case STRING:
                        return new AdditionCastingOverloadNode(parse(jsonReader));
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return null;
    }

    private Node parseDivision(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new DivisionNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseModulo(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new ModuloNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }



    private Node parseVar(JsonReader jsonReader) throws ParseException {
        Node node = null;
        try {
            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                String name = jsonReader.nextString();
                token = jsonReader.peek();
                if (null != token) {
                    switch (token) {
                        case END_ARRAY:
                            node = new VarNode(name);
                            break;
                        case NUMBER:
                            node = new VarNode(name, new Result(jsonReader.nextLong()));
                            break;
                        case STRING:
                            node = new VarNode(name, new Result(jsonReader.nextString()));
                            break;
                        case BOOLEAN:
                            if (jsonReader.nextBoolean()) {
                                node = new VarNode(name, TRUE_RESULT);
                            } else {
                                node = new VarNode(name, FALSE_RESULT);
                            }
                            break;
                        default:
                            break;
                    }
                }
                jsonReader.endArray();
            } else if (token == JsonToken.STRING) {
                String name = jsonReader.nextString();
                node = new VarNode(name);
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return node;
    }

    private Node parseMissing(JsonReader jsonReader) throws ParseException {

        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        MissingNode missingNode=null;
                        jsonReader.beginArray();
                        missingNode = new MissingNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            missingNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        return missingNode;
                    case BEGIN_OBJECT:
                        missingNode =
                                new MissingNode();

                        missingNode.add(parse(jsonReader));
                        return missingNode;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return null;
    }

    private Node parseMissingSome(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new MissingSomeNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseMerge(JsonReader jsonReader) throws ParseException {

        MergeNode mergeNode=new MergeNode();

        try {
            jsonReader.beginArray();

            while (jsonReader.hasNext())
            {
                JsonToken token = jsonReader.peek();
                if (null != token)
                {
                    switch (token) {
                        case NUMBER:
                            mergeNode.add(new ConstantNode(new Result(jsonReader.nextDouble())));
                            break;
                        case STRING:
                            mergeNode.add(new ConstantNode(new Result(jsonReader.nextString())));
                            break;
                        case BEGIN_ARRAY:
                            unwind(mergeNode, jsonReader);
                            break;
                        case BEGIN_OBJECT:
                            mergeNode.add(parse(jsonReader));
                            break;
                    }
                }
            }

            jsonReader.endArray();
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }

        return mergeNode;
    }

    private void unwind(MergeNode mergeNode, JsonReader reader) {

        try {

            reader.beginArray();

            while(reader.hasNext()) {

                JsonToken peeked = reader.peek();

                if (reader.peek() == JsonToken.NUMBER)
                    mergeNode.add(new ConstantNode(new Result(reader.nextDouble())));
                else if (reader.peek() == JsonToken.STRING)
                    mergeNode.add(new ConstantNode(new Result(reader.nextString())));
                else if (reader.peek() == JsonToken.BOOLEAN)
                    mergeNode.add(new ConstantNode(new Result(reader.nextBoolean())));
                else if (reader.peek() == JsonToken.BEGIN_ARRAY)
                    unwind(mergeNode, reader);
            }

            reader.endArray();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Node parseArrayNode(JsonReader jsonReader) throws ParseException {

        ArrayNode arrayNode=new ArrayNode();
        try {
            while (jsonReader.peek() != JsonToken.END_ARRAY) {
                arrayNode.add(parse(jsonReader));
            }
            return arrayNode;
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
    }


    private Node parseCat(JsonReader jsonReader) throws ParseException {

        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        CatNode node=null;
                        jsonReader.beginArray();
                        node = new CatNode();
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            node.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        return node;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return null;
    }

    private Node parseIn(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new InNode(parse(jsonReader), parse(jsonReader));
                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseAll(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new AllNode(parse(jsonReader), parse(jsonReader));

                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseSome(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new SomeNode(parse(jsonReader), parse(jsonReader));

                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseMap(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new MapNode(parse(jsonReader), parse(jsonReader));

                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseFilter(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new FilterNode(parse(jsonReader), parse(jsonReader));

                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseReduce(JsonReader jsonReader) throws ParseException {
        try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        ReduceNode reduceNode = new ReduceNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            reduceNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        return reduceNode;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return null;
    }

    /*
    * try {
            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        MultiplicationNode multiplicationNode = new MultiplicationNode(parse(jsonReader), parse(jsonReader));
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            multiplicationNode.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        return multiplicationNode;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return null;
    *
    * */

    private Node parseNone(JsonReader jsonReader) throws ParseException {
        Node tree = null;
        try {

            JsonToken token = jsonReader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                tree = new NoneNode(parse(jsonReader), parse(jsonReader));

                jsonReader.endArray();
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return tree;
    }

    private Node parseSubstr(JsonReader jsonReader) throws ParseException {
        SubstrNode node=new SubstrNode();
        try {

            JsonToken token = jsonReader.peek();
            if (null != token) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        while (jsonReader.peek() != JsonToken.END_ARRAY) {
                            node.add(parse(jsonReader));
                        }
                        jsonReader.endArray();
                        return node;
                }
            }
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return node;
    }


    private Node parseLog(JsonReader jsonReader) throws ParseException {
        try {
            return new LogNode(parse(jsonReader));
       } catch (Exception ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
    }

}
