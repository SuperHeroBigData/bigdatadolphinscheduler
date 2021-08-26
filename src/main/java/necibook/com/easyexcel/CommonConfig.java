package necibook.com.easyexcel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import necibook.com.entity.TimeOut;
import necibook.com.enums.Priority;

/**
 * 节点任务公有信息
 *
 * @author franky
 * @date 2021-08-01 16:12
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonConfig {

    private String RunFlag="NORMAL";

    private String maxRetryTimes="3";
    private int retryInterval=30;

    private String taskInstancePriority= Priority.MEDIUM.name();

    private String workerGroup="default";
    private TimeOut time_out=new TimeOut();
    private String preTasks="";
}