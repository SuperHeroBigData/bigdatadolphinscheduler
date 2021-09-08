package necibook.com.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import necibook.com.dolphinscheduler.exceptions.TasksException;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.enums.State;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * @Author Mujp
 * @Date: Create in 11:42 上午 2020/9/9
 * @Description：
 */
public class HttpUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 向企业微信发送信息
     *
     * @param message
     * @throws IOException
     */
    public static void sendDingDingTalkRisk(String message) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=-130c9652ae1c");
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setHeader("guest", "guest");
        JSONObject bodys = new JSONObject();
        bodys.put("msgtype", "text");
        JSONObject text = new JSONObject();
        String sendMessage = "";
        sendMessage = message;
        text.put("content", sendMessage);
        bodys.put("text", text);
        JSONObject at = new JSONObject();
        at.put("isAtAll", true);
        bodys.put("at", at);
        StringEntity se = new StringEntity(bodys.toJSONString(), "utf-8");
        httpPost.setEntity(se);
        httpClient.execute(httpPost);
    }

    /**
     * @throws Exception
     * @version 时间：2020年09月09日
     * 功能描述：通过微信机器人发送告警通知
     */
    public static void sendWeChatRobotTalkRisk(String message) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=d3e6de8c-3b56-4c00-84cd-130c9652ae1c");
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        JSONObject bodys = new JSONObject();
        bodys.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", message);
        text.put("mentioned_list", "@all");
        bodys.put("text", text);
        StringEntity se = new StringEntity(bodys.toJSONString(), "utf-8");
        httpPost.setEntity(se);
      //  httpClient.execute(httpPost);
    }
    /**
     * 执行结果报错发送告警
     *
     * @param result
     */
    public static void executeResult(Result result) {
        String js = JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue);
        if (State.ERROR.equals(State.valueOf(result.getState().toUpperCase()))) {
            LOG.error("执行结果报错:{}", js);
            String message = String.format("excel解析报错。报错时间：%s, 项目名：%s, 任务名：%s, 报错信息：%s",
                    DateUtil.formatDate(new Date()),result.getProjectName(),result.getJobName(),result.getMsg());
            //sendWeChatRobotTalkRisk(message);
            throw new TasksException("当前执行失败："+result);
        }
        LOG.info("执行结果：{}",js);
    }
    /**
     * 执行结果报错发送告警
     *
     * @param result
     */
    public static void executeResourceResult(Result result) {
        String js = JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue);
        if (State.ERROR.equals(State.valueOf(result.getState().toUpperCase()))) {
            LOG.error("执行结果报错:{}", js);
            String message = String.format("资源上传报错。报错时间：%s, 项目名：%s, 任务名：%s, 报错信息：%s",
                    DateUtil.formatDate(new Date()),result.getProjectName(),result.getJobName(),result.getMsg());
            //sendWeChatRobotTalkRisk(message);
            throw new TasksException("当前执行失败："+result);
        }
        LOG.info("执行结果：{}",js);
    }

}
