package necibook.com.dolphinscheduler.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.pojo.LocalParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/**
 * @author mujp
 */
public class ParamsUtil {

    protected static final Log logger = LogFactory.getLog(ParamsUtil.class);

    /**
     * 根绝--分割符解析args参数
     * @param execCommand
     * @return 键值对类型
     */
    public HashMap<String, String> parseCommand(String execCommand) {
        String[] commandSplit = execCommand.split("--");
        HashMap<String, String> paramDict = new HashMap<>(16);
        for (int i = 1; i < commandSplit.length; i++) {
            String paramStr = commandSplit[i];
            String[] paramSplit = paramStr.split(" ", 2);
            String key = paramSplit[0].trim();
            if (paramSplit.length == 2) {
                String value = paramSplit[1].trim();
                paramDict.put(key, value);
            } else {
                paramDict.put(key, null);
            }
        }
        return paramDict;
    }

    /**
     * 根据文件路径读取json文件
     * @param filePath
     * @return
     */
    public JSONObject readJsonFile(String filePath) {
        logger.info("【readJsonFile】: 读取json文件路径为：" + filePath);
        try {
            File jsonFile = new File(filePath);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            return JSONObject.parseObject(sb.toString());
        } catch (IOException e) {
            logger.error("读取json文件失败：" + e);
            return null;
        }
    }

    public static String getVariables(List<LocalParams> li) {
        StringBuilder builder = new StringBuilder(20);
        for (LocalParams localParams : li) {
            builder.append(" ${").append(localParams.getProp()).append("}");
        }
        return builder.toString();
    }

    public static String getLocalParams(List<LocalParams> li) {
        StringBuilder builder = new StringBuilder(20);
        String resultParams = null;
        boolean flag = true;
        for (LocalParams localParams : li) {
            Object json = JSONArray.toJSON(localParams);
            if (flag) {
                builder.append(json);
                flag = false;
            } else {
                builder.append(",").append(json);
            }
        }
        resultParams = "[" + builder.toString() + "]";
        return resultParams;
    }
}
