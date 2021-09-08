package necibook.com;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import necibook.com.dolphinscheduler.exceptions.TasksException;
import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.easyexcel.ExcelListener;
import necibook.com.easyexcel.ProcessConfig;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.dsentity.Resource;
import necibook.com.enums.State;
import necibook.com.upgrade.BuildTask;
import necibook.com.utils.ConfigUtil;
import necibook.com.utils.Constants;
import necibook.com.utils.DateUtil;
import necibook.com.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnalysisApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisApplication.class);
    public static  int SHEET_INDEX = 0;
    public static final int HEAD_NUM = 2;
    public static final int ARGS_LEN = 4;
    public static SheetEnv sheetEnv;
    public static String JOB_DDL="CREATE";

    public static void main(String[] args) throws IllegalAccessException {
        LOGGER.info("test");
        //主程序参数控制（1、资源上传参数：资源全路径名 无效资源路径 2、工作流cud操作：excel全路径名 ddl操作 sheet页数）
        if (Objects.nonNull(args) && args.length < ARGS_LEN) {
            AnalysisApplication analysisApplication = new AnalysisApplication();
            analysisApplication.packageEnv();
            if(args.length==2)
            {
                LOGGER.info("资源上传");
                analysisApplication.resourceUpload(args[0],args[1]);
                LOGGER.info("资源{}上传完毕",args[0]);
            }else {
                LOGGER.info("工作流逻辑");
                analysisApplication.excelEnv(args[0], args[1], args[2]);
            }
        } else {
            LOGGER.error("参数数量错误");
            throw new IllegalArgumentException("传入参数为空!!!");
        }
    }
    /**
     * 获取当前环境参数
     *
     * @param filePath excel路径
     * @ddl ddl逻辑
     * @param sheet_index sheet页数
     */
    public void excelEnv(String filePath,String ddl,String sheet_index) throws IllegalAccessException {
        LOGGER.info("获取文件路径：{}", filePath);
        LOGGER.info("工作流逻辑：{}",ddl);
        String JOB_DDL = ddl.toUpperCase();
        sheetEnv.setJobDDL(JOB_DDL);
        List<ReadSheet> readSheets = EasyExcel.read(filePath).build().excelExecutor().sheetList();
        int sheet_nums = readSheets.size();
        if(Objects.isNull(sheet_index))
        {
            sheet_index=String.valueOf(sheet_nums);
        }
        if(Objects.nonNull(sheet_index)&&sheet_nums>=Integer.parseInt(sheet_index))
        {
            SHEET_INDEX=Integer.parseInt(sheet_index);
            excelParam(filePath);
        }else
        {
            LOGGER.error("传入sheet页参数超限，不符合要求，传入{}，总页数{}",sheet_index,sheet_nums);
            throw new IllegalAccessException("传入sheet页数大于表总页数");
        }
    }

    /**
     * 遍历解析封装任务流图
     *
     * @param filePath excel路径
     */
    public void excelParam(String filePath) {
        ExcelListener<SheetParam> listener = new ExcelListener<>();
        for (int i = 0; i < SHEET_INDEX; i++) {
            LOGGER.info("解析excel第{}页",i);
            switch (i)
            {
                case 0:
                    LOGGER.info("解析工作流配置参数，封装工作流信息及调度信息");
                    listener.readData(filePath, ProcessConfig.class,i,HEAD_NUM);
                    continue;
                default:
                    LOGGER.info("解析工作流图，绘制dag有向无环图");
                    listener.readData(filePath, SheetParam.class, i, HEAD_NUM);
                    continue;
            }

        }

    }

    /**
     * 封装sheetEnv环境信息
     */
    public void packageEnv()
    {
        sheetEnv=new SheetEnv();
        sheetEnv.setPresent(ConfigUtil.getProperty(Constants.PRESENT_ENV));
        sheetEnv.setUserName(ConfigUtil.getProperty(Constants.DS_USER_NAME));
        sheetEnv.setPassword(ConfigUtil.getProperty(Constants.DS_USER_PASSWD));
        sheetEnv.setIp(ConfigUtil.getProperty(Constants.DS_IP));
        sheetEnv.setPort(ConfigUtil.getProperty(Constants.DS_PORT));
        sheetEnv.setTenant(ConfigUtil.getProperty(Constants.DS_TENANT));
        sheetEnv.setDbType(ConfigUtil.getProperty(Constants.DS_DB_TYPE));
        sheetEnv.setDbIp(ConfigUtil.getProperty(Constants.DS_DB_IP));
        sheetEnv.setDbPort(ConfigUtil.getProperty(Constants.DS_DB_PORT));
        sheetEnv.setDbUser(ConfigUtil.getProperty(Constants.DB_USER));
        sheetEnv.setDbPassword(ConfigUtil.getProperty(Constants.DB_PASSWD));
        sheetEnv.setDbDatabase(ConfigUtil.getProperty(Constants.DB_DATABASE));
        sheetEnv.setProjectName(ConfigUtil.getProperty(Constants.projectName));
        sheetEnv.setReceivers(ConfigUtil.getProperty(Constants.RECEIVERS));
        sheetEnv.setReceiversCc(ConfigUtil.getProperty(Constants.DS_RECEIVERSCC));
        sheetEnv.setJobDDL(JOB_DDL);
    }
    /**
     * 资源中心的资源上传功能（资源中心路径=资源路径-无效路径）
     * @param resourcePath 资源路径
     * @param validResource 路径中的无效路径
     */
    public void resourceUpload(String resourcePath,String validResource)
    {
        if(!resourcePath.contains(validResource))
        {
            LOGGER.error("有效资源路径不包含非有效");
            throw new RuntimeException("有效参数不符合要求");
        }
        String fileName = resourcePath;
        Boolean isWindows=resourcePath.contains("\\\\");
        File file = new File(fileName);
        boolean matches = file.isFile();
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> dirNames=new ArrayList<>();
        if(!matches)
        {
            //资源文件遍历
            FileUtils.findFileALL(file,fileNames,dirNames);
        }else
        {

            fileNames.add(file.getPath());
            dirNames.add(file.getParent());
        }
        List<String> distinctDirs = dirNames.stream().distinct().collect(Collectors.toList());
        LOGGER.info("resourceDirs path is {}",distinctDirs);
        for (int i = 0; i < distinctDirs.size(); i++) {
            //资源路径
            String path = distinctDirs.get(i);
            int suffix = path.lastIndexOf(validResource)+validResource.length();
            String substring = distinctDirs.get(i).substring(suffix);
            String[] split=null;
            //判断是linux、windows环境
            if(isWindows)
            {
             split=substring .split("\\\\");
            }else {
             split = substring.split("/");
            }
            for (int j = 0; j < split.length; j++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("/");
                if(j==0)
                {
                    stringBuilder.append(split[j]);
                }else
                {
                    for (int k = 0; k <= j; k++) {
                        stringBuilder.append(split[k]).append("/");
                    }
                    stringBuilder.delete(stringBuilder.length()-1,stringBuilder.length());
                }
                Resource resource = new Resource();
                resource.setFullName(stringBuilder.toString());
                new BuildTask(AnalysisApplication.sheetEnv).createDirs(resource);
            }

       }
        LOGGER.info("fileNames path is {}",fileNames);
        //创建资源文件
        for (int i = 0; i < fileNames.size(); i++) {
            // 资源文件路径过滤
            String path = fileNames.get(i);
            int suffix = path.lastIndexOf(validResource)+validResource.length();

            String substring = null;
            if(isWindows) {
                substring=fileNames.get(i).substring(suffix).replace("\\", "/");
            }else {
                substring=fileNames.get(i).substring(suffix-1);
            }
            new AnalysisApplication().packageEnv();
            Resource resource = new Resource();
            LOGGER.info("fileNames :{}",fileNames.get(i));
            LOGGER.info("fullNames:{}",substring);
            resource.setFileName(fileNames.get(i));
            resource.setFullName(substring);
            Result result = new BuildTask(AnalysisApplication.sheetEnv).createResource(resource);
                String js = JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue);
                if (State.ERROR.equals(State.valueOf(result.getState().toUpperCase()))) {
                    LOGGER.error("执行结果报错:{}", js);
                    String message = String.format("excel解析报错。报错时间：%s, 项目名：%s, 任务名：%s, 报错信息：%s",
                            DateUtil.formatDate(new Date()),result.getProjectName(),result.getJobName(),result.getMsg());
                    //sendWeChatRobotTalkRisk(message);
                    throw new TasksException("当前执行失败："+result);
                }
                LOGGER.info("执行结果：{}",js);
        }
    }
}
