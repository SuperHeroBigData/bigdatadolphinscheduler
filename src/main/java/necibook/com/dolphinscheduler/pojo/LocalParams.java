package necibook.com.dolphinscheduler.pojo;

/**
 * @author mujp
 */
public class LocalParams {
    private String direct = "IN";
    private String prop;
    private String type = "VARCHAR";
    private String value;

    public LocalParams() {
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LocalParams{" +
                "direct='" + direct + '\'' +
                ", prop='" + prop + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

}
