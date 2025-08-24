package org.ruoyi.chat.service.chat.impl;


import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.chat.support.ChatServiceHelper;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
/**
 * deepseek
 */
@Service
@Slf4j
public class DeepSeekChatImpl  implements IChatService {

    @Autowired
    private IChatModelService chatModelService;

    @Override
    public SseEmitter chatStream(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        StreamingChatModel chatModel = OpenAiStreamingChatModel.builder()
                .baseUrl(chatModelVo.getApiHost())
                .apiKey(chatModelVo.getApiKey())
                .modelName(chatModelVo.getModelName())
                .logRequests(true)
                .logResponses(true)
                .temperature(0.8)
                .build();
        // 发送流式消息
        try {
            chatModel.chat(chatRequest.getPrompt(), new StreamingChatResponseHandler() {
                @SneakyThrows
                @Override
                public void onPartialResponse(String partialResponse) {
                    emitter.send(partialResponse);
                    log.info("收到消息片段: {}", partialResponse);
                    System.out.print(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    emitter.complete();
                    log.info("消息结束，完整消息ID: {}", completeResponse);
                }

                @Override
                public void onError(Throwable error) {
                    System.err.println("错误: " + error.getMessage());
                    ChatServiceHelper.onStreamError(emitter, error.getMessage());
                }
            });

        } catch (Exception e) {
            log.error("deepseek请求失败：{}", e.getMessage());
            // 同步异常直接通知失败
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
        return ChatModeType.DEEPSEEK.getCode();
    }
}
