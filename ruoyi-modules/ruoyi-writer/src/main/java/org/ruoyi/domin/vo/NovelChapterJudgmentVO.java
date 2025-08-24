package org.ruoyi.domin.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ruoyi.domin.entity.NovelChapterJudgment;

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = NovelChapterJudgment.class)
public class NovelChapterJudgmentVO implements Serializable {

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

    /**
     * 创建部门
     */
    @TableField("create_dept")
    private Long createDept;

    /**
     * 创建者
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
