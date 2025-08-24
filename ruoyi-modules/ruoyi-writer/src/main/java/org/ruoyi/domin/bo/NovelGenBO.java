package org.ruoyi.domin.bo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/17
 * Time：18:06
 * Description：生成novel参数
 *
 * @author xiaoyan
 * @version 1.0
 */
@Data
public class NovelGenBO {

    @NotEmpty(message = "传入的模型不能为空")
    private String model;

    /**
     * 对话角色
     */
    private String role;

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
    private String conversationId;


}
