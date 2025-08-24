package org.ruoyi.domin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 小说信息
 * </p>
 *
 * @author xy
 * @since 2025-08-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("novel_info")
public class NovelInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 小说id
     */
    @TableId("id")
    private String id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 小说名称
     */
    @TableField("name")
    private String name;

    /**
     * 小说描述
     */
    @TableField("description")
    private String description;

    /**
     * 大纲
     */
    @TableField("outline")
    private String outline;

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
