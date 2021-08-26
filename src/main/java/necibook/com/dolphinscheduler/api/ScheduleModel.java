package necibook.com.dolphinscheduler.api;

import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.dolphinscheduler.pojo.Schedule;

/**
 * @ClassName Schedule
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/15 2:01 下午
 * @Version 1.0
 */
public interface ScheduleModel {
    /**
     * 创建定时工作流
     * @param schedule
     * @return
     */
    Result createSchedule(Schedule schedule);

    /**
     * 上线定时工作流
     * @param scheduleId
     * @param projectName
     * @return
     */
    Result onlineSchedule(long scheduleId, String projectName);

    /**
     * 下线定时工作流
     * @param scheduleId
     * @param projectName
     * @return
     */
    Result offlineSchedule(long scheduleId, String projectName);

    /**
     * 更新定时工作流时间
     * @param schedule
     * @return
     */
    Result updateSchedule(Schedule schedule);
}
