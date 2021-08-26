package necibook.com.dolphinscheduler.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mujp
 */
public class JsonUtil {
    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.*?)}");
    public static Map<String,String> json2Str(String json){
        Map<String,String> map = new HashMap<>(6);
        JSONObject jsonObj= JSONObject.parseObject(json);
        JSONArray parse = JSONObject.parseArray(jsonObj.get("tasks").toString());
        Object params = JSONObject.parseObject(parse.get(0).toString()).get("params");
        String rawScript = JSONObject.parseObject(params.toString()).get("rawScript").toString();
        Matcher matcher = PATTERN.matcher(rawScript);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            buffer.append(" ").append(matcher.group(0));
        }
        String localParams = JSONObject.parseObject(params.toString()).get("localParams").toString();
        map.put("localParams",localParams);
        map.put("rawScript",buffer.toString());
        return map;
    }

}
