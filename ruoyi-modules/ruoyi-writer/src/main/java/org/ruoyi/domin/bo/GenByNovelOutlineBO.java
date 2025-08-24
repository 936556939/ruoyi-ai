package org.ruoyi.domin.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/22
 * Time：23:46
 * Description：通过小说大纲生成章节
 *
 * @author xiaoyan
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenByNovelOutlineBO {

    @NotEmpty(message = "传入的模型不能为空")
    private String model;
    /**
     * 小说id
     */
    @NotBlank(message = "小说id不能为空")
    private String novelId;

    /**
     * 生成章节总数量
     */
    private Integer chapterSize = 10;

    /**
     * 单次生成章节数量
     */
    private Integer space = 1;

    /**
     * 生成章节号
     */
    private String chapterNo;

    /**
     * 前几章内容
     */
    private Integer beforeChapterData = 0;

    /**
     * 后几章内容
     */
    private Integer afterChapterData = 0;

    /**
     * 前几章大纲
     */
    private Integer beforeChapterOutline = 0;

    /**
     * 后几章大纲
     */
    private Integer afterChapterOutline = 0;
}
