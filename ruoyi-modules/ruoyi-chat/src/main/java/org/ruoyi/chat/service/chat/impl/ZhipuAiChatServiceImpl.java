package org.ruoyi.chat.service.chat.impl;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
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

import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.data.message.AiMessage;


/**
 * 智谱AI
 */
@Service
@Slf4j
public class ZhipuAiChatServiceImpl implements IChatService {

    ToolSpecification currentTime = ToolSpecification.builder()
            .name("currentTime")
            .description("currentTime")
            .build();
    @Autowired
    private IChatModelService chatModelService;

    @Override
    public SseEmitter chatStream(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        // 发送流式消息
        try {
            StreamingChatResponseHandler handler = new StreamingChatResponseHandler() {
                @SneakyThrows
                @Override
                public void onPartialResponse(String token) {
                    //System.out.println(token);
                    emitter.send(token);
                }

                @SneakyThrows
                @Override
                public void onError(Throwable error) {
                    // System.out.println(error.getMessage());
                    emitter.send(error.getMessage());
                }

                @Override
                public void onCompleteResponse(ChatResponse response) {
                    emitter.complete();
                    log.info("消息结束，完整消息ID: {}", response.aiMessage());
                }
            };

            StreamingChatModel model = ZhipuAiStreamingChatModel.builder()
                    .model(chatModelVo.getModelName())
                    .apiKey(chatModelVo.getApiKey())
                    .logRequests(true)
                    .logResponses(true)
                    .build();
            model.chat(chatRequest.getPrompt(), handler);
        } catch (Exception e) {
            log.error("智谱清言请求失败：{}", e.getMessage());
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
        // 获取模型配置信息
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        
        // 构建非流式智谱AI模型
        ZhipuAiChatModel model = ZhipuAiChatModel.builder()
                .model(chatModelVo.getModelName())
                .apiKey(chatModelVo.getApiKey())
                .logRequests(true)
                .logResponses(true)
                .build();
                
        // 发送请求并获取完整响应
        String zhipuResponse = model.chat(chatRequest.getPrompt());
        
        // 将智谱AI响应转换为项目定义的ChatResponse
        return ChatResponse.builder()
                .aiMessage(AiMessage.builder().text(zhipuResponse).build())
                .build();
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
        return ChatModeType.ZHIPU.getCode();
    }
}