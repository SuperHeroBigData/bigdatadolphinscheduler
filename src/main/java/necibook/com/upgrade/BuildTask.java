package necibook.com.upgrade;

import necibook.com.dolphinscheduler.api.Authenticator;
import necibook.com.dolphinscheduler.api.impl.AuthenticatorImpl;
import necibook.com.dolphinscheduler.api.impl.ResourceModelImpl;
import necibook.com.dolphinscheduler.api.impl.SchedulerImpl;
import necibook.com.dolphinscheduler.api.impl.WorkFlowModelImpl;
import necibook.com.dolphinscheduler.pojo.ProcessDefinition;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.dolphinscheduler.pojo.Schedule;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.entity.dsentity.Resource;

/**
 * @ClassName BuildTask
 * @Description TODO 构建任务
 * @Author jianping.mu
 * @Date 2020/11/26 11:10 上午
 * @Version 1.0
 */
public class BuildTask {

    private final SheetEnv login;
    private final Authenticator authenticator = new AuthenticatorImpl();
    public BuildTask(SheetEnv login) {
        this.login = login;
    }

    public Result createWork(ProcessDefinition processDefinition){
        return new WorkFlowModelImpl(login,authenticator).createWorkFlow(processDefinition);
    }

    public Result updateWork(ProcessDefinition processDefinition){
        return new WorkFlowModelImpl(login,authenticator).updateProcessDefinition(processDefinition);
    }

    public Result batchDeleteWork(String projectName,String taskName){
        return new WorkFlowModelImpl(login,authenticator).deleteProcessDefinition(projectName,taskName);
    }

    public Result createWorkSchedule(Schedule schedule){
        return new SchedulerImpl(login,authenticator).createSchedule(schedule);
    }

    public Result updateWorkSchedule(Schedule schedule){
        return new SchedulerImpl(login,authenticator).updateSchedule(schedule);
    }
    public Result createDirs(Resource resource)
    {
        return new ResourceModelImpl(login,authenticator).createUpdateResourceDirs(resource);
    }
    public Result createResource(Resource resource)
    {
        return new ResourceModelImpl(login,authenticator).createUpdateResource(resource);
    }
    public Result updateResource(Resource resource)
    {
        return new ResourceModelImpl(login,authenticator).updateResource(resource);
    }
    public Result deleteResource(Resource resource)
    {
        return new ResourceModelImpl(login,authenticator).deleteResource(resource);
    }

}
