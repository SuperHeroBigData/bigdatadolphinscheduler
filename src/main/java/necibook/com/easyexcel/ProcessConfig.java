package necibook.com.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import necibook.com.enums.FailureStrategy;
import necibook.com.enums.Priority;
import necibook.com.enums.WarningType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 工作流配置定义
 *
 * @author franky
 * @date 2021/08/10 20:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessConfig {
    @ExcelProperty(value = "项目名",index =0)
    private String projectName;
    @ExcelProperty(value = "工作流名称",index=1)
    private String processName;
    @ExcelProperty(value = "工作流租户",index=2)
    private String tenantId;
    @ExcelProperty(value = "工作流描述",index=3)
    private String description="";
    @ExcelProperty(value = "超时告警",index=4)
    private int timeOut=0;
    @ExcelProperty(value = "全局变量",index=5)
    private String globalParams="";
    @ExcelProperty(value = "定时CRON",index=6)
    private String cron="";
    @ExcelProperty(value = "开始时间",index=7)
    private String startTime="2021-08-31";
    @ExcelProperty(value = "结束时间",index=8)
    private String endTime="2025-12-31";
    @ExcelProperty(value = "失败策略",index=9)
    private String failureStrategy= FailureStrategy.CONTINUE.toString();
    @ExcelProperty(value = "告警策略",index=10)
    private String warningType= WarningType.ALL.toString();
    @ExcelProperty(value = "告警信息",index=11)
    private String alermMes="";
    @ExcelProperty(value = "优先级",index=12)
    private String priority= Priority.MEDIUM.toString();
    @ExcelProperty(value = "worker分组",index=13)
    private String workerGroup="";
    @ExcelProperty(value = "通知组",index=14)
    private String alermGroup="";
    @ExcelProperty(value = "收件人",index=15)
    private String reciever="";
    @ExcelProperty(value = "抄送人",index=16)
    private String ccReciever="";
}
