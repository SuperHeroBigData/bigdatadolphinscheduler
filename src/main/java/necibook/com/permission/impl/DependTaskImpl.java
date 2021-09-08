package necibook.com.permission.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.exceptions.TasksException;
import necibook.com.dolphinscheduler.mapper.ProcessDefinitionMapper;
import necibook.com.dolphinscheduler.mapper.ProjectMapper;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.AbstractParameters;
import necibook.com.entity.ParentTask;
import necibook.com.entity.dependent.DependItemListName;
import necibook.com.entity.dependent.DependTaskListName;
import necibook.com.entity.dependent.Dependence;
import necibook.com.entity.dependent.DependenceName;
import necibook.com.process.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * 子工作流封装类
 *
 * @author franky
 * @date 2021/08/25 14:21
 **/
public class DependTaskImpl extends AbstractTask{
    private final SheetParam sheet;
    private ParentTask parentTask;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final ProjectMapper projectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(DependTaskImpl.class);
    public DependTaskImpl(SheetParam sheet, List<Property> localParamsList, ParentTask parentTask, boolean flag) {
        super(sheet,parentTask);
        processDefinitionMapper=(ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
        projectMapper=(ProjectMapper) DBManager.setUp(ProjectMapper.class);
        this.sheet = sheet;
    }
    /**
     * 封装dep类型参数
     *
     * @return
     */
    @Override
    public ParentTask convertToData() {
        parentTask= super.convertToData();
        LOGGER.info("依赖节点参数封装,节点名{}",sheet.getSubApplication());
        String taskConfig = sheet.getTaskConfig();
        Dependence dependence=new Dependence();
        DependenceName dependenceName = JSONObject.parseObject(taskConfig,DependenceName.class);
        if(!Objects.isNull(dependenceName))
        {
            //流程id、项目id转换完毕后
            List<DependTaskListName> dependTaskList = dependenceName.getDependTaskList();
            for (int i = 0; i < dependTaskList.size(); i++) {
                DependTaskListName dependTaskListName = dependTaskList.get(i);
                List<DependItemListName> dependItemList = dependTaskListName.getDependItemList();
                for (int j = 0; j < dependItemList.size(); j++) {
                    DependItemListName dependItemListName = dependItemList.get(j);
                    String projectName = dependItemListName.getProjectName();
                    String processName = dependItemListName.getProcessName();
                    int projectId = projectMapper.queryByName(projectName).getId();
                    int prodf = processDefinitionMapper.queryByDefineName(projectId, processName);
                    if(Objects.isNull(projectId) ||Objects.isNull(prodf))
                    {
                        LOGGER.error("依赖节点任务：{},依赖作业：{},作业项目{} 不存在",sheet.getSubApplication(),projectName,processName);
                        throw new TasksException(sheet.getSubApplication()+"配置有误");
                    }
                    dependItemListName.setProcessName(String.valueOf(prodf));
                    dependItemListName.setProjectName(String.valueOf(projectId));
                    dependItemList.set(j,dependItemListName);
                }
                dependTaskListName.setDependItemList(dependItemList);
                dependTaskList.set(i,dependTaskListName);
            }
            dependenceName.setDependTaskList(dependTaskList);
            //中间json对象转换
            String json = JSON.toJSONString(dependenceName);
            String replace = json.replace("projectName", "projectId").replace("processName", "definitionId");
            dependence = JSONObject.parseObject(replace, Dependence.class);
        }
        String s = JSONObject.toJSONString(dependence);
        JSONObject jsonObject = JSON.parseObject(s);
        parentTask.setParams(new AbstractParameters() {
            @Override
            public boolean checkParameters() {
                return false;
            }
        });
        parentTask.setDependence(jsonObject);
        return parentTask;
    }
}
