package necibook.com.entity;

import lombok.Data;

/**
 * @ClassName Connects
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/1 7:44 下午
 * @Version 1.0
 */
@Data
public class Connects {
    /**
     * 起始节点x
     */
   private String endPointSourceId;
    /**
     * 目标节点
     */
   private String endPointTargetId;
}
