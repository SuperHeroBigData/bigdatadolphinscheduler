package necibook.com.dolphinscheduler.pojo;

/**
 * @ClassName Priority
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/12/15 2:21 下午
 * @Version 1.0
 */
public enum Priority {
    /**
     * 作业优先级
     */
    HIGHEST(0, "highest"),
    HIGH(1, "high"),
    MEDIUM(2, "medium"),
    LOW(3, "low"),
    LOWEST(4, "lowest");

    Priority(int code, String descp){
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
