package necibook.com;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.ReadSheet;
import necibook.com.easyexcel.*;
import necibook.com.entity.dsentity.Resource;
import necibook.com.upgrade.BuildTask;
import necibook.com.utils.ConfigUtil;
import necibook.com.utils.Constants;
import necibook.com.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnalysisApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisApplication.class);
    public static  int SHEET_INDEX = 0;
    public static final int HEAD_NUM = 2;
    public static final int ARGS_LEN = 4;
    public static final int ENV_INDEX = 0;
    public static SheetEnv sheetEnv;
    public static String JOB_DDL="create";

    public static void main(String[] args) throws IllegalAccessException {

        //实现excel写操作
        if (Objects.nonNull(args) && args.length < ARGS_LEN) {
            AnalysisApplication analysisApplication = new AnalysisApplication();
            analysisApplication.packageEnv();
            if(args.length==2)
            {
                analysisApplication.resourceUpload(args[0],args[1]);
            }else {
                analysisApplication.excelEnv(args[0], args[1], args[2]);
            }
        } else {
            throw new IllegalArgumentException("传入参数为空!!!");
        }
    }

    /**
     * 获取当前环境参数
     *
     * @param filePath excel路径
     */
    public void excelEnv(String filePath,String ddl,String sheet_index) throws IllegalAccessException {
        LOGGER.info("获取文件路径：{}", filePath);
        LOGGER.info("工作流逻辑：{}",JOB_DDL);
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
     * 遍历依赖任务
     *
     * @param filePath excel路径
     */
    public void excelParam(String filePath) {
        ExcelListener<SheetParam> listener = new ExcelListener<>();
        for (int i = 0; i < SHEET_INDEX; i++) {
            LOGGER.info("解析excel第{}页，封装工作流",i);
            if(i==0)
            {
                continue;
            }

            switch (i)
            {
                case 0:
                    listener.readData(filePath, ResourceConfig.class,i,HEAD_NUM);
                    continue;
                case 1:
                    listener.readData(filePath, ProcessConfig.class,i,HEAD_NUM);
                    continue;
                default:
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
     * 资源中心的资源上传功能
     * @param resourcePath 资源路径
     */
    public void resourceUpload(String resourcePath,String validResource)
    {

        String fileName = resourcePath;
        boolean matches = Pattern.matches("\\*.*", fileName);
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> dirNames=new ArrayList<>();
        if(!matches)
        {
            //资源文件遍历
            FileUtils.findFileALL(new File(fileName),fileNames,dirNames);
        }else
        {
            File file = new File(fileName);
            fileNames.add(file.getName());
        }
        System.out.println(fileNames);
        System.out.println(dirNames);
        List<String> distinctDirs = dirNames.stream().distinct().collect(Collectors.toList());
        for (int i = 0; i < distinctDirs.size(); i++) {
            String path = distinctDirs.get(i);
            System.out.println(path.contains(validResource));
            System.out.println(path.indexOf(validResource));
            int suffix = path.lastIndexOf(validResource)+validResource.length();
            String substring = distinctDirs.get(i).substring(suffix);
            System.out.println("路径"+substring);
            String[] split =substring .split("\\\\");
            System.out.println(split);
            new AnalysisApplication().packageEnv();
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
                System.out.println(stringBuilder.toString());
                Resource resource = new Resource();
                resource.setFullName(stringBuilder.toString());
                new BuildTask(AnalysisApplication.sheetEnv).createDirs(resource);
            }

       }
        //创建资源文件
        for (int i = 0; i < fileNames.size(); i++) {
            System.out.println(fileNames.get(i));
            // 资源文件路径过滤
            String path = fileNames.get(i);
            System.out.println(path.contains(validResource));
            System.out.println(path.indexOf(validResource));
            int suffix = path.lastIndexOf(validResource)+validResource.length();
            String substring = fileNames.get(i).substring(suffix-1).replace("\\","/");
            System.out.println("路径"+substring);
            new AnalysisApplication().packageEnv();
            Resource resource = new Resource();
            System.out.println("文件全限定名"+fileNames.get(i));
            resource.setFileName(fileNames.get(i));
            resource.setFullName(substring);
            new BuildTask(AnalysisApplication.sheetEnv).createResource(resource);
    }
    }
}
