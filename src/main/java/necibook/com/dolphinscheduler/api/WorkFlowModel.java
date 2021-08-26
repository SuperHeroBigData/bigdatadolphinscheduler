package necibook.com.dolphinscheduler.api;


import necibook.com.dolphinscheduler.pojo.LineState;
import necibook.com.dolphinscheduler.pojo.ProcessDefinition;
import necibook.com.dolphinscheduler.pojo.Result;

/**
 * @author mujp
 */
public interface WorkFlowModel {
    /**
     * 创建工作流
     * @param processDefinitionJson
     * @return
     */
    Result createWorkFlow(ProcessDefinition processDefinitionJson);

    /**
     * 提交工作流
     * @param loginUser
     * @param loginPassword
     * @param hostName
     * @param receivers 预警邮件多个用 “，” 隔开
     * @param jobName
     * @param projectName
     * @return
     */
    Result startJob(String loginUser, String loginPassword, String hostName, String receivers, String jobName, String projectName);

    /**
     * 作业部署状态
     * @param lineState
     * @param result
     * @param hostName
     *  @return
     */
    Result releaseState(LineState lineState, Result result, String hostName);

    /**
     * 更新任务
     * @param processDefinition
     * @return
     */
    Result updateProcessDefinition(ProcessDefinition processDefinition);

    /**
     * 删除任务
     * @param taskName
     * @return
     */
    Result deleteProcessDefinition(String taskName);

}
