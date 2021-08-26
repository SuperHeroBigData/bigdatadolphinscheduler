package necibook.com.dolphinscheduler.api;


import necibook.com.dolphinscheduler.pojo.Login;
import necibook.com.dolphinscheduler.pojo.Result;

/**
 * @author mujp
 */
public interface LoggerModel {
    /**
     * 查看task日志
     * @param login 登录信息
     * @param taskName 任务名
     * @return 执行状态
     */
   Result logClientService(Login login, String taskName);
}
