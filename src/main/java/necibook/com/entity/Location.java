package necibook.com.entity;

import lombok.Data;

/**
 * @ClassName Location
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/24 4:42 下午
 * @Version 1.0
 */
@Data
public class Location {

    /**
     * 任务Id
     */
    private String name;
    /**
     * 前置依赖Id
     */
    private String targetarr="";
    /**
     * 后置依赖个数
     */
    private String nodenumber="0";

    /**
     * 坐标系 x,y轴
     */
    private int x;
    private int y;
}
