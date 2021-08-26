package necibook.com.entity.dependent;

import lombok.Data;

import java.util.List;

/**
 * @ClassName Dependence
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/24 7:23 下午
 * @Version 1.0
 */

@Data
public class Dependence {

    /**
     * 依赖条件
     */
    private String relation = DependentRelation.AND.name();

    /**
     * 依赖任务
     */
    private List<DependTaskList> dependTaskList;


}
