package necibook.com.entity;

import com.alibaba.fastjson.JSONArray;
import necibook.com.process.Property;
import lombok.Data;

import java.util.ArrayList;

/**
 * @ClassName TaskParameters
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/26 3:44 下午
 * @Version 1.0
 */
@Data
public class TaskParameters {

    /**
     * 全局变量
     */
    private ArrayList<Property> globalParams;

    /**
     * 任务信息
     */
    private JSONArray tasks;

    /**
     * 租户
     */
    private long tenantId;

    /**
     * 超时
     */
    private String timeout;
}
