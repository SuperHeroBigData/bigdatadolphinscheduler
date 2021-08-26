package necibook.com.dolphinscheduler.utils;

import necibook.com.dolphinscheduler.mapper.DataSourceMapper;
import necibook.com.dolphinscheduler.mapper.ProcessInstanceMapper;
import necibook.com.dolphinscheduler.mapper.ResourceMapper;
import necibook.com.easyexcel.SheetEnv;
import necibook.com.enums.DatabaseType;
import necibook.com.utils.ParamUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author mujp
 * 使用mybatis连接数据库
 */
public class DBManager {
    public static final String RESOURCE = "mybatis-config.xml";
    public static final String JDBC_URL = "jdbc:mysql://%s:%s/%s";
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static Properties properties;
    public static ProcessInstanceMapper processInstanceMapper = null;
    public static ResourceMapper resourceMapper=null;
    public static DataSourceMapper dataSourceMapper=null;

    /**
     * 工作流信息
     *
     * @return
     */
    public static ProcessInstanceMapper processInstanceMapper() {
        if (Objects.isNull(processInstanceMapper)) {
            processInstanceMapper = (ProcessInstanceMapper) setUp(ProcessInstanceMapper.class);
        }
        return processInstanceMapper;
    }
    /**
     * 工作流信息
     *
     * @return
     */
    public static ProcessInstanceMapper resourceMapper() {
        if (Objects.isNull(resourceMapper)) {
            resourceMapper = (ResourceMapper) setUp(ResourceMapper.class);
        }
        return resourceMapper();
    }

    /**
     * 获取数据库连接
     *
     * @param cla
     * @return
     */
    public static Object setUp(Class<?> cla) {
        InputStream is = null;
        SqlSession sqlSession = null;
        try {
            Properties processInstanceMapper = getProcessInstanceMapper(ParamUtils.getInstanceEnv());
            is = Resources.getResourceAsStream(RESOURCE);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is, processInstanceMapper);
            sqlSession = sqlSessionFactory.openSession(true);
            return sqlSession.getMapper(cla);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 配置数据库连接信息
     *
     * @param sheetEnv
     * @return
     */
    public static Properties getProcessInstanceMapper(SheetEnv sheetEnv) {
        if (!Objects.equals(DatabaseType.MYSQL, DatabaseType.valueOf(sheetEnv.getDbType()))) {
            throw new RuntimeException("ds元数据连接仅支持MYSQL");
        }
        Properties properties = new Properties();
        properties.setProperty("jdbc.driver", DRIVER);
        String url = String.format(JDBC_URL, sheetEnv.getDbIp(), sheetEnv.getDbPort(), sheetEnv.getDbDatabase());
        properties.setProperty("jdbc.url", url);
        properties.setProperty("jdbc.username", sheetEnv.getDbUser());
        properties.setProperty("jdbc.password", sheetEnv.getDbPassword());
        return properties;
    }

}