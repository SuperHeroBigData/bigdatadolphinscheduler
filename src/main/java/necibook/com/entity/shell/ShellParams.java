package necibook.com.entity.shell;

import necibook.com.entity.LocalParams;
import necibook.com.entity.dsentity.ResourceInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName Params
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/27 3:47 下午
 * @Version 1.0
 */
@Data
public class ShellParams {

    private List<ResourceInfo> resourceList;
    /**
     * 本地参数
     */
    private List<LocalParams> localParams;
    /**
     * shell脚本
     */
    private String rawScript;
}
