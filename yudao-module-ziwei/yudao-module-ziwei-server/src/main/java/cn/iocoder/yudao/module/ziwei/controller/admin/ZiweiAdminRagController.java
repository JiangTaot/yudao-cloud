package cn.iocoder.yudao.module.ziwei.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiRagDocumentDO;
import cn.iocoder.yudao.module.ziwei.dal.mysql.ZiweiRagDocumentMapper;
import cn.iocoder.yudao.module.ziwei.service.rag.ZiweiRagService;
import cn.iocoder.yudao.module.ziwei.service.rag.ZiweiRagService.ZiweiRagResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 紫微斗数 RAG 知识库 Controller
 *
 * @author JTWORLD
 */
@Tag(name = "管理后台 - 紫微斗数 RAG 知识库")
@RestController
@RequestMapping("/ziwei/rag")
@Validated
public class ZiweiAdminRagController {

    @Resource
    private ZiweiRagService ragService;

    @Resource
    private ZiweiRagDocumentMapper documentMapper;

    @PostMapping("/upload")
    @Operation(summary = "上传命理古籍 PDF，自动解析并向量化")
    public CommonResult<UploadRespVO> uploadBook(
            @Parameter(description = "PDF 文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "书名") @RequestParam("bookTitle") String bookTitle,
            @Parameter(description = "作者/来源（可选）") @RequestParam(value = "bookAuthor", required = false) String bookAuthor)
            throws IOException {

        byte[] fileBytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        Long documentId = ragService.uploadBook(fileBytes, fileName, bookTitle, bookAuthor);

        return success(new UploadRespVO(documentId, fileName, bookTitle));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询古籍文档列表")
    public CommonResult<PageResult<ZiweiRagDocumentDO>> getDocumentPage(
            @Parameter(description = "页码，默认 1") @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页条数，默认 10") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "书名（模糊搜索）") @RequestParam(value = "bookTitle", required = false) String bookTitle) {

        Page<ZiweiRagDocumentDO> page = documentMapper.selectPage(
                new Page<>(pageNo, pageSize),
                new LambdaQueryWrapperX<ZiweiRagDocumentDO>()
                        .likeIfPresent(ZiweiRagDocumentDO::getBookTitle, bookTitle)
                        .orderByDesc(ZiweiRagDocumentDO::getId));
        PageResult<ZiweiRagDocumentDO> pageResult = new PageResult<>(page.getRecords(), page.getTotal());
        return success(pageResult);
    }

    @GetMapping("/search")
    @Operation(summary = "检索古籍知识")
    public CommonResult<List<ZiweiRagResult>> searchKnowledge(
            @Parameter(description = "查询内容", required = true) @RequestParam("query") @NotBlank String query,
            @Parameter(description = "返回结果数，默认 5") @RequestParam(value = "topK", defaultValue = "5") int topK) {

        return success(ragService.searchKnowledge(query, topK));
    }

    @DeleteMapping("/document/{id}")
    @Operation(summary = "删除古籍文档及其向量")
    public CommonResult<Boolean> deleteDocument(
            @Parameter(description = "文档 ID") @PathVariable("id") Long id) {

        ragService.deleteDocument(id);
        return success(true);
    }

    /**
     * 上传响应 VO
     */
    public record UploadRespVO(
            Long documentId,
            String fileName,
            String bookTitle
    ) {}

}
