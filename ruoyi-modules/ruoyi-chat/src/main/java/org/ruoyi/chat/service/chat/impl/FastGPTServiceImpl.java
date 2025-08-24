package org.ruoyi.chat.service.chat.impl;

import dev.langchain4j.model.chat.response.ChatResponse;
import org.ruoyi.chat.config.ChatConfig;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.listener.FastGPTSSEEventSourceListener;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.common.chat.entity.chat.FastGPTChatCompletion;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.openai.OpenAiStreamClient;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * FastGpt 聊天管理
 * 项目整体沿用Openai接口范式，根据FastGPT文档增加相应的参数
 *
 * @author yzm
 */
@Service
public class FastGPTServiceImpl implements IChatService {

    @Autowired
    private IChatModelService chatModelService;

    @Override
    public SseEmitter chatStream(ChatRequest chatRequest, SseEmitter emitter) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        OpenAiStreamClient openAiStreamClient = ChatConfig.createOpenAiStreamClient(chatModelVo.getApiHost(), chatModelVo.getApiKey());
        List<Message> messages = chatRequest.getMessages();
        FastGPTSSEEventSourceListener listener = new FastGPTSSEEventSourceListener(emitter);
        FastGPTChatCompletion completion = FastGPTChatCompletion
                .builder()
                .messages(messages)
                // 开启后sse会返回event值
                .detail(true)
                .stream(true)
                .build();
        openAiStreamClient.streamChatCompletion(completion, listener);
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
        return ChatModeType.FASTGPT.getCode();
    }
}
