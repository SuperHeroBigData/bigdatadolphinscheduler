package necibook.com.dolphinscheduler.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName Cron
 * @Description TODO cron表达式封装
 * @Author jianping.mu
 * @Date 2020/12/15 4:12 下午
 * @Version 1.0
 */
@Data
public class Cron {

    /**
     * 定时开始时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 定时结束时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss",defaultValue = "9999-12-30 00:00:00")
    private Date endTime;
    /**
     * corn表达式
     */
    private String crontab;

}
