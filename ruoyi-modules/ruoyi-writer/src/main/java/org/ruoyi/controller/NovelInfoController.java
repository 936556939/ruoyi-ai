package org.ruoyi.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domin.bo.NovelInfoBO;
import org.ruoyi.domin.dto.req.GenOutlineReqDTO;
import org.ruoyi.domin.vo.NovelInfoVO;
import org.ruoyi.service.INovelInfoService;
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
 * Time：11:20
 * Description：小说信息
 *
 * @author xiaoyan
 * @version 1.0
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/novel/info")
public class NovelInfoController extends BaseController {

    private final INovelInfoService service;

    /**
     * <p>小说列表</p>
     */
    @GetMapping("/list")
    public TableDataInfo<NovelInfoVO> list(NovelInfoBO bo, PageQuery pageQuery) {
        return service.qryNovelList(bo, pageQuery);
    }

    /**
     * <p>新增小说</p>
     */
    @Log(title = "小说", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody  NovelInfoBO bo) {
        return toAjax(service.addNovel(bo));
    }

    /**
     * <p>修改小说信息</p>
     */
    @Log(title = "小说", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody  NovelInfoBO bo) {
        return toAjax(service.editNovel(bo));
    }

    /**
     * <p>删除小说信息</p>
     */
    @Log(title = "小说", businessType = BusinessType.UPDATE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(service.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * <p>按照提示生成小说大纲</p>
     */
    @PostMapping("/genOutline")
    public String genOutline(@Validated @RequestBody ChatRequest bo) {
        return service.genOutline(bo);
    }

    /**
     * <p>按照提示生成小说大纲sse</p>
     */
    @PostMapping("/genOutlineSse")
    public SseEmitter genOutlineSse(@Validated @RequestBody GenOutlineReqDTO bo) {
        return service.genOutlineSse(bo);
    }

    /**
     * <p>历史对话总结小说大纲</p>
     */
    @PostMapping("/referOutline")
    public R<NovelInfoVO> referOutline(@Validated @RequestBody ChatRequest bo) {
        return R.ok(service.referOutline(bo));
    }


}
