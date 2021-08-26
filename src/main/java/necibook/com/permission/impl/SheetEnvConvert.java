package necibook.com.permission.impl;

import necibook.com.easyexcel.SheetEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName SheetEnvConvert
 * @Author jianping.mu
 * @Date 2020/11/28 2:10 下午
 * @Version 1.0
 * 获取环境参数
 */
public class SheetEnvConvert {
    private static Logger LOGGER = LoggerFactory.getLogger(ParamConvert.class);
    private List<SheetEnv> sheetEnv;

    public SheetEnvConvert(List<SheetEnv> sheetEnv) {
        this.sheetEnv = sheetEnv;
    }

}
