package com.remote.diagnosis.web.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonUtil
{
  private static SerializeConfig mapping = new SerializeConfig();

  private static String dateFormat = "yyyy-MM-dd HH:mm:ss";

  public static String toJson(Object obj)
  {
    return JSON.toJSONString(obj, new SerializerFeature[] { SerializerFeature.WriteDateUseDateFormat });
  }

  public static String toJson(Object obj, String dateFormat)
  {
    mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
    return JSON.toJSONString(obj, mapping, new SerializerFeature[0]);
  }

  public static Map toMap(String text)
    throws JSONException
  {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    return (Map)JSON.parseObject(text, Map.class);
  }

  public static List toList(String text)
    throws JSONException
  {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    return (List)JSON.parseObject(text, List.class);
  }

  public static <T> List<T> toList(String text, Class<T> clazz)
    throws JSONException
  {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    return JSON.parseArray(text, clazz);
  }

  public static <T> T toBean(String text, Class<T> clazz)
    throws JSONException
  {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    return JSON.parseObject(text, clazz);
  }

  public static <T> T fromJSON(String text, Class<T> tClass)
  {
    return JSON.parseObject(text, tClass);
  }

  public static JSONObject toJsonObject(String text)
    throws JSONException
  {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    return JSON.parseObject(text);
  }

  public static Object getObjectFromJson(String text, String[] keys)
    throws JSONException
  {
    if (keys.length <= 0) {
      return text;
    }
    String key = keys[0];
    Object obj = null;
    if ((text.startsWith("[")) && (StringUtils.isNumeric(key))) {
      JSONArray jsonArray = JSON.parseArray(text);
      obj = jsonArray.get(Integer.valueOf(key).intValue());
    } else {
      JSONObject json = toJsonObject(text);

      obj = json.get(key);
    }
    if (obj == null)
    {
      return null;
    }
    return getObjectFromJson(obj.toString(), (String[])ArrayUtils.subarray(keys, 1, keys.length));
  }

  public static <T> T getFromJson(String text, String key, Class<T> t)
    throws JSONException
  {
    return (T) getObjectFromJson(text, new String[] { key });
  }
}
