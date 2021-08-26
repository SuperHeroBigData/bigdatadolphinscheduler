package necibook.com.entity.taskConfig;

import necibook.com.entity.TaskConfig;
import lombok.Data;

/**
 * excel定义shellConfig
 *
 * @author franky
 * @date 2021-08-01 10:23
 **/
@Data
public class DataXConfig extends TaskConfig {
    private FlinkCommon flinkCommon=new FlinkCommon();
    private String submit_url;
    private String others;
}