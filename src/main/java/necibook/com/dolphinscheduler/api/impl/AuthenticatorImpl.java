package necibook.com.dolphinscheduler.api.impl;

import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.api.Authenticator;
import necibook.com.dolphinscheduler.api.Constant;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.easyexcel.SheetEnv;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mujp
 */
public class AuthenticatorImpl implements Authenticator {
    protected static final Log LOGGER = LogFactory.getLog(AuthenticatorImpl.class);
    @Override
    public Result authenticate(SheetEnv login) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        Result result = new Result();
        result.setUserName(login.getUserName());
        result.setProjectName(login.getProjectName());
        String content = null;
        try {
            List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("userName", login.getUserName()));
            parameters.add(new BasicNameValuePair("userPassword", login.getPassword()));
            URI uri = new URIBuilder(Constant.URL_HEADER+login.getIp()+":"+login.getPort()+"/dolphinscheduler/login")
                    .setParameters(parameters)
                    .build();
            HttpPost httpGet = new HttpPost(uri);
            response = httpclient.execute(httpGet);
            LOGGER.info("???getLoginToken???????????????????????????" + response.getStatusLine().getStatusCode());
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
            LOGGER.info("???getLoginToken????????????????????????: " + content);
            String data = JSONObject.parseObject(content).getString("data");
            if (data != null ){
                result.setData(JSONObject.parseObject(data).getString("sessionId"));
            }
            result.setState(Constant.STATE_SUCCESS);
        } catch (ClientProtocolException e) {
            LOGGER.error("???getLoginToken?????????????????????????????????" + e);
            result.setMsg("???getLoginToken?????????????????????????????????" + e);
        } catch (IOException e) {
            LOGGER.error("???getLoginToken??????????????????IO?????????" + e);
            result.setMsg("???getLoginToken??????????????????IO?????????" + e);
        } catch (URISyntaxException e) {
            LOGGER.error("???getLoginToken??????????????????URI???????????????" + e);
            result.setMsg("???getLoginToken??????????????????URI???????????????" + e);
        } catch (Exception e) {
            LOGGER.error("???getLoginToken??????????????????" + e);
            result.setMsg("???getLoginToken??????????????????" + e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error("???getLoginToken???????????????response???????????????" + e);
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                LOGGER.error("???getLoginToken?????????????????????????????????" + e);
            }
        }
        return result;
    }
}
