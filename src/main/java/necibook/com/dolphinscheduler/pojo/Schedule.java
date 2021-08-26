package necibook.com.dolphinscheduler.pojo;

import lombok.Data;

/**
 * @ClassName Schdule
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/15 2:16 下午
 * @Version 1.0
 */
@Data
public class Schedule {
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 失败策略,可用值:END,CONTINUE
     */
    private String failureStrategy;

    /**
     * 流程实例优先级,可用值:HIGHEST,HIGH,MEDIUM,LOW,LOWEST
     */
    private String processInstancePriority;

    /**
     * 收件人 多个空格分开
     */
    private String receivers;

    /**
     * 收件人(抄送)
     */
    private String receiversCc;

    /**
     * 定时
     */
    private Cron schedule;

    /**
     * 发送组ID
     */
    private String warningGroupId="1";
    /**
     * 发送策略,可用值:NONE,SUCCESS,FAILURE,ALL
     */
    private String warningType;

    /**
     * Worker Server分组ID
     */
    private long workerGroupId;

}
