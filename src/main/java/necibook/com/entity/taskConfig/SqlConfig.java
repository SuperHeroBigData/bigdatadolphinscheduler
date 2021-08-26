package necibook.com.entity.taskConfig;

import lombok.Data;
import necibook.com.entity.TaskConfig;

/**
 * excel定义shellConfig
 *
 * @author franky
 * @date 2021-08-01 10:23
 **/
@Data
public class SqlConfig extends TaskConfig {
    private SqlCommon sqlCommon=new SqlCommon();
    private String connParams;
    private String sql;
    private String preStatements;
    private String postStatements;
}