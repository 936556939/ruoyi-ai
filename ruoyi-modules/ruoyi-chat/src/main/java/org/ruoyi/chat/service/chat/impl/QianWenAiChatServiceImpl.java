package org.ruoyi.chat.service.chat.impl;

import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.ruoyi.chat.support.ChatServiceHelper;


/**
 * 阿里通义千问
 */
@Service
@Slf4j
public class QianWenAiChatServiceImpl  implements IChatService {

    @Autowired
    private IChatModelService chatModelService;


    @Override
    public SseEmitter chatStream(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        StreamingChatModel model = QwenStreamingChatModel.builder()
                .apiKey(chatModelVo.getApiKey())
                .modelName(chatModelVo.getModelName())
                .build();



        // 发送流式消息
        try {
            model.chat(chatRequest.getPrompt(), new StreamingChatResponseHandler() {
                @SneakyThrows
                @Override
                public void onPartialResponse(String partialResponse) {
                    emitter.send(partialResponse);
                    log.info("收到消息片段: {}", partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    emitter.complete();
                    log.info("消息结束，完整消息ID: {}", completeResponse);
                    org.ruoyi.chat.support.RetryNotifier.clear(emitter);
                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                    ChatServiceHelper.onStreamError(emitter, error.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("千问请求失败：{}", e.getMessage());
            ChatServiceHelper.onStreamError(emitter, e.getMessage());
        }

        return emitter;

    }

    /**
     * 客户端发送消息到服务端
     *
     * @param chatRequest 请求对象
     */
    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        // TODO: 待补充
        return null;
    }

    /**
     * 获取服务端结果并转换为T
     *
     * @param bo 业务对象
     * @param chatRequest 请求对象
     */
    @Override
    public <T> T create(Class<T> bo, ChatRequest chatRequest) {
        // TODO: 待补充
        return null;
    }

    @Override
    public String getCategory() {
        return ChatModeType.QIANWEN.getCode();
    }



}
