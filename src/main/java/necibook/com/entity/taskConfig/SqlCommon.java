package necibook.com.entity.taskConfig;

import lombok.Data;

/**
 * Sql节点公有配置
 *
 * @author franky
 * @date 2021-08-03 12:57
 **/
@Data
public class SqlCommon {
    private String type= "mysql";
    private String data_source="study";
    private String udfs="";
    private String sqlType="0";
    private String title="";
    private boolean sendEmail=false;
    private String receivers="";
    private String receiversCc="";
    private String showType="TABLE";

}