package org.ruoyi.chat.service.chat;

import dev.langchain4j.model.chat.response.ChatResponse;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.bo.ChatMessageBo;
import org.ruoyi.domain.vo.ChatMessageVo;
import org.ruoyi.service.IChatMessageService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话Service接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
public interface IChatService {
    /**
     * 客户端发送消息到服务端 stream
     *
     * @param chatRequest 请求对象
     */
    SseEmitter chatStream(ChatRequest chatRequest, SseEmitter emitter);
    /**
     * 客户端发送消息到服务端
     *
     * @param chatRequest 请求对象
     */
    ChatResponse chat(ChatRequest chatRequest);
    /**
     * 获取服务端结果并转换为T
     *
     * @param chatRequest 请求对象
     */
    <T> T create(Class<T> bo, ChatRequest chatRequest);

    /**
     * 获取此服务支持的模型类别
     */
    String getCategory();

    /**
     * 保存sse消息记录
     */
    default void saveChatMessage(ChatRequest chatRequest, IChatMessageService service) {
        ChatMessageBo chatMessageBo = new ChatMessageBo();
        // 设置用户id
        chatMessageBo.setUserId(chatRequest.getUserId());
        // 设置会话id
        chatMessageBo.setSessionId(chatRequest.getSessionId());
        // 设置对话角色
        chatMessageBo.setRole(chatRequest.getRole());
        // 设置对话内容
        chatMessageBo.setContent(chatRequest.getPrompt());
        // 设置模型名字
        chatMessageBo.setModelName(chatRequest.getModel());
        chatMessageBo.setDeductCost(0d);
        chatMessageBo.setRemark("不计费!");
        // 保存消息记录
        service.insertByBo(chatMessageBo);
        chatRequest.setUuid(chatMessageBo.getId());
    }

    /**
     * 获取消息记录
     */
    default List<Message> getChatMessageList(Long uuid, IChatMessageService service) {
        ChatMessageBo bo = new ChatMessageBo();
        bo.setSessionId(uuid);
        List<ChatMessageVo> chatMessageVos = service.queryList(bo);
        return chatMessageVos.stream().map(chatMessageVo -> {
            Message message = new Message();
            message.setRole(chatMessageVo.getRole());
            message.setContent(chatMessageVo.getContent());
            return message;
        }).collect(Collectors.toCollection(ArrayList::new));
    }
}
