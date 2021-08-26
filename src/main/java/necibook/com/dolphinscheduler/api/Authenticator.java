package necibook.com.dolphinscheduler.api;

import necibook.com.dolphinscheduler.pojo.Result;
import necibook.com.easyexcel.SheetEnv;

/**
 * @author mujp
 */
public interface Authenticator {
    /**
     * 用户认证获取session
     * @param login
     * @return
     */
    Result authenticate(SheetEnv login);

}
