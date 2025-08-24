package org.ruoyi.domin.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/23
 * Time：18:35
 * Description：按照提示生成小说大纲请求体
 *
 * @author xiaoyan
 * @version 1.0
 */
@Data
public class GenOutlineReqDTO {

    @NotEmpty(message = "传入的模型不能为空")
    private String model;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 系统提示词
     */
    private String sysPrompt;

    /**
     * 是否开启流式对话
     */
    private Boolean stream = Boolean.TRUE;

    /**
     * 对话id(每个聊天窗口都不一样)
     */
    private Long conversationId;

    /**
     * 小说id
     */
    @NotEmpty(message = "小说id不能为空")
    private String novelId;

    /**
     * 章节id
     */
    private String chapterIds;
}
