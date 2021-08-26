package necibook.com.dolphinscheduler.exceptions;

/**
 * @ClassName ResponseStatus
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/19 6:59 下午
 * @Version 1.0
 */
public enum ResponseStatus implements BaseErrorInfo{
    /**
     * ds 提交任务异常
     */
    SUCCESS(200, "导入成功！"),
    ERROR(500, "导入失败！"),
    TIMEOUT(408, "连接超时！");


    public final Integer code;
    public final String msg;

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

    ResponseStatus(final Integer code, final String msg) {
        this.code = code;
        this.msg = msg;
    }
    @Override
    public Integer getResultCode() {
        return code;
    }

    @Override
    public String getResultMsg() {
        return msg;
    }

}
