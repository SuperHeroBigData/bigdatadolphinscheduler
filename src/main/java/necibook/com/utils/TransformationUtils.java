package necibook.com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName TransformationUtils
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/24 10:23 上午
 * @Version 1.0
 */
public class TransformationUtils {
    private static Pattern pattern = Pattern.compile("[A-Z]");
    /**
     * 对象反射解析
     *
     * @param clazz
     * @param jsonArray
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> List<T> jsonObject(Class<?> clazz, JSONArray jsonArray, Class<T> entity) throws IllegalAccessException, InstantiationException {
        List<T> storeListEntity = new ArrayList<>();
        JSONObject onedatainfojs = null;
        Field[] fields = clazz.getDeclaredFields();
        Object obj = clazz.newInstance();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject oneDataInfo = jsonArray.getJSONObject(i);
            onedatainfojs = new JSONObject();
            for (Field field : fields) {
                if ("serialVersionUID".contains(field.getName())) {
                    continue;
                }
                String name = field.getName();
                field.setAccessible(true);
                String resultValue = (String) field.get(obj);
                onedatainfojs.put(name, oneDataInfo.get(resultValue));
            }
            T entt =  JSONObject.toJavaObject(onedatainfojs,entity);
            storeListEntity.add(entt);
        }
        return storeListEntity;
    }

    /**
     * 对象转Map
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>(10);
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * map转对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 在大写字母前加 _ （驼峰转下划线）
     *
     * @param name 字段名称
     * @return 转换后的字段名称
     */
    public static String underline(String name) {
        StringBuffer sb = underline(new StringBuffer(name));
        return sb.toString();
    }

    private static StringBuffer underline(StringBuffer str) {
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return underline(sb);
    }

    /**
     * 对象字段转下划线
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String underline2(T t) {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSON.toJSONString(t, config, SerializerFeature.WriteMapNullValue);
    }

}
