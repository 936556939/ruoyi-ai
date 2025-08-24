package org.ruoyi.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domin.bo.GenByNovelOutlineBO;
import org.ruoyi.domin.bo.NovelChapterBO;
import org.ruoyi.domin.dto.req.GenOutlineReqDTO;
import org.ruoyi.domin.vo.NovelChapterVO;
import org.ruoyi.service.INovelChapterService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * Project：ruoyi-ai2
 * Date：2025/8/16
 * Time：11:39
 * Description：章节信息
 *
 * @author xiaoyan
 * @version 1.0
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/novel/chapter")
public class NovelChapterController extends BaseController {

    private final INovelChapterService service;

    /**
     * <p>小说章节列表</p>
     */
    @GetMapping("/list")
    public TableDataInfo<NovelChapterVO> list(@Validated NovelChapterBO bo, PageQuery pageQuery) {
        return service.qryNovelChapterList(bo, pageQuery);
    }

    /**
     * <p>新增小说章节</p>
     */
    @Log(title = "小说", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody NovelChapterBO bo) {
        return toAjax(service.addNovelChapter(bo));
    }

    /**
     * <p>修改小说章节</p>
     */
    @Log(title = "小说", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody NovelChapterBO bo) {
        return toAjax(service.editNovelChapter(bo));
    }

    /**
     * <p>删除小说章节</p>
     */
    @Log(title = "小说", businessType = BusinessType.UPDATE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(service.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * <p>根据小说大纲生成章节大纲</p>
     */
    @Log(title = "小说", businessType = BusinessType.INSERT)
    @PostMapping("/genByNovelOutline")
    public R<String> genByNovelOutline(@Validated(AddGroup.class) @RequestBody GenByNovelOutlineBO bo) {
        return R.ok(service.genByNovelOutline(bo));
    }

    /**
     * <p>按照提示生成小说大纲sse</p>
     */
    @PostMapping("/genByNovelOutlineAndPreSse")
    public SseEmitter genByNovelOutlineAndPreSse(@Validated @RequestBody GenOutlineReqDTO bo) {
        return service.genByNovelOutlineAndPreSse(bo);
    }

}
