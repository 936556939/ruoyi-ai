package org.ruoyi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.factory.ChatServiceFactory;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.core.utils.DateUtils;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domain.vo.ChatMessageVo;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.domin.bo.NovelInfoBO;
import org.ruoyi.domin.dto.req.GenOutlineReqDTO;
import org.ruoyi.domin.entity.NovelInfo;
import org.ruoyi.domin.vo.NovelInfoVO;
import org.ruoyi.mapper.NovelInfoMapper;
import org.ruoyi.service.IChatMessageService;
import org.ruoyi.service.IChatModelService;
import org.ruoyi.service.INovelInfoService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/17
 * Time：10:30
 * Description：小说信息服务实现类
 *
 * @author xiaoyan
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NovelInfoServiceImpl extends ServiceImpl<NovelInfoMapper, NovelInfo> implements INovelInfoService {

    private final IChatModelService chatModelService;

    private final ChatServiceFactory chatServiceFactory;

    private final IChatMessageService chatMessageService;

    /**
     * 查询小说列表
     *
     * @param bo        查询参数
     * @param pageQuery 分页参数
     * @return 列表
     */
    @Override
    public TableDataInfo<NovelInfoVO> qryNovelList(NovelInfoBO bo, PageQuery pageQuery) {
        IPage<NovelInfoVO> page = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(page);
    }


    private Wrapper<NovelInfo> buildQueryWrapper(NovelInfoBO bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper<NovelInfo> wrapper = Wrappers.query();
        wrapper.eq(ObjectUtil.isNotNull(bo.getId()), "id", bo.getId())
                .like(StringUtils.isNotBlank(bo.getName()), "name", bo.getName())
                .like(StringUtils.isNotBlank(bo.getOutline()), "outline", bo.getOutline());
        return wrapper;
    }


    /**
     * <p>新增小说</p>
     *
     * @param bo 新增参数
     * @return 结果
     */
    @Override
    public int addNovel(NovelInfoBO bo) {
        NovelInfo add = MapstructUtils.convert(bo, NovelInfo.class);
        validEntityBeforeSave(add);
        return baseMapper.insert(add);
    }

    private void validEntityBeforeSave(NovelInfo info) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * <p>修改小说信息</p>
     *
     * @param bo 修改参数
     * @return 结果
     */
    @Override
    public int editNovel(NovelInfoBO bo) {
        NovelInfo info = MapstructUtils.convert(bo, NovelInfo.class);
        validEntityBeforeSave(info);
        return baseMapper.updateById(info);
    }

    /**
     * <p>删除小说</p>
     *
     * @param ids     ids
     * @param isValid 是否校验
     * @return 删除结果
     */
    @Override
    public int deleteWithValidByIds(List<Long> ids, boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids);
    }

    /**
     * <p>生成小说大纲</p>
     *
     * @param chatRequest 生成参数
     * @return 结果
     */
    @Override
    public String genOutline(ChatRequest chatRequest) {
        if (null == chatRequest.getSessionId()) {
            chatRequest.setSessionId(IdWorker.getId());
        }

        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        // 构建消息列表
        buildChatMessageList(chatRequest, chatModelVo);
        chatRequest.setRole(Message.Role.USER.getName());

        // 根据模型分类调用不同的处理逻辑
        IChatService chatService = chatServiceFactory.getChatService(chatModelVo.getCategory());
        if (LoginHelper.isLogin()) {
            chatRequest.setUserId(LoginHelper.getUserId());
            //报存对话信息
            chatService.saveChatMessage(chatRequest, chatMessageService);
        }
        return chatService.chat(chatRequest).aiMessage().text();
    }

    /**
     * <p>生成小说大纲sse</p>
     *
     * @param bo 生成参数
     * @return 生成结果
     */
    @Override
    public SseEmitter genOutlineSse(GenOutlineReqDTO bo) {
        SseEmitter sseEmitter = new SseEmitter(0L);
        ChatRequest chatRequest = new ChatRequest();
        if (null == bo.getConversationId()) {
            chatRequest.setSessionId(IdWorker.getId());
        }else {
            chatRequest.setSessionId(bo.getConversationId());
        }
        chatRequest.setModel(bo.getModel());
        chatRequest.setPrompt(bo.getPrompt());
        chatRequest.setSysPrompt(bo.getSysPrompt());
        chatRequest.setStream(bo.getStream());

        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());

        // 构建消息列表
        buildChatMessageList(chatRequest, chatModelVo);
        chatRequest.setRole(Message.Role.USER.getName());

        // 根据模型分类调用不同的处理逻辑
        IChatService chatService = chatServiceFactory.getChatService(chatModelVo.getCategory());
        chatService.chatStream(chatRequest, sseEmitter);
        return sseEmitter;
    }

    /**
     * 构建消息列表
     */
    private void buildChatMessageList(ChatRequest chatRequest, ChatModelVo chatModelVo) {
        // 获取对话消息列表
        List<Message> messages;
        if(CollUtil.isEmpty(chatRequest.getMessages())){
            IChatService chatService = chatServiceFactory.getChatService(chatModelVo.getCategory());
            messages = chatService.getChatMessageList(chatRequest.getSessionId(), chatMessageService);
        }else{
            messages = chatRequest.getMessages();
        }

        String sysPrompt = chatModelVo.getSystemPrompt();
        if (StringUtils.isEmpty(sysPrompt)) {
            sysPrompt = "作为资深小说策划，请根据用户输入的信息\n" +
                    "创建一个引人入胜的小说大纲，要求：\n" +
                    "1. 提炼核心冲突，设计多重矛盾\n" +
                    "2. 设计3-5个重大转折点\n" +
                    "3. 人物塑造要立体，性格鲜明\n" +
                    "4. 构建层层递进的剧情架构\n" +
                    "5. 设置悬念和伏笔\n" +
                    "\n" +
                    "输出格式：\n" +
                    "1. 故事梗概\n" +
                    "2. 主要情节脉络\n" +
                    "3. 人物塑造重点\n" +
                    "4. 转折点设计\n" +
                    "5. 结局构思" +
                    "当前时间：" + DateUtils.getDate() +
                    "#注意：回复之前注意结合上下文和工具返回内容进行回复。";
        }
        // 设置系统默认提示词
        Message sysMessage = Message.builder().content(sysPrompt).role(Message.Role.SYSTEM).build();
        messages.add(0, sysMessage);
        // 用户对话内容
        messages.add(Message.builder().content(chatRequest.getPrompt()).role(Message.Role.USER).build());

        chatRequest.setMessages(messages);
        chatRequest.setSysPrompt(sysPrompt);
    }

    /**
     * <p>小说大纲参考</p>
     *
     * @param chatRequest 保存参数
     * @return 结果
     */
    @Override
    public NovelInfoVO referOutline(ChatRequest chatRequest) {
        NovelInfoVO vo = new NovelInfoVO();

        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        // 构建消息列表
        buildReferMessageList(chatRequest, chatModelVo);
        chatRequest.setRole(Message.Role.USER.getName());

        // 根据模型分类调用不同的处理逻辑
        IChatService chatService = chatServiceFactory.getChatService(chatModelVo.getCategory());
        if (LoginHelper.isLogin()) {
            chatRequest.setUserId(LoginHelper.getUserId());
            //报存对话信息
            chatService.saveChatMessage(chatRequest, chatMessageService);
        }
        ChatResponse response = chatService.chat(chatRequest);
        vo.setOutline(response.aiMessage().text());
        return vo;
    }

    private void buildReferMessageList(ChatRequest chatRequest, ChatModelVo chatModelVo) {
        // 获取对话消息列表
        List<Message> messages;
        if(CollUtil.isEmpty(chatRequest.getMessages())){

            IChatService chatService = chatServiceFactory.getChatService(chatModelVo.getCategory());
            messages = chatService.getChatMessageList(chatRequest.getSessionId(), chatMessageService);
        }else{
            messages = chatRequest.getMessages();
        }

        String sysPrompt = chatModelVo.getSystemPrompt();
        if (StringUtils.isEmpty(sysPrompt)) {
            sysPrompt = "作为资深小说策划，请根据用户输入的信息\n" +
                    "创建一个引人入胜的小说大纲。\n" +
                    "生成以下格式的JSON返回：\n" +
                    "{\n" +
                    "  \"name\": \"{小说名称}\",\n" +
                    "  \"outline\": {大纲},\n" +
                    "}";
        }
        // 设置系统默认提示词
        Message sysMessage = Message.builder().content(sysPrompt).role(Message.Role.SYSTEM).build();
        messages.add(0, sysMessage);
        // 用户对话内容
        messages.add(Message.builder().content(chatRequest.getPrompt()).role(Message.Role.USER).build());

        chatRequest.setMessages(messages);
        chatRequest.setSysPrompt(sysPrompt);
    }
}
