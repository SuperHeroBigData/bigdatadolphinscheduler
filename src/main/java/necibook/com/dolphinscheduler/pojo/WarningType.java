package necibook.com.dolphinscheduler.pojo;

/**
 * @ClassName WarningType
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/15 2:24 下午
 * @Version 1.0
 */
public enum WarningType {
    /**
     * 告警通知
     */
    NONE(0, "none"),
    SUCCESS(1, "success"),
    FAILURE(2, "failure"),
    ALL(3, "all");


    WarningType(int code, String descp){
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
