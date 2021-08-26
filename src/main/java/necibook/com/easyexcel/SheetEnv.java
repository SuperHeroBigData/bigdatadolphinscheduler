package necibook.com.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @ClassName SheetEnv
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/28 2:14 下午
 * @Version 1.0
 */
@Data
public class SheetEnv {
    @ExcelProperty(value = "当前环境",index = 0)
    private String present;

    @ExcelProperty(value = "环境",index = 1)
    private String environment;

    @ExcelProperty(value = "登录用户",index = 2)
    private String userName;

    @ExcelProperty(value = "登录密码",index = 3)
    private String password;

    @ExcelProperty(value = "ip",index = 4)
    private String ip;

    @ExcelProperty(value = "端口",index = 5)
    private String port;

    @ExcelProperty(value = "租户",index = 6)
    private String tenant;

    @ExcelProperty(value = "meta类型",index = 7)
    private String dbType;

    @ExcelProperty(value = "metaIp",index = 8)
    private String dbIp;

    @ExcelProperty(value = "meta端口",index = 9)
    private String dbPort;

    @ExcelProperty(value = "meta用户",index = 10)
    private String dbUser;

    @ExcelProperty(value = "meta密码",index = 11)
    private String dbPassword;

    @ExcelProperty(value = "meta库名",index = 12)
    private String dbDatabase;

    @ExcelProperty(value = "项目名",index = 13)
    private String projectName;

    @ExcelProperty(value = "用户邮件",index = 14)
    private String receivers;

    @ExcelProperty(value = "抄送人员",index = 15)
    private String receiversCc;

    /**
     * 执行任务方式
     */
    private String jobDDL;

}
