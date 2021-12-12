package com.cjml.util;

import java.util.ResourceBundle;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class ResourceUtils {
    private ResourceUtils() {}

    /**
     * 通过key获取resource文件中的value值
     *
     * @param fileName properties文件名, 不带后缀
     * @param key 要获取的key
     * @return 指定key的value
     */
    public static String gainValueByKey(String fileName, String key) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(fileName);
        //遍历取值
        return resourceBundle.getString(key);
    }
}
