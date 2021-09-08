package necibook.com.permission.impl;

import com.alibaba.fastjson.JSON;
import necibook.com.dolphinscheduler.mapper.ResourceMapper;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.ParentTask;
import necibook.com.entity.dsentity.ResourceInfo;
import necibook.com.entity.shell.ShellParameters;
import necibook.com.entity.taskConfig.ShellConfig;
import necibook.com.process.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/** 节点参数封装
 * @author franky
 * @date 2021/08/17 14:40
 **/
public class ShellTaskImpl extends AbstractTask {
    private final SheetParam sheet;
    private ShellParameters shellParameters;
    private final boolean flag;
    private ParentTask parentTask;
    private  final Logger LOGGER = LoggerFactory.getLogger(ShellTaskImpl.class);
    public ShellTaskImpl(SheetParam sheet, List<Property> localParamsList, ParentTask parentTask, boolean flag) {
        super(sheet,parentTask);
        this.shellParameters=new ShellParameters();
        parentTask.setParams(this.shellParameters);
        this.shellParameters.setLocalParams(localParamsList);
        this.sheet = sheet;
        this.flag = flag;
    }

    /**
     * 封装shell类型参数
     *
     * @return
     */
    @Override
    public ParentTask convertToData() {
        LOGGER.info("shell节点参数封装，节点名",sheet.getSubApplication());
        parentTask= super.convertToData();
        String resourceString = sheet.getResourceList();
        shellParameters=(ShellParameters) parentTask.getParams();
        if(!"".equals(resourceString))
        {
            String[] resource = resourceString.split(",");
            HashSet<ResourceInfo> resourcesSet = new HashSet<>();
            for (int i = 0; i < resource.length; i++) {
                ResourceMapper resourceMapper = (ResourceMapper) DBManager.setUp(ResourceMapper.class);
                List<ResourceInfo> resourceList = resourceMapper.queryResourceByfullName(resource[i]);
                if(!Objects.isNull(resourceList))
                {
                    resourcesSet.addAll(resourceList);
                }else
                {
                    LOGGER.warn(sheet.getSubApplication()+"resourcelist:{} is not exits",resource[i]);
                }
            }
            ArrayList<ResourceInfo> totalResource = new ArrayList<>(resourcesSet);
            shellParameters.setResourceList(totalResource);
        }
        ShellConfig shellConfig = JSON.parseObject(sheet.getTaskConfig(),ShellConfig.class);
        shellParameters.setRawScript(shellConfig.getRawScript());
        parentTask.setParams(shellParameters);
        return parentTask;
    }
}
