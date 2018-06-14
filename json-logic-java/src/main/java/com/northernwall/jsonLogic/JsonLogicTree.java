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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * JsonLogicTree is a reusable representation of a 'JsonLogic' expression.
 *
 * @author Richard
 */
public class JsonLogicTree {

    private Node node;
    private final Gson gson;

    JsonLogicTree(Node node, Gson gson) {
        this.node = node;
        this.gson = gson;
    }

    /**
     * Applies the value of data to the rules described in the tree to produce a
     * result.
     *
     * @param data A String containing JSON
     * @return
     */
    public Result evaluate(String data) throws ParseException, EvaluationException {
        return node.eval(convertData(data));
    }

    private Map<String, Result> convertData(String data) throws ParseException {
        Map<String, Result> temp = new HashMap<>();
        /////////////////Map<String, Result> temp = new TreeMap<>();
        if (data == null || data.isEmpty()) {
            return temp;
        }

        try {
            JsonReader jsonReader = gson.newJsonReader(new StringReader(data));
            jsonReader.beginObject();
            JsonToken token = jsonReader.peek();
            while (token != JsonToken.END_OBJECT) {
                String name = jsonReader.nextName();
                readValue(name, jsonReader, temp);
                token = jsonReader.peek();
            }
            jsonReader.endObject();
        } catch (IOException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
        return temp;
    }

    private void readValue(String name, JsonReader jsonReader, Map<String, Result> temp) throws IOException {
        JsonToken token = jsonReader.peek();
        switch (token) {
            case NUMBER:
                temp.put(name, new Result(jsonReader.nextDouble()));
                return;
            case BOOLEAN:
                temp.put(name, new Result(jsonReader.nextBoolean()));
                return;
            case STRING:
                temp.put(name, new Result(jsonReader.nextString()));
                return;
            case BEGIN_OBJECT:
                jsonReader.beginObject();
                token = jsonReader.peek();
                while (token != JsonToken.END_OBJECT) {
                    String subName = jsonReader.nextName();
                    readValue(name + "." + subName, jsonReader, temp);
                    token = jsonReader.peek();
                }
                jsonReader.endObject();
                return;
            case BEGIN_ARRAY:
                jsonReader.beginArray();

                LinkedHashMap<String,Result> lolo=new LinkedHashMap();
                int counter=0;
                token = jsonReader.peek();

                while (token != JsonToken.END_ARRAY) {
                    readValue(name+".$"+counter,jsonReader,lolo);
                    counter++;
                    token = jsonReader.peek();
                }

                boolean isJSONObject=true;

                for(Map.Entry<String,Result> entry:lolo.entrySet())
                    if (entry.getKey().equals(name + ".$0")) {
                        isJSONObject=false;
                        break;
                    }

                if(isJSONObject){
                    JsonArray doomed=new JsonArray();

                    for(int i=0;i<counter;i++)
                    {
                        JsonObject jj=new JsonObject();
                        for(Map.Entry<String,Result> entry:lolo.entrySet())
                        {
                            if(entry.getKey().startsWith(name+".$"+i))
                            {
                                String fieldName=entry.getKey().replaceAll("^"+name+"\\.\\$"+i+"\\.","");

                                if(entry.getValue().isString())
                                    jj.addProperty(fieldName,entry.getValue().getStringValue());
                                else if(entry.getValue().isDouble())
                                    jj.addProperty(fieldName,entry.getValue().getDoubleValue());
                                else if(entry.getValue().isBoolean())
                                    jj.addProperty(fieldName,entry.getValue().getBooleanValue());
                            }
                        }

                        doomed.add(jj);
                    }

                    jsonReader.endArray();
                    temp.put(name,new Result(doomed));
                    return;

                }else {
                    JsonArray doomed=new JsonArray();

                    for(Map.Entry<String,Result> entry:lolo.entrySet())
                    {
                        if(entry.getValue().isString())
                            doomed.add(entry.getValue().getStringValue());
                        else if(entry.getValue().isDouble())
                            doomed.add(entry.getValue().getDoubleValue());
                        else if(entry.getValue().isBoolean())
                            doomed.add(entry.getValue().getBooleanValue());
                    }

                    jsonReader.endArray();
                    temp.put(name,new Result(doomed));
                    return;
                }


        }
    }

    /**
     * This method tries to reduces the complexity of the tree by pruning
     * sub-trees that produce a constant value regardless of the variable data.
     */
    public void reduce() throws EvaluationException {
        if (node.isConstant()) {
            node = new ConstantNode(node.eval(new HashMap<>()));
        } else {
            node.reduce();
        }
    }

    /**
     * Produces the human readable text equivalent of the rules.
     *
     * @return
     */
    public String treeToString() {
        StringBuilder builder = new StringBuilder();
        node.treeToString(builder);
        return builder.toString();
    }

}
