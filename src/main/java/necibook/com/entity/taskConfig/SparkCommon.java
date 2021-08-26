package necibook.com.entity.taskConfig;

/**
 * SparkCommon参数
 *
 * @author franky
 * @date 2021-08-03 14:43
 **/
public class SparkCommon {
   private String deployMode="cluster";
   private String driverMemory="2G";
   private String driverCores="3";
   private String executorMemory="4G";
   private String executorCores="4";
   private String numExecutors="3";
   private String programType="JAVA";
   private String sparkVersion="SPARK1";
}