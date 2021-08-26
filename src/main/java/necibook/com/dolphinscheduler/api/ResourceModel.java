package necibook.com.dolphinscheduler.api;

import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.entity.dsentity.Resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface ResourceModel {
    /**
     * 创建资源表
     * @param resource
     * @return
     */
    Result createUpdateResource(Resource resource) throws IOException;
    /**
     * 更新资源
     * @param
     * @return
     */
    Result updateResource(Resource resource);

    /**
     * 删除资源
     * @param resource
     * @return
     */
    Result deleteResource(Resource resource);

    /**
     * 资源目录的upinsert操作
     * @param resource
     * @return
     */
    Result createUpdateResourceDirs(Resource resource);
}
