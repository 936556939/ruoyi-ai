package org.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domin.bo.NovelInfoBO;
import org.ruoyi.domin.dto.req.GenOutlineReqDTO;
import org.ruoyi.domin.entity.NovelInfo;
import org.ruoyi.domin.vo.NovelInfoVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * <p>
 * 小说信息 服务类
 * </p>
 *
 * @author xy
 * @since 2025-08-05
 */
public interface INovelInfoService extends IService<NovelInfo> {

    /**
     * 查询小说列表
     *
     * @param bo        查询参数
     * @param pageQuery 分页参数
     * @return 列表
     */
    TableDataInfo<NovelInfoVO> qryNovelList(NovelInfoBO bo, PageQuery pageQuery);

    /**
     * <p>新增小说</p>
     *
     * @param bo 新增参数
     * @return 结果
     */
    int addNovel(NovelInfoBO bo);

    /**
     * <p>修改小说信息</p>
     *
     * @param bo 修改参数
     * @return 结果
     */
    int editNovel(NovelInfoBO bo);

    /**
     * <p>删除小说</p>
     *
     * @param ids ids
     * @param b   是否校验
     * @return 删除结果
     */
    int deleteWithValidByIds(List<Long> ids, boolean b);

    /**
     * <p>生成小说大纲</p>
     *
      * @param bo 生成参数
     * @return 结果
     */
    String genOutline(ChatRequest bo);

    /**
     * <p>生成小说大纲sse</p>
     *
     * @param bo 生成参数
     * @return 生成结果
     */
    SseEmitter genOutlineSse(GenOutlineReqDTO bo);

    /**
     * <p>保存小说大纲</p>
     *
     * @param bo 保存参数
     * @return 结果
     */
    NovelInfoVO referOutline(ChatRequest bo);
}
