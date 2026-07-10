package cn.iocoder.yudao.module.ziwei.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ziwei.service.rag.ZiweiRagService;
import cn.iocoder.yudao.module.ziwei.service.rag.ZiweiRagService.ZiweiRagResult;
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
 * 用户端 - 紫微斗数 RAG 知识库 Controller
 * <p>
 * 提供命理古籍的上传、检索和删除功能。
 *
 * @author JTWORLD
 */
@Tag(name = "用户端 - 紫微斗数 RAG 知识库")
@RestController
@RequestMapping("/ziwei/rag")
@Validated
public class ZiweiRagController {

    @Resource
    private ZiweiRagService ragService;

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

    @GetMapping("/search")
    @Operation(summary = "检索古籍知识")
    public CommonResult<List<ZiweiRagResult>> searchKnowledge(
            @Parameter(description = "查询内容", required = true) @RequestParam("query") @NotBlank String query,
            @Parameter(description = "返回结果数，默认 5") @RequestParam(value = "topK", defaultValue = "5") int topK) {

        List<ZiweiRagResult> results = ragService.searchKnowledge(query, topK);
        return success(results);
    }

    @DeleteMapping("/document/{id}")
    @Operation(summary = "删除上传的古籍及其向量")
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
