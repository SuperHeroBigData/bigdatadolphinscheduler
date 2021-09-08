package necibook.com.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.alibaba.excel.util.DateUtils;
import lombok.SneakyThrows;
import necibook.com.AnalysisApplication;
import necibook.com.dolphinscheduler.mapper.TenantMapper;
import necibook.com.dolphinscheduler.mapper.WorkerGroupMapper;
import necibook.com.dolphinscheduler.pojo.Cron;
import necibook.com.dolphinscheduler.pojo.ProcessDefinition;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.dolphinscheduler.pojo.Schedule;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.entity.dsentity.Tenant;
import necibook.com.entity.dsentity.WorkerGroup;
import necibook.com.permission.impl.ParamConvert;
import necibook.com.permission.impl.SubProcessTaskImpl;
import necibook.com.upgrade.BuildTask;
import necibook.com.utils.Constants;
import necibook.com.utils.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class ExcelListener<T> extends AnalysisEventListener<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelListener.class);
    private static final List<SheetEnv> SHEET_ENV_LIST = new ArrayList<>();
    private static final StringBuilder JOB_NAME = new StringBuilder(10);
    public static HashMap<String,ProcessDefinition> processMap=new HashMap<>();
    public static HashMap<String,Schedule> scheduleMap=new HashMap<>();
    /**
     * 读取excel头
     *
     * @param filePath 文件路径
     * @param cla      对象
     */
    public void readExcelHead(String filePath, Class<?> cla) {
        EasyExcel.read(filePath, cla, new ExcelListener<>()).sheet().doRead();
    }


    /**
     * 根据规则一行一行读取数据
     *
     * @param filePath 文件路径
     * @param cla      对象
     * @param sheetNum sheet页
     * @param headNum  文件头行
     */
    public void readData(String filePath, Class<?> cla, int sheetNum, int headNum) {
        EasyExcel.read(filePath, cla, new ExcelListener<>()).sheet(sheetNum)
                .headRowNumber(headNum).doRead();
    }

    /**
     * 读取excel表头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        LOGGER.info("读取excel表头信息：{}", headMap);
    }

    /**
     * 读取excel内容
     *
     * @param t       解析对象
     * @param context excel文件信息
     */
    @SneakyThrows
    @Override
    public void invoke(T t, AnalysisContext context) {
    if(t instanceof ProcessConfig)
        {
            //执行工作流参数配置逻辑
            ProcessConfig processConfig=(ProcessConfig) t;
            ProcessDefinition processDefinition = new ProcessDefinition();
            processDefinition.setProjectName(processConfig.getProjectName());
            processDefinition.setDescription(processConfig.getDescription());
            processDefinition.setGlobalParamList(ParamUtils.taskParamToList(processConfig.getGlobalParams()));
            TenantMapper tenantMapper = (TenantMapper) DBManager.setUp(TenantMapper.class);
            List<Tenant> tenants = tenantMapper.queryByTenantCode(processConfig.getTenantId());
            if(tenants.isEmpty())
            {
                LOGGER.error("当前租户{}ds中不存在",processConfig.getTenantId());
                throw new RuntimeException("当前租户不存在");
            }
            int id = tenants.get(0).getId();
            processDefinition.setName(processConfig.getProcessName());
            processDefinition.setTenantId(id);
            processDefinition.setTimeout(processConfig.getTimeOut());
            processDefinition.setDescription(processConfig.getDescription());
            processDefinition.setReceivers(processConfig.getReciever());
            processDefinition.setReceiversCc(processConfig.getCcReciever());
            if(!processConfig.getCron().equals(""))
            {
                Schedule schedule=new Schedule();
                schedule.setFailureStrategy(processConfig.getFailureStrategy());
                schedule.setProcessInstancePriority(processConfig.getPriority());
                Cron cron = new Cron();
                cron.setStartTime(DateUtils.parseDate(processConfig.getStartTime()));
                cron.setEndTime(DateUtils.parseDate(processConfig.getEndTime()));
                cron.setCrontab(processConfig.getCron());
                schedule.setSchedule(cron);
                schedule.setProjectName(processConfig.getProjectName());
                schedule.setReceivers(processConfig.getReciever());
                schedule.setReceiversCc(processConfig.getCcReciever());
                schedule.setWarningGroupId(processConfig.getAlermGroup());
                schedule.setWarningType(processConfig.getWarningType());
                schedule.setWorkerGroupId(1L);
                WorkerGroupMapper workerGroupMapper = (WorkerGroupMapper) DBManager.setUp(WorkerGroupMapper.class);
                if(!"default".equals(processConfig.getWorkerGroup()))
                {
                    try {
                        WorkerGroup workerGroup = workerGroupMapper.queryWorkerGroupByName(processConfig.getWorkerGroup()).get(0);
                        schedule.setWorkerGroupId(workerGroup.getId());
                    }catch (Exception e)
                    {
                        LOGGER.error("当前告警组不存在，告警组{}",processConfig.getWorkerGroup());
                        e.printStackTrace();
                        throw new RuntimeException("工作组不存在");
                    }
                }
                scheduleMap.put(processConfig.getProcessName(),schedule);
            }

            //工作流及定时调度信息放于map缓存中
            processMap.put(processConfig.getProcessName(),processDefinition);
        }else {
            //执行工作流封装逻辑
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        convert(t, readSheetHolder.getSheetNo());}
    }


    /**
     * 解析完成后实现依赖功能
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        String sheetName = readSheetHolder.getSheetName();
        Integer sheetNo = readSheetHolder.getSheetNo();
        LOGGER.info("Sheet页名字：{}，Sheet页下标：{}",
                sheetName, sheetNo + "。读取完毕！！！");
        if(AnalysisApplication.sheetEnv.getJobDDL().toUpperCase().equals("DELETE"))
        {
            Collection<ProcessDefinition> values = processMap.values();
            for (ProcessDefinition value : values) {
                String projectName = value.getProjectName();
                String processName = value.getName();
                Result result = new BuildTask(AnalysisApplication.sheetEnv).batchDeleteWork(projectName, processName);
                if(result.getState().equals("SUCCESS"))
                {
                    LOGGER.info("当前项目：{}，工作流{} 删除成功",projectName,processName);
                }else
                {
                    LOGGER.error("当前项目：{}，工作流{} 删除失败",projectName,processName);
                    LOGGER.error("失败信息"+result.toString());
                    throw new RuntimeException("删除任务失败");
                }
            }
            return;
        }
        //TODO 按照依赖关系生成Job
        if (sheetNo >= Constants.SHEET_NO && !AnalysisApplication.sheetEnv.getJobDDL().toUpperCase().equals("DELETE")) {
            String jobNames =sheetName;
            LOGGER.info("开始生成工作流{}",jobNames);
            ProcessDefinition processDefinition = processMap.get(jobNames);
            processDefinition.setName(jobNames);
            new SubProcessTaskImpl().getTaskParam(processDefinition);
            LOGGER.info("Job名字是：{}", jobNames + ",已执行完毕！！！");
            //清空临时缓存表
            ParamConvert.locationsRam.clear();
            ParamConvert.taskRam.clear();
            ParamConvert.tasksMap.clear();
            ParamConvert.connectsList.clear();
        }
    }

    /**
     * 执行有向无环图绘制
     *
     * @param data
     * @param sheetNumber
     */
    public void convert(T data, int sheetNumber) {
        if (data instanceof SheetParam) {
            SheetParam sheetParam = (SheetParam) data;
            sheetParam.setSheetNumber(sheetNumber);
            new ParamConvert(sheetParam);
        }
    }

}
