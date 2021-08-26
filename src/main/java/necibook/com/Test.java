package necibook.com;

import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.mapper.TenantMapper;
import necibook.com.dolphinscheduler.pojo.Cron;
import necibook.com.easyexcel.CommonConfig;
import necibook.com.entity.Location;
import necibook.com.entity.dsentity.ProcessDefinition;
import necibook.com.entity.dsentity.Resource;
import necibook.com.entity.dsentity.Tenant;
import necibook.com.upgrade.BuildTask;
import necibook.com.utils.FileUtils;
import necibook.com.utils.ParamUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static necibook.com.dolphinscheduler.utils.DBManager.RESOURCE;
import static necibook.com.dolphinscheduler.utils.DBManager.getProcessInstanceMapper;

/**
 * @author franky
 * @date 2021/08/17 14:40
 **/

public class Test {
    public static void main(String[] args) {
        new AnalysisApplication().packageEnv();
        InputStream is = null;
        SqlSession sqlSession = null;
        try {
            Properties processInstanceMapper = getProcessInstanceMapper(ParamUtils.getInstanceEnv());
            is = Resources.getResourceAsStream(RESOURCE);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is, processInstanceMapper);
            sqlSession = sqlSessionFactory.openSession(true);
            List<Tenant> hive = sqlSession.getMapper(TenantMapper.class).queryByTenantCode("hive");
            System.out.println(hive);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    public void test() {
//        String config="{\"preTasks\":\"\"}";
        String config = "{\"runFlag\":\"NORMAL\",\"maxRetryTimes\":\"1\", \"retryInterval\":\"4\",\"taskInstancePriority\":\"MEDIUM\",\"workerGroup\":\"default\",\"time_out\":{},\n" +
                "    \"preTasks\":\"ods0\"}";
        System.out.println(config);
        CommonConfig commonConfig = JSONObject.parseObject(config, CommonConfig.class);
        System.out.println(commonConfig);
    }

    @org.junit.jupiter.api.Test
    public void test1() {
        Location location = new Location();
        location.setNodenumber("1");
        location.setName("t1");
        location.setTargetarr("test1,test2");
        location.setX(1);
        location.setY(2);
        Location location1 = new Location();
        location1.setNodenumber("1");
        location1.setName("t1");
        location1.setTargetarr("test1,test2");
        location1.setX(1);
        location1.setY(2);
        String s = JSONObject.toJSONString(location);
        String tes = JSONObject.toJSONString("tes");
        String s11 = tes + ":" + s;
        String s1 = JSONObject.toJSONString(location1);
        String tes1 = JSONObject.toJSONString("tes1");
        String s22 = tes1 + ":" + s1;
        ArrayList<String> strings = new ArrayList<>();
        strings.add(s11);
        strings.add(s22);
        ProcessDefinition processDefinition = new ProcessDefinition();
        String s2 = strings.toString();
        processDefinition.setLocations("{" + s2.substring(1, s2.length() - 1) + "}");
        System.out.println(processDefinition.getLocations());
    }

    @org.junit.jupiter.api.Test
    public void startTimeJoson() throws ParseException {
        Cron cron = new Cron();
        Date parse = new SimpleDateFormat("yyyy-MM-dd").parse("2021-12-31");
        cron.setStartTime(parse);
        cron.setCrontab("0 0 2 * * ? *");
        String cronStr = JSONObject.toJSONString(cron);
        System.out.println(cronStr);
    }
    @org.junit.jupiter.api.Test

    public void resourceUploadTest()
    {
        String resourcePath="C:\\Users\\p\\Desktop\\test1";
        String validResource="C:\\Users\\p\\Desktop\\";

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
/*
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

    }*/
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
@org.junit.jupiter.api.Test
    public void test2()
    {
            String sql="C:-Users-p-Desktop-测试文档-test.sql";
            sql=sql.replaceAll("-","/");
        System.out.println(sql);
    }
}
