package org.ruoyi.domin.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/23
 * Time：11:51
 * Description：TODO
 *
 * @author xiaoyan
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNovelChapterBO {
    /**
     * 章节号
     */
    @TableField("chapter_no")
    private String chapterNo;

    /**
     * 章节名称
     */
    @TableField("chapter_name")
    private String chapterName;

    /**
     * 章节大纲
     */
    @TableField("chapter_outline")
    private String chapterOutline;

    /**
     * 章节内容
     */
    @TableField("chapter_data")
    private String chapterData;

}
