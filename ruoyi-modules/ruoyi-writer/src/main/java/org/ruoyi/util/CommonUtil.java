package org.ruoyi.util;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/17
 * Time：23:37
 * Description：工具类
 *
 * @author xiaoyan
 * @version 1.0
 */
public class CommonUtil {
    /**
     * 树形结构转列表
     *
     * @param text
     * @return List<String>
     */
    public static String getThink(String text) {
        int i = text.indexOf("<think>");
        int j = text.lastIndexOf("</think>");
        if (i == -1 || j == -1) {
            return "";
        }
        return text.substring(i + 8, j);
    }

    /**
     * 去除think标签
     */
    public static String getAnswer(String text) {
        int i = text.indexOf("<think>");
        int j = text.lastIndexOf("</think>");
        if (i == -1 || j == -1) {
            return text;
        }
        return text.substring(0, i) + text.substring(j + 9);
    }

}
