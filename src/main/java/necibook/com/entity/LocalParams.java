package necibook.com.entity;

import necibook.com.enums.Direct;
import lombok.Data;

/**
 * @ClassName LocalParams
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/27 3:45 下午
 * @Version 1.0
 */
@Data
public class LocalParams {
     /**
      * 参数key
      */
     private String prop;
     /**
      * 是出参还是入参
      */
     private Direct direct = Direct.IN;
     /**
      * 参数类型
      */
     private String type = "VARCHAR";
     /**
      * 参数值
      */
     private String value;
}
