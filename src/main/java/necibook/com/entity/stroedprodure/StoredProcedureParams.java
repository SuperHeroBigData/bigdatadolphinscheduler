package necibook.com.entity.stroedprodure;

import necibook.com.dolphinscheduler.pojo.LocalParams;
import necibook.com.enums.DatabaseType;
import lombok.Data;

import java.util.List;

/**
 * @ClassName StoredProcedureParams
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/29 6:54 下午
 * @Version 1.0
 */
@Data
public class StoredProcedureParams {
    /**
     * 数据库类型
     */
    private DatabaseType type;
    /**
     * 数据源Id
     */
    private long datasource;
    /**
     * 存储过程main()
     */
    private String method = "";
    /**
     * 本地参数
     */
    private List<LocalParams> localParams;
}
