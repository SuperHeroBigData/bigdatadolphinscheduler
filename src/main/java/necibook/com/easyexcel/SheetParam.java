package necibook.com.easyexcel;



import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @ClassName SheetOds
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/16 9:53 上午
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SheetParam {

    @ExcelProperty(value = "应用名",index=0)
    private String application;

    @ExcelProperty(value = "子应用名",index = 1)
    private String subApplication;
    @ExcelProperty(value = "任务类型",index = 2)
    private String taskType;

    @ExcelProperty(value = "任务描述",index = 3)
    private String description;

    @ExcelProperty(value = "负责人",index = 4)
    private String createdBy;

    @ExcelProperty(value = "节点任务参数",index = 5)
    private String taskConfig;

    @ExcelProperty(value = "任务参数",index = 6)
    private String taskParam;

    @ExcelProperty(value = "资源列表",index = 7)
    private String resourceList="";

    @ExcelProperty(value = "公共节点配置",index = 8)
    @Builder.Default
    private String commonConfig="";
    @ExcelProperty(value = "超时描述信息",index = 9)
    private String alarmMessage;
    @ExcelProperty(value = "是否有效",index = 10)
    private String validFlag="1";
    private int sheetNumber;
}
