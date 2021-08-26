package necibook.com.dolphinscheduler.api.impl;

import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.api.Authenticator;
import necibook.com.dolphinscheduler.api.Constant;
import necibook.com.dolphinscheduler.api.ScheduleModel;
import necibook.com.dolphinscheduler.exceptions.TasksException;
import necibook.com.dolphinscheduler.mapper.ProcessDefinitionMapper;
import necibook.com.dolphinscheduler.mapper.ProjectMapper;
import necibook.com.dolphinscheduler.mapper.ScheduleMapper;
import necibook.com.dolphinscheduler.pojo.*;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.dolphinscheduler.utils.HttpClient;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.utils.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static necibook.com.dolphinscheduler.api.Constant.STATE_ERROR;

/**
 * @ClassName SchedulerImpl
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/15 2:35 下午
 * @Version 1.0
 */
public class SchedulerImpl implements ScheduleModel {
    protected static final Log LOGGER = LogFactory.getLog(SchedulerImpl.class);
    private static final String MSG = "msg";
    private final Authenticator authenticator;
    private final SheetEnv sheetEnv;
    private final ProjectMapper projectMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;

    public SchedulerImpl(SheetEnv sheetEnv,Authenticator authenticator) {
        this.sheetEnv = sheetEnv;
        this.authenticator = authenticator;
        this.projectMapper = (ProjectMapper)DBManager.setUp(ProjectMapper.class);
        this.processDefinitionMapper = (ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
    }

    public Result getSessionId() {
        return authenticator.authenticate(sheetEnv);
    }

    @Override
    public Result createSchedule(Schedule schedule) {
        CloseableHttpResponse response = null;
        System.out.println(schedule);
        String content = null;
        CloseableHttpClient httpclient = null;
        Result result = new Result();
        String projectName = schedule.getProjectName();
        try {
            httpclient = HttpClients.createDefault();
            String hostName = sheetEnv.getIp() + ":" + sheetEnv.getPort();
            HttpPost httpPost = new HttpPost(Constant.URL_HEADER + hostName + Constant.CREATE_SCHEDULE.replace("${projectName}", sheetEnv.getProjectName()));
            httpPost.setHeader("sessionId", getSessionId().getData());
            List<NameValuePair> parameters = getScheduleCommitParam(schedule);
            parameters.add(new BasicNameValuePair("processDefinitionId", schedule.getProcessDefinitionId()));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            httpPost.setEntity(formEntity);
            response = httpclient.execute(httpPost);
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
            LOGGER.debug("创建定时时间获取返回值：" + content);
            JSONObject createJobResult = JSONObject.parseObject(content);
            if (Constant.STATE_SUCCESS.equalsIgnoreCase(createJobResult.get(MSG).toString())) {
                ScheduleMapper scheduleMapper = (ScheduleMapper) DBManager.setUp(ScheduleMapper.class);
//                List<Schedule> schedules = scheduleMapper.queryByProcessDefinitionId(Integer.parseInt(schedule.getProcessDefinitionId()));
                String startTime = DateUtils.format(schedule.getSchedule().getStartTime(), "yyyy-MM-dd HH:mm:ss");
                String endTime = DateUtils.format(schedule.getSchedule().getEndTime(), "yyyy-MM-dd HH:mm:ss");
                necibook.com.entity.dsentity.Schedule schedule1 = scheduleMapper.queryByProcessDefinitionIdAndTime(Integer.parseInt(schedule.getProcessDefinitionId()),startTime ,endTime);
                result = onlineSchedule(schedule1.getId(), projectName);

            } else {
                result.setState(STATE_ERROR);
                result.setMsg(createJobResult.get(MSG).toString());
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("【createSchedule】客户端连接异常：" + e);
            result.setState("error");
            result.setMsg("【createSchedule】客户端连接异常：" + e);
        } catch (IOException e) {
            result.setState(STATE_ERROR);
            LOGGER.error("【createSchedule】客户端IO异常：" + e);
            result.setMsg("【createSchedule】客户端连接异常：" + e);
        } catch (Exception e) {
            result.setState(STATE_ERROR);
            result.setMsg("【createSchedule】连接异常：" + e);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error("【createSchedule】关闭response响应异常：" + e);
                }
            }
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                LOGGER.error("【createSchedule】关闭客户端异常：" + e);
            }
        }
        return result;
    }

    @Override
    public Result onlineSchedule(long scheduleId, String projectName) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("id", String.valueOf(scheduleId)));
        HttpClient httpClient = new HttpClient(parameters, sheetEnv.getIp() + ":" + sheetEnv.getPort() + Constant.ONLINE_SCHEDULE.replace("${projectName}",
                sheetEnv.getProjectName()), getSessionId().getData(), Constant.POST);
        Result result = new Result();
        httpClient.submit(result);
        return result;
    }

    @Override
    public Result offlineSchedule(long scheduleId, String projectName) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("id", String.valueOf(scheduleId)));
        HttpClient httpClient = new HttpClient(parameters, sheetEnv.getIp() + ":" + sheetEnv.getPort() + Constant.OFFLINE_SCHEDULE.replace("${projectName}",
                sheetEnv.getProjectName()), getSessionId().getData(), Constant.POST);
        Result result = new Result();
        httpClient.submit(result);
        return result;
    }

    @Override
    public Result updateSchedule(Schedule schedule) {
        ScheduleMapper scheduleMapper = (ScheduleMapper) DBManager.setUp(ScheduleMapper.class);
        Result result=new Result();
        List<necibook.com.entity.dsentity.Schedule> schedules = scheduleMapper.queryByProcessDefinitionId(Integer.parseInt(schedule.getProcessDefinitionId()));
        if(!Objects.isNull(schedules))
        {
            for (int i = 0; i < schedules.size(); i++) {
                String releaseState = scheduleMapper.queryReleaseStateByScheduleId(schedules.get(i).getId());
                if(!"0".equals(releaseState)) {
                    result = offlineSchedule(schedules.get(i).getId(), sheetEnv.getProjectName());
                }
                List<NameValuePair> scheduleCommitParam = getScheduleCommitParam(schedule);
                scheduleCommitParam.add(new BasicNameValuePair("id", String.valueOf(schedules.get(i).getId())));
                HttpClient httpClient = new HttpClient(scheduleCommitParam, sheetEnv.getIp() + ":" + sheetEnv.getPort() + Constant.UPDATE_SCHEDULE.replace("${projectName}",
                        schedule.getProjectName()), getSessionId().getData(), Constant.POST);
                httpClient.submit(result);
                if (Constant.STATE_ERROR.equals(result.getState())) {
                    throw new TasksException("更新定时任务失败");
                }
                result = onlineSchedule(schedules.get(i).getId(), schedule.getProjectName());
            }

        }else
        {
            // 定时未配置执行创建定时逻辑
            result=createSchedule(schedule);

        }
        return result;
    }


    /**
     * 定时任务参数
     *
     * @param schedule
     * @return
     */
    public List<NameValuePair> getScheduleCommitParam(Schedule schedule) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("schedule", JSONObject.toJSONString(schedule.getSchedule())));
        parameters.add(new BasicNameValuePair("warningType", String.valueOf(WarningType.FAILURE)));
        parameters.add(new BasicNameValuePair("warningGroupId", "0"));
        parameters.add(new BasicNameValuePair("failureStrategy", String.valueOf(FailureStrategy.CONTINUE)));
        parameters.add(new BasicNameValuePair("receivers", schedule.getReceivers()));
        parameters.add(new BasicNameValuePair("receiversCc", schedule.getReceiversCc()));
        parameters.add(new BasicNameValuePair("workerGroupId", "0"));
        parameters.add(new BasicNameValuePair("processInstancePriority", String.valueOf(Priority.MEDIUM)));
        return parameters;
    }
}
