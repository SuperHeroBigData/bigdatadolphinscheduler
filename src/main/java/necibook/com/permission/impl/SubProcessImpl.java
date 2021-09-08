package necibook.com.permission.impl;

import com.alibaba.fastjson.JSON;
import necibook.com.dolphinscheduler.mapper.ProcessDefinitionMapper;
import necibook.com.dolphinscheduler.mapper.ProjectMapper;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.ParentTask;
import necibook.com.entity.dsentity.Project;
import necibook.com.entity.subprocess.SubProcessParameters;
import necibook.com.entity.taskConfig.SubprocessConfig;
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
public class SubProcessImpl extends AbstractTask{
    private static final Logger LOGGER = LoggerFactory.getLogger(SubProcessImpl.class);
    private final SheetParam sheet;
    private SubProcessParameters subProcessParameters;
    private ParentTask parentTask;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final ProjectMapper projectMapper;
    public SubProcessImpl(SheetParam sheet, List<Property> localParamsList, ParentTask parentTask, boolean flag) {
        super(sheet,parentTask);
        processDefinitionMapper=(ProcessDefinitionMapper) DBManager.setUp(ProcessDefinitionMapper.class);
        projectMapper=(ProjectMapper) DBManager.setUp(ProjectMapper.class);
        this.subProcessParameters=new SubProcessParameters();
        parentTask.setParams(this.subProcessParameters);
        this.sheet = sheet;
    }

    /**
     * 封装shell类型参数
     *
     * @return
     */
    @Override
    public ParentTask convertToData() {
        parentTask= super.convertToData();
        LOGGER.info("子工作流节点封装,节点名{}",sheet.getSubApplication());
        subProcessParameters=(SubProcessParameters) parentTask.getParams();
        SubprocessConfig subprocessConfig = JSON.parseObject(sheet.getTaskConfig(), SubprocessConfig.class);
        Project project = projectMapper.queryByName(sheet.getApplication());
        if(Objects.isNull(project))
        {
            LOGGER.error("当前节点{},子工作流{}当前项目中不存在，请检查",sheet.getSubApplication(),subprocessConfig.getProcessDefinitionId());
        }
        int project_id = project.getId();
        System.out.println(project_id);
        int process_id = processDefinitionMapper.queryByDefineName(project_id, subprocessConfig.getProcessDefinitionId());
        if(Objects.isNull(process_id))
        {
            LOGGER.error("当前任务{}，子工作流{}不存在",sheet.getSubApplication(),subprocessConfig.getProcessDefinitionId());
        }
        subProcessParameters.setProcessDefinitionId(process_id);
        parentTask.setParams(subProcessParameters);
        return parentTask;
    }

}
