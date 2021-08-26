package necibook.com.entity.taskConfig;

/**
 * SparkCommon参数
 *
 * @author franky
 * @date 2021-08-03 14:43
 **/
public class FlinkCommon {
   private String deployMode="cluster";
   private String jobManamgerMemory="2G";
   private String taskManagerMemory="4G";
   private int slot=2;
   private int executorCores=4;
   private String numExecutors="3";
   private String programType="JAVA";
   private String flinkVersion=">=1.10";
}