package necibook.com.entity;

import java.util.ArrayList;

/**
 * 资源目录节点
 *
 * @author franky
 * @date 2021/08/23 15:43
 **/
public class ResourceDirs {
    private String parentName ;
    private String myOwnName;
    private ArrayList<ResourceDirs> childrenList;
}
