package necibook.com.dolphinscheduler.pojo;

/**
 * @ClassName FailureStrategy
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/15 2:19 下午
 * @Version 1.0
 */
public enum FailureStrategy {
    /**
     * 失败重试策略
     */
    END(0, "end"),
    CONTINUE(1, "continue");

    FailureStrategy(int code, String descp){
        this.code = code;
        this.descp = descp;
    }
    private final int code;
    private final String descp;

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }
}
