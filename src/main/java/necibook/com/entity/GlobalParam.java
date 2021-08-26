package necibook.com.entity;

import lombok.Data;

import java.util.ArrayList;

/**
 * @ClassName globalParam
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/17 1:49 下午
 * @Version 1.0
 */
@Data
public class GlobalParam {
    /**
     * 全局参数
     */
   private ArrayList<String> globalParams=new ArrayList<>();
    /**
     * 流程中的任务集合
     */
   private ArrayList<String> tasks = new ArrayList<>();
    /**
     * 租户id
     */
   private int tenantId;
    /**
     * 超时时间
     */
   private int timeout;

}

