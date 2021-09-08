package necibook.com.easyexcel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源中心文件上传配置
 *
 * @author franky
 * @date 2021/08/10 20:41
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceConfig {
    private String resourceDir;
    private String fileName;
    private String resourceCenterDir;
    private String resourceCenterName;
    private String resourceType;
    private String resourceDes;
    private String CRUD;
    private String isValid;
}
