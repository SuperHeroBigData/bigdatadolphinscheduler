package necibook.com.dolphinscheduler.pojo;

/**
 * @author mujp
 */
public class Result {
    /**
     * status
     */
    private String state;

    /**
     * message
     */
    private String msg;

    /**
     * data
     */
    private String data;

    /**
     * jobName
     */
    private String jobName;

    /**
     * userName
     */
    private String userName;

    /**
     * projectName
     */
    private String projectName;

    public Result(){}


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "Result{" +
                "state='" + state + '\'' +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                ", jobName='" + jobName + '\'' +
                ", userName='" + userName + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}