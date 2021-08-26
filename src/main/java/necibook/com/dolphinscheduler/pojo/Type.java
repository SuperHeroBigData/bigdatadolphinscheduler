package necibook.com.dolphinscheduler.pojo;

/**
 * @author mujp
 */

public enum Type {
    /**
     * 任务类型
     */
    SHELL(1,"bash"),
    SPARK(2,"spark submit"),
    PYTHON(3,"python"),
    SQOOP(4,"sqoop"),
    HTTP(5,""),
    FLINK(6,"");

    private int code;
    private String type;

    Type(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getType();
    }
}
