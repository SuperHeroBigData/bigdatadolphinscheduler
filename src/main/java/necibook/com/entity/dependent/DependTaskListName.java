package necibook.com.entity.dependent;

import lombok.Data;

import java.util.List;

/**
 * @ClassName DependentList
 * @Description TODO
 * @Author jianping.mu
 * @Date 2020/11/25 8:44 下午
 * @Version 1.0
 */
@Data
public class DependTaskListName {

    /**
     * 依赖条件
     */
    private String relation = DependentRelation.AND.name();
    /**
     * 依赖任务
     */
    private List<DependItemListName> dependItemList;


}
