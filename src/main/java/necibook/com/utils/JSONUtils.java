/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package necibook.com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/13 6:03 下午
 * @Version 1.0
 */
public class JSONUtils {

  private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JSONUtils() {
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setTimeZone(TimeZone.getDefault());
  }

  /**
   *
   * @param object
   * @return
   */
  public static String toJson(Object object) {
    try{
      return JSON.toJSONString(object,false);
    } catch (Exception e) {
      logger.error("json解析异常",e);

    }

    return null;
  }

  /**
   *
   * @param json
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T parseObject(String json, Class<T> clazz) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }

    try {
      return JSON.parseObject(json, clazz);
    } catch (Exception e) {
      logger.error("解析对象异常!",e);
    }
    return null;
  }


  /**
   *
   * @param json
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> List<T> toList(String json, Class<T> clazz) {
    if (StringUtils.isEmpty(json)) {
      return new ArrayList<>();
    }
    try {
      return JSONArray.parseArray(json, clazz);
    } catch (Exception e) {
      logger.error("JSONArray.parseArray 异常!",e);
    }

    return new ArrayList<>();
  }


  /**
   *
   * @param json
   * @return
   */
  public static boolean checkJsonValid(String json) {

    if (StringUtils.isEmpty(json)) {
      return false;
    }

    try {
      OBJECT_MAPPER.readTree(json);
      return true;
    } catch (IOException e) {
      logger.error("检查json对象是否有效异常!",e);
    }

    return false;
  }


  /**
   * 查看json中是否存在某一个字段
   * @param jsonNode
   * @param fieldName
   * @return
   */
  public static String findValue(JsonNode jsonNode, String fieldName) {
    JsonNode node = jsonNode.findValue(fieldName);

    if (node == null) {
      return null;
    }

    return node.toString();
  }


  /**
   *
   * @param json
   * @return
   */
  public static Map<String, String> toMap(String json) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }

    try {
      return JSON.parseObject(json, new TypeReference<HashMap<String, String>>(){});
    } catch (Exception e) {
      logger.error("json 转 map异常!",e);
    }

    return null;
  }

  /**
   *
   * @param json
   * @param classK
   * @param classV
   * @param <K>
   * @param <V>
   * @return
   */
  public static <K, V> Map<K, V> toMap(String json, Class<K> classK, Class<V> classV) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }

    try {
      return JSON.parseObject(json, new TypeReference<HashMap<K, V>>() {});
    } catch (Exception e) {
      logger.error("json 转 map异常!",e);
    }

    return null;
  }

  /**
   *
   * @param object
   * @return
   */
  public static String toJsonString(Object object) {
    try{
      return JSON.toJSONString(object,false);
    } catch (Exception e) {
      throw new RuntimeException("对象json序列化异常.", e);
    }
  }

  public static JSONObject parseObject(String text) {
    try{
      return JSON.parseObject(text);
    } catch (Exception e) {
      throw new RuntimeException("字符符json串反序列化异常.", e);
    }
  }

  public static JSONArray parseArray(String text) {
    try{
      return JSON.parseArray(text);
    } catch (Exception e) {
      throw new RuntimeException("json序列化异常.", e);
    }
  }



  public static class JsonDataSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeRawValue(value);
    }

  }

  public static class JsonDataDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      return node.toString();
    }

  }
}
