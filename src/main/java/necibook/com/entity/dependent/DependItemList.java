package necibook.com.entity.dependent;

import lombok.Data;

/**
 * @ClassName DependentTask
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/24 7:14 下午
 * @Version 1.0
 */
@Data
public class DependItemList {

    /**
     * 项目Id
     */
    private long projectId;
    /**
     * 作业Id
     */
    private long definitionId;
    /**
     * 依赖任务id
     */
    private String depTasks = "ALL";
    /**
     * 时间单位
     */
    private String cycle = "day";

    /**
     * 依赖日期
     */
    private String dateValue = "today";
}
