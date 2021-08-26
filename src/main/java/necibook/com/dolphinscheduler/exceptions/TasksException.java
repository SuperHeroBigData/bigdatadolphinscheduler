package necibook.com.dolphinscheduler.exceptions;

/**
 * @ClassName TasksException
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/19 6:56 下午
 * @Version 1.0
 */
public class TasksException extends RuntimeException {
    /**
     * 错误码
     */
    protected Integer errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public TasksException(String message) {
        super(message);
    }

    public TasksException(String message, Throwable cause) {
        super(message, cause);
    }

    public TasksException(BaseErrorInfo errorInfo) {
        super(errorInfo.getResultCode().toString());
        this.errorCode = errorInfo.getResultCode();
        this.errorMsg = errorInfo.getResultMsg();
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
