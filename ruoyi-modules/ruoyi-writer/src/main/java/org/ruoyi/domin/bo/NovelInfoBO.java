package org.ruoyi.domin.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.core.domain.BaseEntity;
import org.ruoyi.domin.entity.NovelInfo;


/**
 * <p>
 * 小说信息
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
@AutoMapper(target = NovelInfo.class, reverseConvertGenerate = false)
public class NovelInfoBO extends BaseEntity {
    /**
     * 小说id
     */
    @NotBlank(message = "id不能为空", groups = {EditGroup.class})
    private String id;

    /**
     * 小说名称
     */
    @NotBlank(message = "小说名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 小说描述
     */
    private String description;

    /**
     * 大纲
     */
    private String outline;
}
