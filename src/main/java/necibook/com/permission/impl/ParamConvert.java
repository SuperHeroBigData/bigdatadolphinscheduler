package necibook.com.permission.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import necibook.com.easyexcel.CommonConfig;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.Connects;
import necibook.com.entity.ParentTask;
import necibook.com.enums.TaskType;
import necibook.com.process.Property;
import necibook.com.utils.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/** 节点参数封装
 * @author franky
 * @date 2021/08/17 14:40
 **/
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
    public ParamConvert(SheetParam sheetParam) {
        this.sheetParam = sheetParam;
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
        analysisType(taskType, connects, targetarr,localParamsList);
        locationsRam.put(taskRam.get(sheetParam.getSubApplication()),new ArrayList<>());
        ParentTask parentTask = tasksMap.get(sheetParam.getSubApplication());
        List<String> preTasks = parentTask.getPreTasks();
        if(!"[]".equals(preTasks.toString())){
         for (int i = 0; i < preTasks.size(); i++) {
            String taskId = taskRam.get(preTasks.get(i));
            ArrayList<String> targetArr = locationsRam.get(taskId);
            targetArr.add(taskRam.get(sheetParam.getSubApplication()));

           }
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
        LOGGER.info("任务名{}参数封装完毕",sheetParam.getSubApplication());
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
            }
        }
        //任务放入暂存区
        taskRam.put(sheetParam.getSubApplication(),id);
    }
}
