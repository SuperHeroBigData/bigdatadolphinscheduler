package necibook.com.permission.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
    private final Dependence dependence;
    private final boolean flag;
    private ParentTask parentTask;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final ProjectMapper projectMapper;
    public DependTaskImpl(SheetParam sheet, List<Property> localParamsList, ParentTask parentTask, boolean flag) {
        super(sheet,parentTask);

        processDefinitionMapper=(ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
        projectMapper=(ProjectMapper) DBManager.setUp(ProjectMapper.class);
//        this.resourceMapper=new ResourceMapperImpl();
        this.dependence=new Dependence();
        this.sheet = sheet;
        this.flag = flag;
    }
    /**
     * 封装dep类型参数
     *
     * @return
     */
    @Override
    public ParentTask convertToData() {
        parentTask= super.convertToData();
        String taskConfig = sheet.getTaskConfig();
/*        JSONObject jsonObject1 = JSONObject.parseObject(taskConfig);
        System.out.println(jsonObject1);*/
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
                    int prodf = processDefinitionMapper.queryByDefineName(projectId, processName).getId();
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
