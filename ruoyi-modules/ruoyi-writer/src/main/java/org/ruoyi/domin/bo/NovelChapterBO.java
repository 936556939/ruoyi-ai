package org.ruoyi.domin.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.core.domain.BaseEntity;
import org.ruoyi.domin.entity.NovelChapter;

/**
 * <p>
 * 章节信息
 * </p>
 *
 * @author xy
 * @since 2025-08-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = NovelChapter.class, reverseConvertGenerate = false)
public class NovelChapterBO extends BaseEntity {

    /**
     * 章节id
     */
    @NotBlank(message = "章节id不能为空", groups = {EditGroup.class})
    private String id;

    /**
     * 小说id
     */
    @NotBlank(message = "小说id不能为空")
    private String novelId;

    /**
     * 对话id
     */
    private String conversationId;

    /**
     * 章节号
     */
    private String chapterNo;

    /**
     * 章节名称
     */
    private String chapterName;

    /**
     * 章节大纲
     */
    private String chapterOutline;

    /**
     * 章节内容
     */
    private String chapterData;

    /**
     * 排序
     */
    private Integer orderNo;
}
