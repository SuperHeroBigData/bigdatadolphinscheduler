package necibook.com.dolphinscheduler.api.impl;

import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.api.Authenticator;
import necibook.com.dolphinscheduler.api.Constant;
import necibook.com.dolphinscheduler.api.WorkFlowModel;
import necibook.com.dolphinscheduler.exceptions.TasksException;
import necibook.com.dolphinscheduler.mapper.ProcessDefinitionMapper;
import necibook.com.dolphinscheduler.mapper.ProjectMapper;
import necibook.com.dolphinscheduler.pojo.LineState;
import necibook.com.dolphinscheduler.pojo.ProcessDefinition;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.dolphinscheduler.pojo.Schedule;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.dolphinscheduler.utils.HttpClient;
import necibook.com.easyexcel.ExcelListener;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.enums.RunMode;
import necibook.com.enums.State;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author franky
 */
public class WorkFlowModelImpl implements WorkFlowModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowModelImpl.class);
    private final Authenticator authenticator;
    private final SheetEnv sheetEnv;
    private final ProjectMapper projectMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;
    public WorkFlowModelImpl(SheetEnv sheetEnv,Authenticator authenticator) {
        this.sheetEnv = sheetEnv;
        this.authenticator = authenticator;
        this.projectMapper = (ProjectMapper)DBManager.setUp(ProjectMapper.class);
        this.processDefinitionMapper = (ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
    }

    public Result getSessionId() {
        return authenticator.authenticate(sheetEnv);
    }

    @Override
    public Result createWorkFlow(ProcessDefinition processDefinitionJson) {
        LOGGER.info("createWorkFlow创建任务："+processDefinitionJson.getName());
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpclient = null;
        Result result = new Result();
        result.setProjectName(processDefinitionJson.getProjectName());
        result.setJobName(processDefinitionJson.getName());
        try {
            httpclient = HttpClients.createDefault();
            String hostName = sheetEnv.getIp() + ":" + sheetEnv.getPort();
            HttpPost httpPost = new HttpPost(Constant.URL_HEADER + hostName + Constant.WORK_FLOW.replace("${projectName}", processDefinitionJson.getProjectName()));
            httpPost.setHeader("sessionId", getSessionId().getData());
            List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("connects", processDefinitionJson.getConnects()));
            parameters.add(new BasicNameValuePair("description", processDefinitionJson.getDescription()));
            parameters.add(new BasicNameValuePair("locations", processDefinitionJson.getLocations()));
            parameters.add(new BasicNameValuePair("processDefinitionJson", processDefinitionJson.getProcessDefinitionJson()));
            parameters.add(new BasicNameValuePair("projectName", processDefinitionJson.getProjectName()));
            parameters.add(new BasicNameValuePair("name", processDefinitionJson.getName()));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            LOGGER.info("connects"+processDefinitionJson.getConnects());
            LOGGER.info("psjson"+processDefinitionJson.getProcessDefinitionJson());
            LOGGER.info("description"+processDefinitionJson.getDescription());
            LOGGER.info("locations"+processDefinitionJson.getLocations());
            LOGGER.info("projectName"+processDefinitionJson.getProjectName());
            LOGGER.info("name"+processDefinitionJson.getName());
            httpPost.setEntity(formEntity);
            LOGGER.info("创建工作流post："+Constant.URL_HEADER + hostName + Constant.WORK_FLOW.replace("${projectName}", processDefinitionJson.getProjectName()));
            LOGGER.info("创建工作流信息参数："+parameters);
            response = httpclient.execute(httpPost);
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONObject createJobResult = JSONObject.parseObject(content);
            if (Constant.STATE_SUCCESS.equalsIgnoreCase(createJobResult.get(Constant.MSG).toString())) {
                LOGGER.info("工作流{}创建成功，执行工作流状态释放为Release",processDefinitionJson.getName());
                String project_name=processDefinitionJson.getProjectName();
                int processId = projectMapper.queryByName(project_name).getId();
                necibook.com.entity.dsentity.ProcessDefinition processDefinition = processDefinitionMapper.verifyByDefineName(processId, processDefinitionJson.getName());
                LineState lineState = new LineState();
                lineState.setFlag(Constant.ONLINE);
                lineState.setJobName(processDefinitionJson.getName());
                lineState.setProjectName(project_name);
                lineState.setProcessDefinitionId(String.valueOf(processDefinition.getId()));
                releaseState(lineState, result, hostName);
            } else {
                result.setState(Constant.STATE_ERROR);
                result.setMsg(createJobResult.get(Constant.MSG).toString());

            }
        } catch (ClientProtocolException e) {
            LOGGER.error("【createWorkFlow接口】客户端连接异常：" + e);
            result.setState("error");
            result.setMsg("【createWorkFlow接口】客户端连接异常：" + e);
        } catch (IOException e) {
            result.setState(Constant.STATE_ERROR);
            LOGGER.error("【createWorkFlow接口】客户端IO异常：" + e);
            result.setMsg("【createWorkFlow接口】客户端连接异常：" + e);
        } catch (Exception e) {
            result.setState(Constant.STATE_ERROR);
            result.setMsg("【createWorkFlow接口】连接异常：" + e);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error("【startJob接口】关闭response响应异常：" + e);
                }
            }
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                LOGGER.error("【startJob接口】关闭客户端异常：" + e);
            }
        }
        return result;
    }

    @Override
    public Result startJob(String loginUser, String loginPassword, String hostName, String receivers, String jobName, String projectName) {
        Result result = new Result();
        int projectId = projectMapper.queryByName(projectName).getId();
        LOGGER.info("释放状态获取 projectId:"+projectId);
        necibook.com.entity.dsentity.ProcessDefinition processDefinition = processDefinitionMapper.verifyByDefineName(projectId, jobName);
        String processDefinitionId = String.valueOf(processDefinition.getId());
        LOGGER.info("当前工作流ID"+processDefinitionId);
        List<NameValuePair> parameters = new ArrayList<>();
        Schedule schedule = ExcelListener.scheduleMap.get(processDefinition.getName());
        parameters.add(new BasicNameValuePair("failureStrategy",schedule.getFailureStrategy()));
        parameters.add(new BasicNameValuePair("processDefinitionId", processDefinitionId));
        parameters.add(new BasicNameValuePair("processInstancePriority",schedule.getProcessInstancePriority()));
        parameters.add(new BasicNameValuePair("scheduleTime", JSONObject.toJSONString(schedule.getSchedule())));
        parameters.add(new BasicNameValuePair("warningGroupId", schedule.getWarningGroupId()));
        parameters.add(new BasicNameValuePair("warningType", schedule.getWarningType()));
        parameters.add(new BasicNameValuePair("execType","START_PROCESS"));
        parameters.add(new BasicNameValuePair("receivers", schedule.getReceivers()));
        parameters.add(new BasicNameValuePair("receiversCc", schedule.getReceiversCc()));
        parameters.add(new BasicNameValuePair("runMode", RunMode.RUN_MODE_PARALLEL.name()));
        parameters.add(new BasicNameValuePair("startNodeList", ""));
        parameters.add(new BasicNameValuePair("taskDependType", ""));
        parameters.add(new BasicNameValuePair("timeout", String.valueOf(processDefinition.getTimeout())));
        parameters.add(new BasicNameValuePair("workerGroupId", "0"));
        HttpClient httpClient = new HttpClient(parameters, hostName + Constant.START_JOB_CONN.replace("${projectName}", projectName), getSessionId().getData(), Constant.POST);
        httpClient.submit(result);
        return result;
    }

    @Override
    public Result releaseState(LineState lineState, Result result, String hostName) {

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("processId", lineState.getProcessDefinitionId()));
        // 1为上线 ，0为下线
        parameters.add(new BasicNameValuePair("releaseState", lineState.getFlag()));
        LOGGER.info("定时状态："+lineState.toString());
        HttpClient httpClient = new HttpClient(parameters, hostName + Constant.STATE.replace("${projectName}", lineState.getProjectName()), getSessionId().getData(), Constant.POST);
        return httpClient.submit(result);
    }

    @Override
    public Result updateProcessDefinition(ProcessDefinition processDefinition) {
        String jobName = processDefinition.getName();
        LOGGER.info("更新任务："+jobName);
        String projectName = processDefinition.getProjectName();
        String hostName = sheetEnv.getIp() + ":" + sheetEnv.getPort();
        int projectId = projectMapper.queryByName(projectName).getId();
        necibook.com.entity.dsentity.ProcessDefinition processDefinitionNew = processDefinitionMapper.verifyByDefineName(projectId, jobName);
        String processDefinitionId = String.valueOf(processDefinitionNew.getId());

        LineState lineState = new LineState();
        lineState.setFlag(Constant.OFFLINE);
        lineState.setJobName(jobName);
        lineState.setProjectName(projectName);
        lineState.setProcessDefinitionId(processDefinitionId);
        Result result = new Result();
        releaseState(lineState, result, hostName);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("connects", processDefinition.getConnects()));
        parameters.add(new BasicNameValuePair("id", processDefinitionId));
        parameters.add(new BasicNameValuePair("locations", processDefinition.getLocations()));
        parameters.add(new BasicNameValuePair("name", jobName));
        parameters.add(new BasicNameValuePair("processDefinitionJson", processDefinition.getProcessDefinitionJson()));
        parameters.add(new BasicNameValuePair("projectName", projectName));
        parameters.add(new BasicNameValuePair("description", processDefinition.getDescription()));
        HttpClient httpClient = new HttpClient(parameters, hostName + Constant.PROCESS_UPDATE.replace("${projectName}", projectName), getSessionId().getData(), Constant.POST);
        result = httpClient.submit(result);
        if(State.valueOf(result.getState()).equals(State.ERROR))
        {
            throw new TasksException("任务更新失败："+result);
        }
        lineState.setFlag(Constant.ONLINE);
        return releaseState(lineState, result, hostName);
    }

    @Override
    public Result deleteProcessDefinition(String projectName,String taskName) {
        LOGGER.info("删除任务："+projectName+"-"+taskName);
        //删除任务
        int projectId = projectMapper.queryByName(projectName).getId();
        Integer processId = processDefinitionMapper.queryByDefineName(projectId, taskName);
        String processDefinitionId = String.valueOf(processId);
        if (Objects.isNull(processId)){
            LOGGER.info("当前项目{}任务{}已经删除",projectName,taskName);
            Result result = new Result();
            result.setState("SUCCESS");
            return result;
        }
        LineState lineState = new LineState();
        lineState.setFlag(Constant.OFFLINE);
        lineState.setJobName(taskName);
        lineState.setProjectName(projectName);
        lineState.setProcessDefinitionId(processDefinitionId);
        String hostName = sheetEnv.getIp() + ":" + sheetEnv.getPort();
        Result result = new Result();
        result = releaseState(lineState, result, hostName);
        System.out.println(result);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("projectName", projectName));
        parameters.add(new BasicNameValuePair("processDefinitionId", processDefinitionId));
        HttpClient httpClient = new HttpClient(parameters, hostName + Constant.PROCESS_DELETE.replace("${projectName}",projectName), getSessionId().getData(), Constant.GET);
        return httpClient.submit(result);
    }
}
