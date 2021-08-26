package necibook.com.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author Mujp
 * @Date: Create in 11:17 上午 2020/9/9
 * @Description：使用SimpleDateFormat实现线程安全
 */

public class DateUtil {
    private static final String DATE_REGEX ="yyyy-MM-dd HH:mm:ss";
    private static final String DATE_REGEX2 ="yyyyMMdd";

    private static final ThreadLocal<DateFormat> THREAD_LOCAL =new ThreadLocal<DateFormat>();

    /**
     * 格式化str日期类型
     * @param regex
     * @return 格式化时间
     */
    public static DateFormat getDateFormat(String regex){
        DateFormat dateFormat = THREAD_LOCAL.get();
        if (dateFormat==null){

            dateFormat=new SimpleDateFormat(regex);

            THREAD_LOCAL.set(dateFormat);
        }
        /*解决ThreadLocal内存泄露*/
        THREAD_LOCAL.remove();
        return dateFormat;
    }

    public static String formatDate(Date date){
        return getDateFormat(DATE_REGEX).format(date);
    }

    public static Date parse(String dataStr) throws ParseException{
        return getDateFormat(DATE_REGEX).parse(dataStr);
    }

    /**
     * 获取昨天日期
     * @param date yyyyMMdd
     * @return
     */
    public static String getYesterday(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,-1);
        Date time = calendar.getTime();
        return getDateFormat(DATE_REGEX2).format(time);
    }


}
