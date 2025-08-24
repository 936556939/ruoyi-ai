package org.ruoyi.chat.service.chat.impl;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatRequestModel;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.generate.OllamaStreamHandler;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.chat.util.SSEUtil;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatMessageService;
import org.ruoyi.service.IChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.ruoyi.chat.support.RetryNotifier;
import org.ruoyi.chat.support.ChatServiceHelper;


/**
 * @author ageer
 */
@Service
@Slf4j
public class OllamaServiceImpl implements IChatService {

    @Autowired
    private IChatModelService chatModelService;
    @Autowired
    private IChatMessageService chatMessageService;


    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        String host = chatModelVo.getApiHost();
        List<Message> msgList = chatRequest.getMessages();

        List<OllamaChatMessage> messages = new ArrayList<>();
        for (Message message : msgList) {
            OllamaChatMessage ollamaChatMessage = new OllamaChatMessage();
            if (message.getRole().equals(Message.Role.USER.getName())) {
                ollamaChatMessage.setRole(OllamaChatMessageRole.USER);
            } else if (message.getRole().equals(Message.Role.ASSISTANT.getName())) {
                ollamaChatMessage.setRole(OllamaChatMessageRole.ASSISTANT);
            } else if (message.getRole().equals(Message.Role.SYSTEM.getName())) {
                ollamaChatMessage.setRole(OllamaChatMessageRole.SYSTEM);
            }
            ollamaChatMessage.setContent(message.getContent().toString());
            messages.add(ollamaChatMessage);
        }
        OllamaAPI api = new OllamaAPI(host);
        api.setRequestTimeoutSeconds(100);
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatRequest.getModel());

        OllamaChatRequestModel requestModel = builder
                .withMessages(messages)
                .build();

        // 异步执行 OllAma API 调用
        CompletableFuture.runAsync(() -> {
            try {
                StringBuilder response = new StringBuilder();
                OllamaStreamHandler streamHandler = (s) -> {
                    String substr = s.substring(response.length());
                    response.append(substr);
                    try {
                        emitter.send(substr);
                    } catch (IOException e) {
                        ChatServiceHelper.onStreamError(emitter, e.getMessage());
                    }
                };
                api.chat(requestModel, streamHandler);
                // 发送完成事件
                emitter.send("[DONE]");
                emitter.complete();
                RetryNotifier.clear(emitter);
                //保存模型返回信息
                chatRequest.setRole(Message.Role.ASSISTANT.getName());
                chatRequest.setPrompt(response.toString());
                saveChatMessage(chatRequest, chatMessageService);
            } catch (Exception e) {
                ChatServiceHelper.onStreamError(emitter, e.getMessage());
            }
        });

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
        String host = chatModelVo.getApiHost();
        List<Message> msgList = chatRequest.getMessages();

        // 构建 Ollama 消息列表
        List<OllamaChatMessage> messages = new ArrayList<>();
        for (Message message : msgList) {
            OllamaChatMessage ollamaChatMessage = new OllamaChatMessage();
            if (message.getRole().equals(Message.Role.USER.getName())) {
                ollamaChatMessage.setRole(OllamaChatMessageRole.USER);
            } else if (message.getRole().equals(Message.Role.ASSISTANT.getName())) {
                ollamaChatMessage.setRole(OllamaChatMessageRole.ASSISTANT);
            } else if (message.getRole().equals(Message.Role.SYSTEM.getName())) {
                ollamaChatMessage.setRole(OllamaChatMessageRole.SYSTEM);
            }
            ollamaChatMessage.setContent(message.getContent().toString());
            messages.add(ollamaChatMessage);
        }

        // 初始化 Ollama API 客户端
        OllamaAPI api = new OllamaAPI(host);
        api.setRequestTimeoutSeconds(100);

        // 构建请求模型
        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(chatRequest.getModel());
        OllamaChatRequestModel requestModel = builder
                .withMessages(messages)
                .build();

        try {
            // 发送请求并获取完整响应
            OllamaChatResult ollamaResponse = api.chat(requestModel);

            // 将 Ollama 响应转换为项目定义的 ChatResponse
            ChatResponse.Builder responseBuilder = ChatResponse.builder();

            responseBuilder.aiMessage(AiMessage.builder().text(ollamaResponse.getChatHistory().get(ollamaResponse.getChatHistory().size() - 1).getContent()).build());

            // 保存聊天记录
            chatRequest.setRole(Message.Role.ASSISTANT.getName());
            chatRequest.setPrompt(ollamaResponse.getChatHistory().get(ollamaResponse.getChatHistory().size() - 1).getContent());
            saveChatMessage(chatRequest, chatMessageService);

            return responseBuilder.build();
        } catch (Exception e) {
            log.error("Ollama chat error: ", e);
            return null;
        }
    }

    /**
     * 获取服务端结果并转换为T
     *
     * @param bo          业务对象
     * @param chatRequest 请求对象
     */
    @Override
    public <T> T create(Class<T> bo, ChatRequest chatRequest) {
        // 获取模型配置信息
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        String host = chatModelVo.getApiHost();

        // 创建OllamaChatModel实例
        OllamaChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(host)
                .modelName(chatRequest.getModel())
                .temperature(0.0)
                .timeout(Duration.ofSeconds(100))
                .build();

        // 使用AiServices创建指定类型的AI服务
        return AiServices.create(bo, chatModel);
    }

    @Override
    public String getCategory() {
        return ChatModeType.OLLAMA.getCode();
    }
}