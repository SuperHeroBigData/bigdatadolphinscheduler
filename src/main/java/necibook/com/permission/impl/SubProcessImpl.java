package necibook.com.permission.impl;

import com.alibaba.fastjson.JSON;
import necibook.com.dolphinscheduler.mapper.ProcessDefinitionMapper;
import necibook.com.dolphinscheduler.mapper.ProjectMapper;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.ParentTask;
import necibook.com.entity.subprocess.SubProcessParameters;
import necibook.com.entity.taskConfig.SubprocessConfig;
import necibook.com.process.Property;

import java.util.List;

/**
 * 子工作流封装类
 *
 * @author franky
 * @date 2021/08/25 14:21
 **/
public class SubProcessImpl extends AbstractTask{
    private final SheetParam sheet;
    private SubProcessParameters subProcessParameters;
    private final boolean flag;
    private ParentTask parentTask;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final ProjectMapper projectMapper;
    public SubProcessImpl(SheetParam sheet, List<Property> localParamsList, ParentTask parentTask, boolean flag) {
        super(sheet,parentTask);
        processDefinitionMapper=(ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
        projectMapper=(ProjectMapper) DBManager.setUp(ProjectMapper.class);
//        this.resourceMapper=new ResourceMapperImpl();
        this.subProcessParameters=new SubProcessParameters();
        parentTask.setParams(this.subProcessParameters);
        this.sheet = sheet;
        this.flag = flag;
    }

    /**
     * 封装shell类型参数
     *
     * @return
     */
    @Override
    public ParentTask convertToData() {
        parentTask= super.convertToData();
        subProcessParameters=(SubProcessParameters) parentTask.getParams();
        SubprocessConfig subprocessConfig = JSON.parseObject(sheet.getTaskConfig(), SubprocessConfig.class);
        int project_id = projectMapper.queryByName(sheet.getApplication()).getId();
        int process_id = processDefinitionMapper.queryByDefineName(project_id, subprocessConfig.getProcessDefinitionId()).getId();
        subProcessParameters.setProcessDefinitionId(process_id);
        parentTask.setParams(subProcessParameters);
        return parentTask;
    }

}
