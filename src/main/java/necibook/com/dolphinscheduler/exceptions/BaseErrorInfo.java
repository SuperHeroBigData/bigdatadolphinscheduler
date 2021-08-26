package necibook.com.dolphinscheduler.exceptions;

/**
 * @ClassName BaseErrorInfo
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/19 6:56 下午
 * @Version 1.0
 */
public interface BaseErrorInfo {
    /**
     * 状态码
     * @return Integer
     */
    Integer getResultCode();

    /**
     * 异常信息描述
     * @return String 错误信息
     */
    String getResultMsg();
}
