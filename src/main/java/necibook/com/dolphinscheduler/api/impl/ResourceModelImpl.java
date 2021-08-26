package necibook.com.dolphinscheduler.api.impl;

import necibook.com.dolphinscheduler.api.Authenticator;
import necibook.com.dolphinscheduler.api.Constant;
import necibook.com.dolphinscheduler.api.ResourceModel;
import necibook.com.dolphinscheduler.mapper.ProcessDefinitionMapper;
import necibook.com.dolphinscheduler.mapper.ProjectMapper;
import necibook.com.dolphinscheduler.mapper.ResourceMapper;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.dolphinscheduler.utils.HttpClient;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.entity.dsentity.Resource;
import necibook.com.entity.dsentity.ResourceInfo;
import necibook.com.enums.State;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源操作实现
 *
 * @author franky
 * @date 2021/08/23 14:02
 **/
public class ResourceModelImpl implements ResourceModel {
    protected static final Log LOGGER = LogFactory.getLog(ResourceModelImpl.class);
    private static final String MSG = "msg";
    private final Authenticator authenticator;
    private final SheetEnv sheetEnv;
    private final ProjectMapper projectMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final ResourceMapper resourceMapper;

    public ResourceModelImpl(SheetEnv sheetEnv, Authenticator authenticator) {
        this.sheetEnv = sheetEnv;
        this.authenticator = authenticator;
        this.projectMapper = (ProjectMapper) DBManager.setUp(ProjectMapper.class);
        this.processDefinitionMapper = (ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
        this.resourceMapper=(ResourceMapper)DBManager.setUp(ResourceMapper.class);
    }

    public Result getSessionId() {
        return authenticator.authenticate(sheetEnv);
    }
    @Override
    public Result createUpdateResourceDirs(Resource resource) {
        int pid=-1;
        String hostName = sheetEnv.getIp() + ":" + sheetEnv.getPort();
        String fullName = resource.getFullName();
        LOGGER.info("创建资源中心目录："+ fullName);
        Result result = new Result();
        List<Resource> resourceInfos = resourceMapper.queryResourceByfullNameDirs(fullName);
        if(!resourceInfos.isEmpty())
        {
            LOGGER.warn("当前资源中心已经存在：resourceDirName:"+ fullName);
            result.setMsg("当前资源中心已经存在：resourceDirName:"+ fullName);
            result.setState(State.SUCCESS.name());
            return  result;
        }
        int i = fullName.lastIndexOf("/");
        if(i==0)
        {
            i=i+1;
        }
        System.out.println("index"+i);
        String parent= fullName.substring(0, i);
        System.out.println(parent);
        if(!parent.equals("/"))
        {
            List<Resource> resourceInfos1=resourceMapper.queryResourceByfullNameDirs(parent);
            if(resourceInfos1.isEmpty())
            {
                LOGGER.error("当前资源中心父目录不存在：resourceDirName:"+parent);
                result.setMsg("当前资源中心目录不存在");
                throw  new RuntimeException("当前资源中心父目录不存在"+parent);
            }
            pid = resourceInfos1.get(0).getId();
        }
        String fullNames = fullName.substring(fullName.lastIndexOf("/")+1, fullName.length());
        String currentDir="";
        if(fullName.lastIndexOf("/")==0)
        {
            currentDir=fullName.substring(0,fullName.lastIndexOf("/")+1);
        }else
        {
            currentDir=fullName.substring(0,fullName.lastIndexOf("/"));
        }
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("currentDir",currentDir));
        parameters.add(new BasicNameValuePair("file","test.sh"));
        parameters.add(new BasicNameValuePair("name",fullNames ));
        parameters.add(new BasicNameValuePair("pid", String.valueOf(pid)));
        parameters.add(new BasicNameValuePair("type","FILE"));
        HttpClient httpClient = new HttpClient(parameters, hostName + Constant.CREATE_DIRECTORY, getSessionId().getData(), Constant.POST);
        return httpClient.submit(result);

    }

    @Override
    public Result updateResource(Resource resource) {
        return null ;
    }

    @Override
    public Result deleteResource(Resource resource) {
        return null;
    }

    @Override
    public Result createUpdateResource(Resource resource)  {
        int pid=-1;
        String hostName = sheetEnv.getIp() + ":" + sheetEnv.getPort();
        String fullName = resource.getFullName();
        LOGGER.info("创建资源中心目录："+ fullName);
        Result result = new Result();
        int i = fullName.lastIndexOf("/");
        if(i==0)
        {
            i=i+1;
        }
        System.out.println("index"+i);
        String parent= fullName.substring(0, i);
        System.out.println(parent);
        if(!parent.equals("/"))
        {
            List<Resource> resourceInfos1=resourceMapper.queryResourceByfullNameDirs(parent);
            if(resourceInfos1.isEmpty())
            {
                LOGGER.error("当前资源中心父目录不存在：resourceDirName:"+parent);
                result.setMsg("当前资源中心目录不存在");
                throw  new RuntimeException("当前资源中心父目录不存在"+parent);
            }
            pid = resourceInfos1.get(0).getId();
        }
        String fullNames = fullName.substring(fullName.lastIndexOf("/")+1, fullName.length());
        String currentDir="";
        if(fullName.lastIndexOf("/")==0)
        {
            currentDir=fullName.substring(0,fullName.lastIndexOf("/")+1);
        }else
        {
            currentDir=fullName.substring(0,fullName.lastIndexOf("/"));
        }
        //判断资源中心是否存在资源
        List<ResourceInfo> resourceInfos = resourceMapper.queryResourceByfullName(fullName);
        String url= null;
        CloseableHttpClient client = HttpClients.createDefault();
        if(!resourceInfos.isEmpty())
        {
            LOGGER.warn("当前资源中心已经存在：resourceDirName:"+ fullName);
            result.setMsg("当前资源中心已经存在：resourceDirName:"+ fullName);
            //执行更新逻辑
            List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("id",String.valueOf(resourceInfos.get(0).getId())));
            parameters.add(new BasicNameValuePair("name",fullNames ));
            parameters.add(new BasicNameValuePair("type","FILE"));
            try {
                url = Constant.URL_HEADER+hostName + Constant.UPDATE_RESOURCE+"?"+ EntityUtils.toString(new UrlEncodedFormEntity(parameters,"UTF-8"));
                return new HttpClient(parameters,url,getSessionId().getData(),Constant.POST).postFile(url,result,client,resource.getFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("currentDir",currentDir));
        parameters.add(new BasicNameValuePair("name",fullNames ));
        parameters.add(new BasicNameValuePair("pid", String.valueOf(pid)));
        parameters.add(new BasicNameValuePair("type","FILE"));
        try {
            url = Constant.URL_HEADER+hostName + Constant.CREATE_RESOURCE+"?"+ EntityUtils.toString(new UrlEncodedFormEntity(parameters,"UTF-8"));
            return new HttpClient(parameters,url,getSessionId().getData(),Constant.POST).postFile(url,result,client,resource.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
/*        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("sessionId",getSessionId().getData());
        httpPost.setConfig(config);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setCharset(Charset.forName("utf-8"));
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.addPart("file",new FileBody(new File(resource.getFileName())));
        httpPost.setEntity(multipartEntityBuilder.build());
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String s1 = EntityUtils.toString(response.getEntity(), "UTF-8");
            result.setState(Constant.STATE_ERROR);
            result.setMsg(s1);

            Object data = JSONObject.parseObject(s1).get("data");
            result.setData(data == null ? "" : data.toString());

            Object code = JSONObject.parseObject(s1).get("code");
            result.setState((int)code == 0 ? Constant.STATE_SUCCESS : Constant.STATE_ERROR);

            Object msg = JSONObject.parseObject(s1).get("msg");
            result.setMsg(msg == null ? "" : msg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;*/
    return result;
    }
}
