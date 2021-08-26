package necibook.com.permission.impl;

import com.alibaba.fastjson.JSONObject;
import necibook.com.dolphinscheduler.mapper.DataSourceMapper;
import necibook.com.dolphinscheduler.utils.DBManager;
import necibook.com.easyexcel.SheetParam;
import necibook.com.entity.ParentTask;
import necibook.com.entity.sql.SqlParameters;
import necibook.com.entity.taskConfig.SqlCommon;
import necibook.com.entity.taskConfig.SqlConfig;
import necibook.com.process.Property;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Sql任务配置实现
 *
 * @author franky
 * @date 2021-08-03 9:45
 **/
public class SqlTaskImpl extends AbstractTask {
    private final SheetParam sheet;
    private SqlParameters sqlParameters;
    private final boolean flag;
    private final DataSourceMapper dataSourceMapper;
    private ParentTask parentTask;
    BufferedReader bufferedReader=null;
    FileReader fileReader =null;
    public SqlTaskImpl(SheetParam sheet, List<Property> localParamsList, ParentTask parentTask, boolean flag) {
        super(sheet,parentTask);
        this.sqlParameters = new SqlParameters();
        dataSourceMapper=(DataSourceMapper) DBManager.setUp(DataSourceMapper.class);
        this.sqlParameters.setLocalParams(localParamsList);
        this.sheet = sheet;
        this.flag = flag;
    }

    /**
     * 封装Sql类型参数
     *
     * @return
     */
    @Override
    public ParentTask convertToData() {
        parentTask= super.convertToData();
        String taskConfig = sheet.getTaskConfig();
        Boolean flag=true;
        if(taskConfig.contains("/")||taskConfig.contains("\\"))
        {
            taskConfig = taskConfig.replaceAll("/", "-");
            taskConfig=taskConfig.replaceAll("\\\\","-");
        }
        SqlConfig sqlConfig = JSONObject.parseObject(taskConfig,SqlConfig.class);
        SqlCommon sqlCommon = sqlConfig.getSqlCommon();
        sqlParameters.setConnParams(sqlConfig.getConnParams());
        sqlParameters.setType(sqlCommon.getType());
        int dataSourceId = dataSourceMapper.getDatasourceIdByName(sqlCommon.getData_source());
        sqlParameters.setDatasource(dataSourceId);
        String sql = sqlConfig.getSql();
        String preStatements = sqlConfig.getPreStatements();
        String postStatements = sqlConfig.getPostStatements();
        if(sql.contains("-") )
        {
            sql=sql.replaceAll("-","/");
            sql = getFileString(sql);
        }
        if(preStatements.contains("-") )
        {
            preStatements=preStatements.replaceAll("-","/");
            preStatements = getFileString(preStatements);
        }
        if(postStatements.contains("-") )
        {
            postStatements=postStatements.replaceAll("-","/");
            postStatements = getFileString(postStatements);
        }
        try {
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlParameters.setSql(sql);
        sqlParameters.setUdfs(sqlCommon.getUdfs());
        sqlParameters.setSqlType(Integer.parseInt(sqlCommon.getSqlType()));
        if(sqlCommon.isSendEmail())
        {
            sqlParameters.setTitle(sqlCommon.getTitle());
            sqlParameters.setReceivers(sqlCommon.getReceivers());
            sqlParameters.setReceiversCc(sqlCommon.getReceiversCc());
            sqlParameters.setShowType(sqlCommon.getShowType());
        }
        ArrayList<String> preList = new ArrayList<>();
        preList.add(preStatements);
        sqlParameters.setPreStatements(preList);
        ArrayList<String> postList = new ArrayList<>();
        postList.add(postStatements);
        sqlParameters.setPostStatements(postList);
        parentTask.setParams(sqlParameters);
        return parentTask;
    }
    public String getFileString(String filePath)
    {
        StringBuilder stringBuilder = new StringBuilder();

        //sql文件形式
        File file = new File(filePath);

        try {
            fileReader=new FileReader(file);
             bufferedReader = new BufferedReader(fileReader);
            char[] buf=new char[1024];
            int numRead=0;
            while ((numRead=bufferedReader.read(buf))!=-1)
            {
                String readData = String.valueOf(buf, 0, numRead);
                stringBuilder.append(readData);
                buf=new char[1024];
            }
           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
        
    }

}