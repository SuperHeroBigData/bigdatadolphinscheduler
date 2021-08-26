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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ShellTaskImpl
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/17 4:27 下午
 * @Version 1.0
 */
public class ShellTaskImpl extends AbstractTask {
    private final SheetParam sheet;
    private ShellParameters shellParameters;
    private final boolean flag;
    private ParentTask parentTask;
    public ShellTaskImpl(SheetParam sheet, List<Property> localParamsList, ParentTask parentTask, boolean flag) {
        super(sheet,parentTask);
//        this.resourceMapper=new ResourceMapperImpl();
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
        parentTask= super.convertToData();
        String resourceString = sheet.getResourceList();
        shellParameters=(ShellParameters) parentTask.getParams();
        if(!"".equals(resourceString))
        {
            String[] resource = resourceString.split(",");
            System.out.println(sheet.getResourceList()+"-----------"+resource.length+resource.toString());
            HashSet<ResourceInfo> resourcesSet = new HashSet<>();
            for (int i = 0; i < resource.length; i++) {
                ResourceMapper resourceMapper = (ResourceMapper) DBManager.setUp(ResourceMapper.class);
                List<ResourceInfo> resourceList = resourceMapper.queryResourceByfullName(resource[i]);
                if(!Objects.isNull(resourceList))
                {
                    resourcesSet.addAll(resourceList);
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
