package necibook.com.permission.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import necibook.com.AnalysisApplication;
import necibook.com.dolphinscheduler.exceptions.TasksException;
import necibook.com.dolphinscheduler.mapper.ProcessDefinitionMapper;
import necibook.com.dolphinscheduler.mapper.ProjectMapper;
import necibook.com.dolphinscheduler.pojo.ProcessDefinition;
import necibook.com.dolphinscheduler.pojo.Schedule;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.easyexcel.ExcelListener;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.Connects;
import necibook.com.entity.Location;
import necibook.com.entity.ParentTask;
import necibook.com.entity.dsentity.ProcessData;
import necibook.com.entity.subprocess.SubProcessParameters;
import necibook.com.permission.TaskCommit;
import necibook.com.process.Property;
import necibook.com.utils.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static necibook.com.dolphinscheduler.utils.RandomUtil.randomInteger;
import static necibook.com.utils.ParamUtils.commitSchedule;
import static necibook.com.utils.ParamUtils.commitTask;

/**
 * @ClassName SubProcessTaskImpl
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/3 3:14 下午
 * @Version 1.0
 */
public class SubProcessTaskImpl implements TaskCommit {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubProcessTaskImpl.class);
    private static final String TASK_NOT_EXIST = "任务不存在";
    private static String CRON = null;
    private SubProcessParameters subProcessParameters;
    private SheetParam sheet;
/*    private InstanceTask instanceTask;*/
    private static final JSONObject JSON_LOCATIONS = new JSONObject();
    private static final List<Connects> LIST_CONNECTS = new ArrayList<>();
    private static final List<String> NODE_NUMBER_LIST = new ArrayList<>(10);
    private static final Queue<SubProcessParameters> SUB_PROCESS_PARAMETERS_QUEUE = new LinkedList<>();
    private final ArrayList<String> locationsList=new ArrayList<>();

    private ProcessDefinitionMapper processDefinitionMapper;
    private ProjectMapper projectMapper;

    public SubProcessTaskImpl() {
        this.projectMapper = (ProjectMapper) DBManager.setUp(ProjectMapper.class);
        this.processDefinitionMapper = (ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
    }

    public SubProcessTaskImpl(SheetParam sheet) {
        this.subProcessParameters = new SubProcessParameters();
        /*this.instanceTask = new InstanceTask();*/
        this.sheet = sheet;

    }
    /**
     * 初始化subprocess信息
     */
/*    public void convertToData() {
        String taskId = taskId(iquantex.com.utils.Constant.RANDOM_ID);
        subProcessParameters.setId(taskId);
        subProcessParameters.setDescription(Objects.isNull(sheet.getDescription()) ? "" : sheet.getDescription());
        subProcessParameters.setMaxRetryTimes(Objects.isNull(sheet.getMaxRerun()) ? iquantex.com.utils.Constant.MAX_RETRY_TIMES : Integer.parseInt(sheet.getMaxRerun()));

        TimeOut timeOut = new TimeOut();
        timeOut.setInterval(Long.parseLong(sheet.getAlarmTime()));
        subProcessParameters.setTimeout(timeOut);

        subProcessParameters.setType(TaskType.SUB_PROCESS.name());
        String taskName = sheet.getSubApplication() + "." + sheet.getTableName();
        subProcessParameters.setName(taskName);

        ProcessDefinition processDefinition = instanceTask.getProcessDefinitionId(taskName, sheet.getApplication());

        if (Objects.isNull(processDefinition.getId())) {
            Result result = new Result();
            result.setState(State.ERROR.name());
            result.setMsg(subProcessParameters.getName() + "\t" + TASK_NOT_EXIST);
            executeResult(result);
            throw new TasksException(subProcessParameters.getName() + "\t" + TASK_NOT_EXIST);
        }

        Params params = new Params();
        params.setProcessDefinitionId(processDefinition.getId());
        subProcessParameters.setParams(params);
        String depend = sheet.getDepend();
        List<String> dependent = taskDependentToList(depend);
        List<String> newDependent = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dependent) && CollectionUtils.isNotEmpty(SUB_PROCESS_PARAMETERS_QUEUE)) {
            subProcessLocations(taskId, taskName, dependent, newDependent);
        } else {
            JSON_LOCATIONS.fluentPutAll(getLocation(taskId, taskName, null));
        }
        subProcessParameters.setPreTasks(newDependent);
        NODE_NUMBER_LIST.addAll(dependent);

        if (Objects.isNull(CRON)) {
            CRON = sheet.getTaskScheduler();
        }
        SUB_PROCESS_PARAMETERS_QUEUE.add(subProcessParameters);
    }*/

    /**
     * 将当前作业依赖从队列中获取进行对象映射
     *
     * @param taskId 任务Id
     * @param taskName 任务名
     * @param depend 每一个excel中当前任务前值依赖任务
     * @param newDepend 当前任务的依赖添加到depend
     *//*
    public void subProcessLocations(String taskId, String taskName,
                                    List<String> depend, List<String> newDepend) {
        LOGGER.info("开始生成作业依赖：{}", taskName);
        StringBuilder locationTarget = new StringBuilder(10);
        Connects connects = null;
        String subProcessName = null;
        String subProcessId = null;
        for (String task : depend) {
            connects = new Connects();
            connects.setEndPointTargetId(taskId);
            for (SubProcessParameters subProcess :
                    SUB_PROCESS_PARAMETERS_QUEUE) {
                subProcessName = subProcess.getName();
                if (Objects.equals(task, subProcessName)) {
                    newDepend.add(task);
                    subProcessId = subProcess.getId();

                    connects.setEndPointSourceId(subProcessId);
                    LIST_CONNECTS.add(connects);
                    locationTarget.append(subProcessId).append(",");
                }
            }
*//*
            if (Objects.isNull(getTaskId(task))) {
                throw new TasksException(task + " 依赖任务不存在");
            }*//*
        }

        JSONObject location;
        if (locationTarget.length() != 0) {
            location = getLocation(taskId, taskName, locationTarget.substring(0, locationTarget.length() - 1));
        } else {
            location = getLocation(taskId, taskName, null);
        }
        JSON_LOCATIONS.fluentPutAll(location);


    }*/

    /**
     * 获取任务Id
     * @param taskName 任务名
     * @return 返回依赖中存在的任务名
     */
/*    public String getTaskId(String taskName) {
        LOGGER.info("查询依赖信息：{}", taskName);
        ProcessDefinition processDefinitionId = instanceTask.getProcessDefinitionId(taskName, Objects.requireNonNull(getEnvInfo()).getProjectName());
        JSONObject jsonObject = JSON.parseObject(processDefinitionId.getLocations());
        for (String key : jsonObject.keySet()) {
            JSONObject location = jsonObject.getJSONObject(key);
            String nodeName = location.getString("name");
            if (Objects.equals(nodeName, taskName.trim())) {
                return key;
            }
        }
        return "";
    }*/

    /**
     * 美化坐标轴
     *
     * @return
     */
    public Long point() {
        //TODO x，y轴坐标系
        return null;
    }

    /**
     * 获取nodeNumber
     *
     * @param taskName 任务名
     * @return  节点后置依赖任务数
     */
    public static Long nodeNumber(String taskName) {
        Map<String, Long> collect = NODE_NUMBER_LIST.
                stream().
                collect(Collectors.
                        groupingBy(Function.identity(), Collectors.counting()));
        return Objects.isNull(collect.get(taskName)) ? 0L : collect.get(taskName);
    }

    /**
     * 替换nodeNumber默认值
     */
/*    public static JSONObject replaceNodeNumber() {
        for (String key :
                JSON_LOCATIONS.keySet()) {
            JSONObject location = JSON_LOCATIONS.getJSONObject(key);
            Long nodeNumber = nodeNumber(location.getString("name"));
            location.put("nodenumber", String.valueOf(nodeNumber));
        }
        return JSON_LOCATIONS;
    }*/

    /**
     * 封装subProcess依赖 location部分
     *
     * @param taskId 任务Id
     * @param taskName 任务名
     * @return 返回单个task的location参数
     */
    public JSONObject getLocation(String taskId, String taskName, String targetarr) {
        Location location = new Location();
        JSONObject jsonLocation = new JSONObject();
        location.setTargetarr(Objects.isNull(targetarr) ? "" : targetarr);
        location.setX(randomInteger(Constants.NUMBER));
        location.setY(randomInteger(Constants.NUMBER));
        location.setName(taskName);
        jsonLocation.put(taskId, JSONObject.toJSON(location));
        return jsonLocation;
    }

    /**
     * 获取依赖对象队列 使用
     *
     * @return  工作流参数
     */
    public static JSONArray getDependenceDefinition() {
        JSONArray dependence = new JSONArray();
        if (CollectionUtils.isEmpty(SUB_PROCESS_PARAMETERS_QUEUE)) {
            throw new TasksException("依赖队列为空。");
        }

        while (!SUB_PROCESS_PARAMETERS_QUEUE.isEmpty()) {
            SubProcessParameters parameters = SUB_PROCESS_PARAMETERS_QUEUE.poll();
            dependence.fluentAdd(JSONObject.toJSON(parameters));
        }
        return dependence;
    }

    /**
     * 按照依赖关系提交任务
     *
     * @param processDefinition
     * @return 返回执行结果
     */
    @Override
    public void getTaskParam(ProcessDefinition processDefinition) {

/*        JSONObject jsonObject = replaceNodeNumber();*/
        createJob(processDefinition);
/*        LOGGER.info("依赖关系locations参数：{}", jsonObject.toJSONString());
        processDefinition.setLocations(jsonObject.toJSONString());*/
        commitTask(processDefinition);
//        SheetEnv instanceEnv = getInstanceEnv();
        SheetEnv sheetEnv = AnalysisApplication.sheetEnv;
        Schedule schedule = ExcelListener.scheduleMap.get(processDefinition.getName());
        int project_id = projectMapper.queryByName(schedule.getProjectName()).getId();
        int process_id = processDefinitionMapper.queryByDefineName(project_id, processDefinition.getName()).getId();
        schedule.setProcessDefinitionId(String.valueOf(process_id));
//        Schedule taskSchedule = getTaskSchedule(sheetEnv,processDefinition.getName());
        LOGGER.info("Job定时信息：{}", JSONObject.toJSONString(schedule));
        commitSchedule(schedule);
    }
    /**
     * 创建新任务
     *
     * @param processDefinition
     */
    public void createJob(ProcessDefinition processDefinition) {

/*      TaskParameters taskParameters = new TaskParameters();
        taskParameters.setGlobalParams(new ArrayList<>());
        JSONArray dependenceDefinition = getDependenceDefinition();

        LOGGER.info("依赖关系tasks参数：{}", dependenceDefinition);
        taskParameters.setTasks(dependenceDefinition);
        taskParameters.setTenantId(new InstanceTask().getTenantId());
        taskParameters.setTimeout(iquantex.com.utils.Constant.TIMEOUT);

        String jsonString = JSONObject.toJSONString(taskParameters, SerializerFeature.WriteMapNullValue);


        LOGGER.info("依赖关系connect参数：{}", JSONObject.toJSONString(LIST_CONNECTS));
        processDefinition.setConnects(JSONObject.toJSONString(LIST_CONNECTS));

        processDefinition.setDescription("");

        processDefinition.setGlobalParams("[]");
        processDefinition.setProcessDefinitionJson(jsonString);*/
        locationsPackage(processDefinition);
    }
    /*
        封装locations
     */
    public ProcessDefinition locationsPackage(ProcessDefinition processDefinition)
    {
        HashMap<String, ArrayList<String>> locationsRam = ParamConvert.locationsRam;
        HashMap<String,ParentTask> taskLists = ParamConvert.tasksMap;
        Iterator<Map.Entry<String, ParentTask>> iterator = taskLists.entrySet().iterator();
        HashMap<String, String> taskRam = ParamConvert.taskRam;
        HashMap<String, ProcessDefinition> processMap = ExcelListener.processMap;
        while (iterator.hasNext())
        {
            ParentTask next = iterator.next().getValue();
            Location location = new Location();
            location.setNodenumber(String.valueOf(locationsRam.get(next.getId()).size()));
            location.setName(next.getName());
            List<String> preTasks = next.getPreTasks();
            StringBuilder targetArr=new StringBuilder(5);
            for (int i = 0; i <preTasks.size() ; i++) {
                targetArr.append(taskRam.get(preTasks.get(i))).append(",");
            }
            String targetIds = targetArr.substring(0, targetArr.length() - 1);
            if(targetIds.equals("null"))
            {
                targetIds="";
            }
            location.setTargetarr(targetIds);
            location.setX(randomInteger(Constants.NUMBER));
            location.setY(randomInteger(Constants.NUMBER));
            String s = JSONObject.toJSONString(location);
            String id = JSONObject.toJSONString(next.getId());
            String locations=id+":"+s;
            System.out.println(locations);
            locationsList.add(locations);
        }
        processDefinition.setConnects(JSONObject.toJSONString(ParamConvert.connectsList));
        String location = locationsList.toString();
        String locations="{"+location.substring(1,location.length()-1)+"}";
        processDefinition.setLocations(locations);
        ProcessData processData = new ProcessData();
        List<Property> globalParamList = processMap.get(processDefinition.getName()).getGlobalParamList();
        processData.setGlobalParams(Objects.isNull(processDefinition.getGlobalParamList())?new ArrayList<>():globalParamList);
        processData.setTenantId(processDefinition.getTenantId());
        processData.setTimeout(processDefinition.getTimeout());

        Set<Map.Entry<String, ParentTask>> entries = taskLists.entrySet();
        Iterator<Map.Entry<String, ParentTask>> iterator1 = entries.iterator();
        ArrayList<ParentTask> parentTasks = new ArrayList<>();
        while (iterator1.hasNext())
        {

            ParentTask value = iterator1.next().getValue();
            System.out.println("preTasks"+value.getPreTasks());
            if("[]".equals(value.getPreTasks().toString()))
            {
                value.setPreTasks(new ArrayList<>());
            }
            parentTasks.add(value);

        }
        processData.setTasks(parentTasks);
        String jsonDefination = JSONObject.toJSONString(processData, SerializerFeature.WriteMapNullValue);
        processDefinition.setProcessDefinitionJson(jsonDefination);
        return processDefinition;
    }

    /**
     * Job定时
     *
     * @param instanceEnv
     * @param jobName
     * @return
     */
    public Schedule getTaskSchedule(SheetEnv instanceEnv, String jobName) {
        HashMap<String, Schedule> scheduleMap = ExcelListener.scheduleMap;
        Schedule schedule = scheduleMap.get(jobName);
        int projectId = projectMapper.queryByName(instanceEnv.getProjectName()).getId();
        necibook.com.entity.dsentity.ProcessDefinition processDefinition = processDefinitionMapper.verifyByDefineName(projectId, jobName);
//        ProcessDefinition processDefinition = new InstanceTask().getProcessDefinitionId(jobName, instanceEnv.getProjectName());

        schedule.setProcessDefinitionId(String.valueOf(projectId));
        schedule.setProjectName(instanceEnv.getProjectName());
        return schedule;
    }
}
