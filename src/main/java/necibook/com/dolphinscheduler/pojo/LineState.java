package necibook.com.dolphinscheduler.pojo;

/**
 * @author mujp
 */
public class LineState {
    private String sessionId;
    private String processDefinitionId;
    private String jobName;
    private String projectName;
    private String flag;

    public LineState() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "releaseStatePojo{" +
                "sessionId='" + sessionId + '\'' +
                ", processDefinitionId='" + processDefinitionId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }
}
