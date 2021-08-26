package necibook.com.dolphinscheduler.pojo;

import lombok.Data;

/**
 * @ClassName Login
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/26 5:51 下午
 * @Version 1.0
 */
@Data
public class Login {
  private String user;
  private String password;
  private String name;
  private String projectName;
  private String hostName;


}
