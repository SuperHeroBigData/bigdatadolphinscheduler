package necibook.com.entity.shell;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ShellEntity
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/27 4:39 下午
 * @Version 1.0
 */

@Data
public class ShellEntity {
    /**
     * 全局参数
     */
    private ArrayList<String> globalParams=new ArrayList<>();
    /**
     * 任务
     */
    private List<ShellParameters> tasks;
    /**
     * 租户
     */
    private long tenantId;
    /**
     * 超时时间
     */
    private long timeout;

}
