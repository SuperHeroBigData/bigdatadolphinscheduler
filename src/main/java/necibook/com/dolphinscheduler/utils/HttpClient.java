package necibook.com.dolphinscheduler.utils;

import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.api.Constant;
import necibook.com.dolphinscheduler.pojo.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author mujp
 */
public class HttpClient {
    protected static final Log LOGGER = LogFactory.getLog(HttpClient.class);
    private final List<NameValuePair> parameters;
    private final String url;
    private final String sessionId;
    private final String method;
    public HttpClient(List<NameValuePair> parameters
            , String url, String sessionId, String method) {
        this.parameters = parameters;
        this.url = url;
        this.sessionId = sessionId;
        this.method = method;
    }

    public Result submit(Result result) {
        try {
            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

            URI uri = new URIBuilder(Constant.URL_HEADER + this.url)
                    .setParameters(this.parameters)
                    .build();
            if (Constant.GET.equalsIgnoreCase(this.method)) {
                getting(uri, result, closeableHttpClient);
            }
            if (Constant.POST.equalsIgnoreCase(this.method)) {
                posting(uri, result, closeableHttpClient);
            }
        } catch ( URISyntaxException e) {
            LOGGER.error("uri转换错误");
            e.printStackTrace();
            result.setState(Constant.STATE_ERROR);
            result.setMsg(e.toString());
        }

        return result;
    }

    public void getting(URI uri, Result result, CloseableHttpClient closeableHttpClient) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("sessionId", this.sessionId);
            response = closeableHttpClient.execute(httpGet);
            String content = EntityUtils.toString(response.getEntity(), "UTF-8");
            LOGGER.info("访问接口返回结果："+content);
            string2Json(content, result);
        } catch (IOException e) {
            e.printStackTrace();
            result.setState(Constant.STATE_ERROR);
            result.setMsg(e.toString());
        } finally {
            close(closeableHttpClient, response);
        }
    }

    public void posting(URI uri, Result result, CloseableHttpClient closeableHttpClient) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(Constant.URL_HEADER+this.url);
            httpPost.setHeader("sessionId", this.sessionId);
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(this.parameters, "UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
            response = closeableHttpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity(), "UTF-8");
            string2Json(content, result);
        } catch (IOException e) {
            e.printStackTrace();
            result.setState(Constant.STATE_ERROR);
            result.setMsg(e.toString());
        } finally {
            close(closeableHttpClient, response);
        }
    }
    public Result postFile(String url, Result result, CloseableHttpClient closeableHttpClient,String filePath) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("sessionId", this.sessionId);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.setCharset(Charset.forName("utf-8"));
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntityBuilder.addPart("file",new FileBody(new File(filePath)));
            httpPost.setEntity(multipartEntityBuilder.build());
            response = closeableHttpClient.execute(httpPost);
            String content = EntityUtils.toString(response.getEntity(), "UTF-8");
            string2Json(content, result);
        } catch (IOException e) {
            e.printStackTrace();
            result.setState(Constant.STATE_ERROR);
            result.setMsg(e.toString());
        } finally {
             // close(closeableHttpClient, response);
        }
        return result;
    }

    public void string2Json(String content, Result result) {
        try {
            result.setState(Constant.STATE_ERROR);
            result.setMsg(content);

            Object data = JSONObject.parseObject(content).get("data");
            result.setData(data == null ? "" : data.toString());

            Object code = JSONObject.parseObject(content).get("code");
            result.setState((int)code == 0 ? Constant.STATE_SUCCESS : Constant.STATE_ERROR);

            Object msg = JSONObject.parseObject(content).get("msg");
            result.setMsg(msg == null ? "" : msg.toString());

        } catch (Exception e) {
            e.printStackTrace();
            result.setState(Constant.STATE_ERROR);
            result.setMsg(e.toString());
        }


    }

    void close(CloseableHttpClient closeableHttpClient, CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (response != null) {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
