package org.ruoyi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.factory.ChatServiceFactory;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.core.utils.DateUtils;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.domin.bo.CreateNovelChapterBO;
import org.ruoyi.domin.bo.GenByNovelOutlineBO;
import org.ruoyi.domin.bo.NovelChapterBO;
import org.ruoyi.domin.dto.req.GenOutlineReqDTO;
import org.ruoyi.domin.entity.NovelChapter;
import org.ruoyi.domin.entity.NovelInfo;
import org.ruoyi.domin.vo.NovelChapterVO;
import org.ruoyi.mapper.NovelChapterMapper;
import org.ruoyi.mapper.NovelInfoMapper;
import org.ruoyi.service.IChatMessageService;
import org.ruoyi.service.IChatModelService;
import org.ruoyi.service.INovelChapterService;
import org.ruoyi.service.INovelInfoService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/17
 * Time：17:33
 * Description：TODO
 *
 * @author xiaoyan
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NovelChapterServiceImpl extends ServiceImpl<NovelChapterMapper, NovelChapter> implements INovelChapterService {

    private final INovelInfoService novelInfoService;

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
    public TableDataInfo<NovelChapterVO> qryNovelChapterList(NovelChapterBO bo, PageQuery pageQuery) {
        IPage<NovelChapterVO> page = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(page);
    }


    private Wrapper<NovelChapter> buildQueryWrapper(NovelChapterBO bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper<NovelChapter> wrapper = Wrappers.query();
        wrapper.eq(ObjectUtil.isNotNull(bo.getId()), "id", bo.getId())
                .eq("novel_id", bo.getNovelId())
                .like(StringUtils.isNotBlank(bo.getConversationId()), "conversation_id", bo.getConversationId())
                .like(StringUtils.isNotBlank(bo.getChapterNo()), "chapter_no", bo.getChapterNo())
                .like(StringUtils.isNotBlank(bo.getChapterName()), "chapter_name", bo.getChapterName())
                .like(StringUtils.isNotBlank(bo.getChapterOutline()), "chapter_outline", bo.getChapterOutline())
                .like(StringUtils.isNotBlank(bo.getChapterData()), "chapter_data", bo.getChapterData())
                .like(ObjectUtil.isNotNull(bo.getOrderNo()), "order_no", bo.getOrderNo());
        wrapper.orderByAsc("id").orderByAsc("order_no");
        return wrapper;
    }


    /**
     * <p>新增小说</p>
     *
     * @param bo 新增参数
     * @return 结果
     */
    @Override
    public int addNovelChapter(NovelChapterBO bo) {
        NovelChapter add = MapstructUtils.convert(bo, NovelChapter.class);
        validEntityBeforeSave(add);
        return baseMapper.insert(add);
    }

    private void validEntityBeforeSave(NovelChapter info) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * <p>修改小说信息</p>
     *
     * @param bo 修改参数
     * @return 结果
     */
    @Override
    public int editNovelChapter(NovelChapterBO bo) {
        NovelChapter info = MapstructUtils.convert(bo, NovelChapter.class);
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
     * <p>根据小说id生成章节</p>
     *
     * @param bo 参数
     * @return 结果
     */
    @Override
    public String genByNovelOutline(GenByNovelOutlineBO bo) {
        ChatRequest chatRequest = new ChatRequest();
        if (null == chatRequest.getSessionId()) {
            chatRequest.setSessionId(IdWorker.getId());
        }
        NovelInfo novelInfo = novelInfoService.getBaseMapper().selectById(bo.getNovelId());
        Integer chapterSize = bo.getChapterSize();
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        // 根据模型分类调用不同的处理逻辑
        IChatService chatService = chatServiceFactory.getChatService(chatModelVo.getCategory());
        // 获取章节信息
        NovelChapter novelChapter = baseMapper.selectOne(new LambdaQueryWrapper<NovelChapter>()
                .eq(NovelChapter::getChapterNo, bo.getChapterNo())
        );
        if (novelChapter == null) {
            // 获取最后一章
            novelChapter = baseMapper.selectOne(new LambdaQueryWrapper<NovelChapter>()
                    .orderByDesc(NovelChapter::getOrderNo)
                    .last("limit 1")
            );
        }
        String outline = novelInfo.getOutline();
        String promptString = "你是一位经验丰富的网文创作助手，尤其擅长将小说大纲扩展为具体、生动、符合网文类型特点的情节段落。你的核心任务是**严格依据用户提供的小说大纲**，" +
                "为指定的章节生成情节内容。 请基于以下信息创建一个富有爆点的小说章节，并生成富有吸引力的章节标题，一共" +
                bo.getChapterSize() +
                "章左右，根据章节数量控制剧情节奏，剧情进展过快可以适当水文，但不可脱离大纲，简介：" +
                outline + "\n";

        int chapterNo = novelChapter == null ? 1 : novelChapter.getOrderNo();
        int space = bo.getSpace();
        //根据大纲信息，生成章节
        for (int i = chapterNo; i <= chapterSize; i += space) {
            //查询小说的前几个章节，以保证剧情连贯性
            List<NovelChapter> novelChapters = baseMapper.selectList(new LambdaQueryWrapper<NovelChapter>()
                    .eq(NovelChapter::getNovelId, bo.getNovelId())
                    .last("limit " + bo.getBeforeChapterOutline())
                    .orderByDesc(NovelChapter::getCreateTime)
            ).stream().sorted(Comparator.comparing(NovelChapter::getCreateTime)).toList();
            // 获取对话消息列表
            List<Message> messages = CollUtil.isEmpty(chatRequest.getMessages()) ? CollUtil.newArrayList() : chatRequest.getMessages();
            messages.add(Message.builder().content(novelInfo.getOutline()).role(Message.Role.USER).build());
            for (NovelChapter chapter : novelChapters) {
                messages.add(Message.builder().content(chapter.getChapterOutline()).role(Message.Role.USER).build());
            }

            String prompt;
            if (chapterNo + space > bo.getChapterSize()) {
                prompt = "写一下第" + chapterNo + "章到第" + bo.getChapterSize() + "的章节大纲，大概300字。";
            } else {
                prompt = "写一下第" + chapterNo + "章到第" + (chapterNo + space - 1) + "的章节大纲，大概300字。";
            }
            // 用户对话内容
            messages.add(Message.builder().content(prompt).role(Message.Role.USER).build());
            chatRequest.setRole(Message.Role.USER.getName());

            //生成结果
            String text = chatService.chat(chatRequest).aiMessage().text();

            //提取章节
            ChatRequest createRequest = new ChatRequest();
            createRequest.setRole(Message.Role.USER.getName());
            createRequest.setMessages(CollUtil.newArrayList(Message.builder().content(text).role(Message.Role.USER).build()));
            createRequest.setModel(chatRequest.getModel());
            CreateNovelChapterBO createNovelChapterBO = chatService.create(CreateNovelChapterBO.class, chatRequest);
            NovelChapter chapter = BeanUtil.copyProperties(createNovelChapterBO, NovelChapter.class);
            chapter.setNovelId(bo.getNovelId());
            chapter.setConversationId(String.valueOf(chatRequest.getSessionId()));
            chapter.setOrderNo(i);
            baseMapper.insert(chapter);
        }
        return null;
    }

    /**
     * <p>按照提示生成小说大纲sse</p>
     *
     * @param bo
     */
    @Override
    public SseEmitter genByNovelOutlineAndPreSse(GenOutlineReqDTO bo) {
        SseEmitter sseEmitter = new SseEmitter(0L);
        ChatRequest chatRequest = new ChatRequest();
        if (null == bo.getConversationId()) {
            chatRequest.setSessionId(IdWorker.getId());
        } else {
            chatRequest.setSessionId(bo.getConversationId());
        }
        chatRequest.setModel(bo.getModel());
        chatRequest.setPrompt(bo.getPrompt());
        chatRequest.setSysPrompt(bo.getSysPrompt());
        chatRequest.setStream(bo.getStream());

        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        // 构建消息列表
        IChatService chatService = chatServiceFactory.getChatService(chatModelVo.getCategory());

        NovelInfo novelInfo = novelInfoService.getBaseMapper().selectById(bo.getNovelId());
        String outline = novelInfo.getOutline();
        // 获取对话消息列表
        List<Message> messages = new ArrayList<>();
        StringBuilder promptString = new StringBuilder()
                .append("你是一位经验丰富的网文创作助手，尤其擅长将小说大纲扩展为具体、生动、符合网文类型特点的情节段落。你的核心任务是**严格依据用户提供的小说大纲**，")
                .append("为指定的章节生成情节内容。 请基于以下信息创建一个富有爆点的小说章节，并生成富有吸引力的章节标题，根据章节数量控制剧情节奏，剧情进展过快可以适当水文，但不可脱离大纲，简介：")
                .append(outline).append("\n");
        if(StrUtil.isNotEmpty(bo.getChapterIds())){
            List<NovelChapterVO> novelChapterVOS = baseMapper.selectVoByIds(StrUtil.split(bo.getChapterIds(), ","));
            for (NovelChapterVO novelChapterVO : novelChapterVOS) {
                promptString.append("第").append(novelChapterVO.getOrderNo()).append("章：").append(novelChapterVO.getChapterData()).append("\n");
            }
        }
        promptString.append("作为资深小说策划，请根据用户输入的信息生成下一章文章内容，大概3000字");
        // 设置系统默认提示词
        Message sysMessage = Message.builder().content(promptString.toString()).role(Message.Role.SYSTEM).build();
        messages.add(0, sysMessage);
        // 用户对话内容
        messages.add(Message.builder().content(chatRequest.getPrompt()).role(Message.Role.USER).build());

        chatRequest.setMessages(messages);
        chatRequest.setSysPrompt(promptString.toString());
        chatRequest.setRole(Message.Role.USER.getName());
        chatService.chatStream(chatRequest, sseEmitter);
        return sseEmitter;
    }
}
