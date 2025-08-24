package org.ruoyi.chat.service.chat.impl;

import cn.dev33.satoken.stp.StpUtil;
import dev.langchain4j.model.chat.response.ChatResponse;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.config.ChatConfig;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.listener.SSEEventSourceListener;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.chat.support.ChatServiceHelper;
import org.ruoyi.common.chat.entity.chat.ChatCompletion;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.openai.OpenAiStreamClient;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatModelService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


/**
 * @author ageer
 */
@Service
@Slf4j
public class OpenAIServiceImpl implements IChatService {

    @Autowired
    private IChatModelService chatModelService;

    @Value("${spring.ai.mcp.client.enabled}")
    private Boolean enabled;

    private final ChatClient chatClient;

    public OpenAIServiceImpl(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        this.chatClient = chatClientBuilder
                .defaultOptions(
                        OpenAiChatOptions.builder().model("gpt-4o-mini").build())
                .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }

    @Override
    public SseEmitter chatStream(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        OpenAiStreamClient openAiStreamClient = ChatConfig.createOpenAiStreamClient(chatModelVo.getApiHost(), chatModelVo.getApiKey());
        List<Message> messages = chatRequest.getMessages();
        if (enabled) {
            String toolString = mcpChat(chatRequest.getPrompt());
            Message userMessage = Message.builder().content("工具返回信息："+toolString).role(Message.Role.USER).build();
            messages.add(userMessage);
        }
        SSEEventSourceListener listener = ChatServiceHelper.createOpenAiListener(emitter, chatRequest);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(chatRequest.getModel())
                .stream(true)
                .build();
        try {
            openAiStreamClient.streamChatCompletion(completion, listener);
        } catch (Exception ex) {
            ChatServiceHelper.onStreamError(emitter, ex.getMessage());
            throw ex;
        }
        return emitter;
    }

    public String mcpChat(String prompt){
        return this.chatClient.prompt(prompt).call().content();
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
        return ChatModeType.CHAT.getCode();
    }
}
