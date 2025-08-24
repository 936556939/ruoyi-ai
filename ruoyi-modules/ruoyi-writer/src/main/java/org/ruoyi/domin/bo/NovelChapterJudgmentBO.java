package org.ruoyi.domin.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ruoyi.core.domain.BaseEntity;
import org.ruoyi.domin.entity.NovelChapterJudgment;
import org.ruoyi.domin.entity.NovelInfo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@AutoMapper(target = NovelChapterJudgment.class, reverseConvertGenerate = false)
public class NovelChapterJudgmentBO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 章节评价id
     */
    @TableId("id")
    private String id;

    /**
     * 小说id
     */
    @TableField("novel_id")
    private String novelId;

    /**
     * 对话id
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 章节号
     */
    @TableField("chapter_no")
    private String chapterNo;

    /**
     * 章节评价类型 1、简介，2、内容
     */
    @TableField("judgment_type")
    private String judgmentType;

    /**
     * 章节名称
     */
    @TableField("chapter_name")
    private String chapterName;

    /**
     * 章节内容
     */
    @TableField("chapter_outline")
    private String chapterOutline;

    /**
     * 评价分数
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 章节内容评价
     */
    @TableField("chapter_outline_judgment")
    private String chapterOutlineJudgment;

    /**
     * 排序
     */
    @TableField("order_no")
    private Integer orderNo;

}
