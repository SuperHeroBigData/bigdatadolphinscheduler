package necibook.com.entity.taskConfig;

import necibook.com.enums.ProgramType;
import lombok.Data;

/**
 * MapReduceCommon配置
 *
 * @author franky
 * @date 2021-08-03 15:51
 **/
@Data
public class MapreduceCommon {
    private String programType= ProgramType.JAVA.name();
}