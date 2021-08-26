package necibook.com.dolphinscheduler.utils;

import java.util.Objects;
import java.util.Random;

/**
 * @author mujp
 */
public class RandomUtil {
    private static final String TASK_ID = "tasks";

    /**
     * 获取taskId
     *
     * @param num
     * @return
     */
    public static int randomInteger(int num){
            Random random = new Random();
            StringBuilder randomTask = new StringBuilder(6);
            boolean flag = true;
            for (int i = 0; i < num; i++) {
                int nextInt = random.nextInt(9);
                if (flag && nextInt == 0) {
                    continue;
                } else {
                    flag = false;
                    randomTask.append(nextInt);
                }
            }
        return Objects.equals("",randomTask.toString()) ?
                randomInteger(num) : Integer.parseInt(randomTask.toString());
    }

    public static String taskId(int num){
        return TASK_ID+"-"+randomInteger(num);
    }

}
