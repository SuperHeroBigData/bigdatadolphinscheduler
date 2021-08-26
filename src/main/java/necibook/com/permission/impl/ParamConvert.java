package necibook.com.permission.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.pojo.ProcessDefinition;
import necibook.com.easyexcel.CommonConfig;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.Connects;
import necibook.com.entity.Location;
import necibook.com.entity.ParentTask;
import necibook.com.enums.TaskType;
import necibook.com.process.Property;
import necibook.com.utils.Constants;
import necibook.com.utils.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static necibook.com.dolphinscheduler.utils.RandomUtil.randomInteger;
import static necibook.com.utils.ParamUtils.commitTask;

/**
 * @ClassName SheetParamConvert
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/28 2:10 下午
 * @Version 1.0
 * 解析excel to task
 */
public class ParamConvert {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamConvert.class);
    private List<Property> localParamsList;
    public static HashMap<String,String> taskRam=new HashMap<>();
    public static  List<Connects> connectsList=new ArrayList<>();
    public static HashMap<String,ArrayList<String>> locationsRam=new HashMap<>();
    public static HashMap<String,ParentTask> tasksMap=new HashMap<>();
    private final SheetParam sheetParam;
    // 标识是否存在Connects连线
    private boolean flag = true;
    private final JSONObject locations;


    private final StringBuffer dependenceIdAll;

    public ParamConvert(SheetParam sheetParam) {
        this.sheetParam = sheetParam;
        this.locations = new JSONObject();
        this.dependenceIdAll = new StringBuffer(5);
        call();
    }

    /**
     * 解析excel对象信息
     */
    public void call() {
        LOGGER.info("开始解析excel中的任务，任务名为:{}",sheetParam.getSubApplication());
        String validFlag = sheetParam.getValidFlag();
        if(!"1".equals(validFlag))
        {
            return;
        }
        String taskParam = sheetParam.getTaskParam();
        localParamsList=new ArrayList<>();
        if (Objects.nonNull(taskParam)) {
            localParamsList = ParamUtils.taskParamToList(taskParam.replaceAll("\n+","\n"));
        }
        Connects connects = new Connects();
        List<HashMap<String, Boolean>> targetarr = new ArrayList<>();
        String taskType = sheetParam.getTaskType();
        CommonConfig commonConfig = JSON.parseObject(sheetParam.getCommonConfig(), CommonConfig.class);
        if(commonConfig.getPreTasks().split(",").length==0)
        {
            flag=false;
        }
        analysisType(taskType, connects, targetarr,localParamsList);
/*        String dependentId = null;
        String dependentName = null;*/
        locationsRam.put(taskRam.get(sheetParam.getSubApplication()),new ArrayList<>());
        ParentTask parentTask = tasksMap.get(sheetParam.getSubApplication());
        System.out.println(tasksMap.toString());
        List<String> preTasks = parentTask.getPreTasks();
        System.out.println(preTasks.toString());
        if(!"[]".equals(preTasks.toString())){
         for (int i = 0; i < preTasks.size(); i++) {
            String taskId = taskRam.get(preTasks.get(i));
            ArrayList<String> targetArr = locationsRam.get(taskId);
            targetArr.add(taskRam.get(sheetParam.getSubApplication()));

           }
        }
        // 遍历任务的依赖，封装location参数
/*        for (Map<String, Boolean> tar :
                targetarr) {
            for (String key : tar.keySet()) {
                boolean value = tar.get(key);
                String[] taskInfo = key.split("\\|");
                String taskId = taskInfo[0];
                String taskName = taskInfo[1];

                locations.fluentPutAll(getLocation(taskId, taskName, true));

*//*                if (value) {
                    dependentId = taskId;
                    dependentName = taskName;
                } else {
                    dependenceIdAll.append(taskId).append(",");
                    locations.fluentPutAll(getLocation(taskId, taskName, false));
                }*//*
            }
        }*/
    }

    /**
     * 提交task到ds
     *
     * @param json 定义工作流
     * @param taskName 工作流名称
     * @return
     */
    public void aggregateTask(String json, String taskName) {
        try {
            LOGGER.info("【aggregateTask】提交当前任务："+taskName);
            ProcessDefinition processDefinition = new ProcessDefinition();
            processDefinition.setConnects(JSONObject.toJSONString(connectsList));
            processDefinition.setDescription(sheetParam.getDescription());
            processDefinition.setGlobalParams("[]");
            processDefinition.setName(taskName);
            processDefinition.setProcessDefinitionJson(json);
            processDefinition.setLocations(locations.toJSONString());
            commitTask(processDefinition);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 执行不同分支
     *
     * @param type      任务分类
     * @param connects  任务参数
     * @param targetarr 任务画布参数
     */
    public void analysisType(String type, Connects connects, List<HashMap<String, Boolean>> targetarr,List<Property> localParamsList) {
        HashMap<String, Boolean> map = new HashMap<>(3);
        ParentTask parentTask=null;
        switch (TaskType.valueOf(type.toUpperCase())) {
            case SHELL:
                parentTask = new ShellTaskImpl(sheetParam, localParamsList, new ParentTask(), flag).convertToData();
                String shellId = parentTask.getId();
                String shellName = parentTask.getName();
                map.put(shellId + "|" + shellName, flag);
                tasksMap.put(shellName,parentTask);
                getLocationConnect(connects, shellId);
               /* taskTypeArr.fluentAdd(JSONArray.toJSON(parentTask));*/

                break;
            case DEPENDENT:
                parentTask=new DependTaskImpl(sheetParam,localParamsList,new ParentTask(),flag).convertToData();
                String depId = parentTask.getId();
                String depName = parentTask.getName();
                map.put(depId + "|" + depName, flag);
                tasksMap.put(depName,parentTask);
                getLocationConnect(connects, depId);
                break;
            case PROCEDURE:
/*                StoredProcedureParameters storedProcedureParameters = new StoreProducerTaskImpl(sheetParam,
                        localParamsList, new StoredProcedureParameters(), flag).convertToData();
                String procedureId = storedProcedureParameters.getId();
                String procedureName = storedProcedureParameters.getName();
                map.put(procedureId + "|" + procedureName, flag);
                getLocationConnect(connects, procedureId);
                taskTypeArr.fluentAdd(JSONArray.toJSON(storedProcedureParameters));*/
                break;
            case SUB_PROCESS:
                parentTask = new SubProcessImpl(sheetParam, localParamsList, new ParentTask(), flag).convertToData();
                String subId = parentTask.getId();
                String subName = parentTask.getName();
                map.put(subId + "|" + subName, flag);
                tasksMap.put(subName,parentTask);
                getLocationConnect(connects, subId);
                break;
            case SQL:
                parentTask = new SqlTaskImpl(sheetParam, localParamsList, new ParentTask(), flag).convertToData();
                String sqlId = parentTask.getId();
                String sqlName = parentTask.getName();
                map.put(sqlId + "|" + sqlName, flag);
                tasksMap.put(sqlName,parentTask);
                getLocationConnect(connects, sqlId);
                break;
            default:
                throw new IllegalArgumentException("该任务类型不存在："+type.toUpperCase());
        }
        targetarr.add(map);
    }

    /**
     * 初始化作业信息 location部分，主要体现在画布中位置和依赖关系
     *
     * @param taskId 任务Id
     * @param taskName 任务名
     * @return location依赖关系
     */
    public JSONObject getLocation(String taskId, String taskName, boolean flag) {
        Location location = new Location();
        JSONObject jsonLocation = new JSONObject();
        if (!flag) {
            location.setNodenumber("1");
        } else {
            location.setTargetarr(dependenceIdAll.substring(0, dependenceIdAll.length() - 1));
            if(dependenceIdAll.toString().split(",").length>1)
            {
                location.setNodenumber("2");
            }else {
                location.setNodenumber("0");
            }
        }
        location.setX(randomInteger(Constants.NUMBER));
        location.setY(randomInteger(Constants.NUMBER));
        location.setName(taskName);

        jsonLocation.put(taskId, JSONObject.toJSON(location));
        return jsonLocation;
    }


    /**
     * 设置connect参数
     *
     * @param connects DAG依赖关系
     * @param id 任务Id
     */
    public void getLocationConnect(Connects connects, String id) {
        String commonConfig = sheetParam.getCommonConfig();
        if("".equals(commonConfig))
        {
            commonConfig=JSONObject.toJSONString(new CommonConfig());
        }
        String pretasksList = JSONObject.parseObject(commonConfig, CommonConfig.class).getPreTasks();
        flag=false;
        String[] preTasks=null;
        if(!"".equals(pretasksList))
        {
            flag=true;
        }
         preTasks= pretasksList.split(",");
        if (flag==true) {
            //存在pretasks
            for (String preTask : preTasks) {
                connects = new Connects();
                connects.setEndPointTargetId(id);
                connects.setEndPointSourceId(taskRam.get(preTask));
                connectsList.add(connects);
                dependenceIdAll.append(taskRam.get(preTask)).append(",");
            }
        }
        //任务放入暂存区
        taskRam.put(sheetParam.getSubApplication(),id);
    }

}
