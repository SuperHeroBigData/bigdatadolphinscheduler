package necibook.com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import necibook.com.AnalysisApplication;
import necibook.com.dolphinscheduler.pojo.ProcessDefinition;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.dolphinscheduler.pojo.Schedule;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.enums.DDL;
import necibook.com.enums.DataType;
import necibook.com.enums.Direct;
import necibook.com.process.Property;
import necibook.com.upgrade.BuildTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static necibook.com.utils.HttpUtil.executeResult;

/**
 * @ClassName ParamUtils
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/24 5:21 下午
 * @Version 1.0
 */
public class ParamUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamUtils.class);

    private static SheetEnv sheetEnv = null;

    /**
     * 参数格式转换
     *
     * @param taskParam key=value
     * @return json
     */
    public static List<Property> taskParamToList(String taskParam) {
        List<Property> listLocalParams = new ArrayList<>();
        Property property = null;
        for (String dependent :
                taskParam.split("\n")) {
            String[] keyValue = dependent.replace("_x000D_","").split("=");
            property = new Property();
            property.setDirect(Direct.IN);
            property.setType(DataType.VARCHAR);
            property.setProp(keyValue[0]);
            property.setValue(keyValue[1]);
            listLocalParams.add(property);
        }
        return listLocalParams;
    }
    /**
     * 参数格式转换
     *
     * @param taskParam direct=dataType=key=value
     * @return json
     */
    public static List<Property> taskParamToListSql(String taskParam) {
        List<Property> listLocalParams = new ArrayList<>();
        Property property = null;
        for (String dependent :
                taskParam.split("\n")) {
            String[] keyValue = dependent.replace("_x000D_","").split("=");
            property = new Property();
            if(keyValue.length>2)
            {
                //非默认逻辑
                property.setDirect(Enum.valueOf(Direct.class,keyValue[0]));
                property.setType(Enum.valueOf(DataType.class,keyValue[1]));
            }
            property.setProp(keyValue[3]);
            property.setValue(keyValue[4]);
            listLocalParams.add(property);
        }
        return listLocalParams;
    }


    /**
     * 解析依赖字段信息
     * @param dependentParam
     * @return
     */
    public static List<String> taskDependentToList(String dependentParam){

        return Objects.isNull(dependentParam) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(dependentParam.replace("_x000D_","").replaceAll("\n+","\n").split("\n")));
    }

    /**
     * 获取当前环境
     *
     * @return
     */
    public static SheetEnv getEnvInfo() {
        SheetEnv sheetEnv = AnalysisApplication.sheetEnv;
        return sheetEnv;
    }

    /**
     * 静态获取当前环境
     *
     * @return
     */
    public static SheetEnv getInstanceEnv() {
        if (Objects.isNull(sheetEnv)) {
            sheetEnv = getEnvInfo();
            JSONObject json = (JSONObject) JSON.toJSON(sheetEnv);
            assert json != null;
            json.put("password","******");
            json.put("dbPassword","******");
            LOGGER.info("获取环境信息：{}", json);
        }

        return sheetEnv;
    }

    /**
     * 根据类型提交作业
     * @param processDefinition
     * @return
     */
    public static void commitTask(ProcessDefinition processDefinition){
        LOGGER.info("执行工作流{}逻辑{}",processDefinition.getName(),AnalysisApplication.sheetEnv.getJobDDL());
        BuildTask buildTask = new BuildTask(AnalysisApplication.sheetEnv);
        Result result= null;
        switch (DDL.valueOf(getInstanceEnv().getJobDDL().toUpperCase())) {
            case CREATE:
                result = buildTask.createWork(processDefinition);
                result.setJobName(processDefinition.getName());
                executeResult(result);
                break;
            case DELETE:
                result = buildTask.batchDeleteWork(processDefinition.getProjectName(),processDefinition.getName());
                result.setJobName(processDefinition.getName());
                executeResult(result);
                break;
            case UPDATE:
                result = buildTask.updateWork(processDefinition);
                result.setJobName(processDefinition.getName());
                executeResult(result);
                break;
            default:
                LOGGER.error("执行方式错误!!! " + getInstanceEnv().getJobDDL());
                throw new IllegalArgumentException("执行方式错误!!! " + getInstanceEnv().getJobDDL());
        }
    }

    /**
     * 根据类型定时任务
     * @param schedule
     * @return
     */
    public static void commitSchedule(Schedule schedule){
        LOGGER.info("执行任务工作流{}定时逻辑{}",schedule.getProcessDefinitionId(),schedule.getSchedule().getCrontab());
        BuildTask buildTask = new BuildTask(AnalysisApplication.sheetEnv);
        switch (DDL.valueOf(getInstanceEnv().getJobDDL().toUpperCase())) {
            case CREATE:
                executeResult(buildTask.createWorkSchedule(schedule));
                break;
            case UPDATE:
                //再次创建时，会将之前的定时任务下线
                executeResult(buildTask.updateWorkSchedule(schedule));
                break;
            case DELETE:
                //TODO 删除定时任务
                break;
            default:
                LOGGER.error("执行方式错误!!! " + getInstanceEnv().getJobDDL());
                throw new IllegalArgumentException("执行方式错误!!! " + getInstanceEnv().getJobDDL());
        }
    }

}
