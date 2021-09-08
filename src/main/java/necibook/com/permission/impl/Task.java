package necibook.com.permission.impl;

import com.alibaba.fastjson.JSONObject;
import necibook.com.easyexcel.CommonConfig;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.ParentTask;
import necibook.com.enums.Priority;
import necibook.com.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static necibook.com.dolphinscheduler.utils.RandomUtil.taskId;

abstract class AbstractTask {
    private final SheetParam sheet;
    private final ParentTask parentTask;
    private final CommonConfig commonConfig;
    public AbstractTask(SheetParam sheet, ParentTask parentTask) {
        this.parentTask = parentTask;
        this.sheet = sheet;
        String commonConfig = sheet.getCommonConfig();
        if(Objects.isNull(commonConfig))
        {
            commonConfig="";
        }
        CommonConfig config = JSONObject.parseObject(commonConfig,CommonConfig.class);
        if(Objects.isNull(config))
        {
            config=new CommonConfig();
        }
        this.commonConfig =config;
    }

    public ParentTask convertToData() {
        String taskId = taskId(Constants.RANDOM_ID);
        parentTask.setId(taskId);
        parentTask.setDescription(sheet.getDescription());
        parentTask.setType(sheet.getTaskType().toUpperCase());
        parentTask.setName(sheet.getSubApplication());
        parentTask.setMaxRetryTimes(Integer.parseInt(commonConfig.getMaxRetryTimes()));
        parentTask.setRunFlag(commonConfig.getRunFlag());
        parentTask.setRetryInterval(commonConfig.getRetryInterval());
        parentTask.setTaskInstancePriority(Priority.valueOf(commonConfig.getTaskInstancePriority()));
        parentTask.setWorkerGroup(commonConfig.getWorkerGroup());
        parentTask.setTimeout(commonConfig.getTime_out());
        parentTask.setPreTasks(Arrays.asList(commonConfig.getPreTasks().split(",")));
        return parentTask;
    }

    public List<String> getPreTask(){
        List<String> preTasks = new ArrayList<>();
       preTasks= Arrays.asList(commonConfig.getPreTasks().split(","));
        return preTasks;
    }

}


