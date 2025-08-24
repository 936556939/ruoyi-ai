package org.ruoyi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domin.bo.GenByNovelOutlineBO;
import org.ruoyi.domin.bo.NovelChapterBO;
import org.ruoyi.domin.dto.req.GenOutlineReqDTO;
import org.ruoyi.domin.entity.NovelChapter;
import org.ruoyi.domin.vo.NovelChapterVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * <p>
 * 小说章节信息 服务类
 * </p>
 *
 * @author xy
 * @since 2025-08-06
 */
public interface INovelChapterService extends IService<NovelChapter> {

    /**
     * 查询小说章节列表
     *
     * @param bo        查询参数
     * @param pageQuery 分页参数
     * @return 列表
     */
    TableDataInfo<NovelChapterVO> qryNovelChapterList(NovelChapterBO bo, PageQuery pageQuery);

    /**
     * <p>新增小说章节</p>
     *
     * @param bo 新增参数
     * @return 结果
     */
    int addNovelChapter(NovelChapterBO bo);

    /**
     * <p>修改小说章节</p>
     *
     * @param bo 修改参数
     * @return 结果
     */
    int editNovelChapter(NovelChapterBO bo);

    /**
     * <p>删除小说章节</p>
     *
     * @param ids ids
     * @param b   是否校验
     * @return 删除结果
     */
    int deleteWithValidByIds(List<Long> ids, boolean b);

    /**
     * <p>根据小说id生成章节</p>
     *
     * @param bo 参数
     * @return 结果
     */
    String genByNovelOutline(GenByNovelOutlineBO bo);

    /**
     * <p>按照提示生成小说大纲sse</p>
     */
    SseEmitter genByNovelOutlineAndPreSse(GenOutlineReqDTO bo);
}
