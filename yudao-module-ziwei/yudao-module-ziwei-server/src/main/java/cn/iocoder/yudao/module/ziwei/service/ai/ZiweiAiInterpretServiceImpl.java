package cn.iocoder.yudao.module.ziwei.service.ai;

import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartRespVO;
import cn.iocoder.yudao.module.ziwei.service.chart.ZiweiChartService;
import cn.iocoder.yudao.module.ziwei.service.rag.ZiweiRagService;
import cn.iocoder.yudao.module.ziwei.service.rag.ZiweiRagService.ZiweiRagResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 紫微斗数 AI 命盘解读 Service 实现
 * <p>
 * 使用 DeepSeek AI 对命盘进行专业解读，基于紫微斗数经典理论。
 *
 * @author JTWORLD
 */
@Service
@Slf4j
public class ZiweiAiInterpretServiceImpl implements ZiweiAiInterpretService {

    @Resource
    private ZiweiChartService chartService;

    @Resource
    private ChatModel chatModel;

    @Resource
    private ZiweiRagService ragService;

    @Override
    public String interpretChart(Long chartId, String userQuestion) {
        ZiweiChartRespVO chart = chartService.getChart(chartId);
        if (chart == null) return "命盘不存在";

        String chartData = buildChartDataText(chart);
        String question = (userQuestion != null && !userQuestion.isBlank())
                ? userQuestion : "请对这个命盘进行全面解读分析。";

        // === RAG 检索：从古籍中查找相关内容 ===
        String ragContext = buildRagContext(question);

        String systemPrompt = buildSystemPrompt();

        StringBuilder userMessage = new StringBuilder();
        userMessage.append("以下是命盘数据：\n\n").append(chartData);
        if (!ragContext.isEmpty()) {
            userMessage.append("\n\n【古籍参考原文】\n\n").append(ragContext);
            userMessage.append("\n请结合以上古籍原文进行专业解读。");
        }
        userMessage.append("\n\n用户问题：").append(question);

        Prompt prompt = new Prompt(java.util.List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userMessage.toString())
        ));

        try {
            return chatModel.call(prompt).getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("AI 解读失败", e);
            return "AI 解读服务暂时不可用，请稍后重试。错误信息：" + e.getMessage();
        }
    }

    @Override
    public Flux<String> interpretChartStream(Long chartId, String userQuestion) {
        ZiweiChartRespVO chart = chartService.getChart(chartId);
        if (chart == null) return Flux.just("命盘不存在");

        String chartData = buildChartDataText(chart);
        String question = (userQuestion != null && !userQuestion.isBlank())
                ? userQuestion : "请对这个命盘进行全面解读分析。";

        // === RAG 检索：从古籍中查找相关内容 ===
        String ragContext = buildRagContext(question);

        String systemPrompt = buildSystemPrompt();

        StringBuilder userMessage = new StringBuilder();
        userMessage.append("以下是命盘数据：\n\n").append(chartData);
        if (!ragContext.isEmpty()) {
            userMessage.append("\n\n【古籍参考原文】\n\n").append(ragContext);
            userMessage.append("\n请结合以上古籍原文进行专业解读。");
        }
        userMessage.append("\n\n用户问题：").append(question);

        Prompt prompt = new Prompt(java.util.List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userMessage.toString())
        ));

        try {
            return chatModel.stream(prompt).map(response -> {
                String text = response.getResult() != null && response.getResult().getOutput() != null
                        ? response.getResult().getOutput().getText() : "";
                return text != null ? text : "";
            });
        } catch (Exception e) {
            log.error("AI 流式解读失败", e);
            return Flux.just("AI 解读服务暂时不可用，请稍后重试。错误信息：" + e.getMessage());
        }
    }

    @Override
    public String interpretPalace(Long chartId, int palaceType, String userQuestion) {
        ZiweiChartRespVO chart = chartService.getChart(chartId);
        if (chart == null) return "命盘不存在";

        // 找到目标宫位
        ZiweiChartRespVO.PalaceVO targetPalace = null;
        if (chart.getPalaces() != null) {
            for (ZiweiChartRespVO.PalaceVO p : chart.getPalaces()) {
                if (p.getPalaceType() == palaceType) {
                    targetPalace = p;
                    break;
                }
            }
        }
        if (targetPalace == null) return "未找到指定宫位";

        String palaceData = buildPalaceDataText(chart, targetPalace);
        String question = (userQuestion != null && !userQuestion.isBlank())
                ? userQuestion : "请解读此宫位。";

        // === RAG 检索：从古籍中查找与该宫位相关的内容 ===
        String ragQuery = targetPalace.getPalaceName() + " " + question;
        String ragContext = buildRagContext(ragQuery);

        String systemPrompt = buildPalaceSystemPrompt();

        StringBuilder userMessage = new StringBuilder();
        userMessage.append("以下是命盘与宫位数据：\n\n").append(palaceData);
        if (!ragContext.isEmpty()) {
            userMessage.append("\n\n【古籍参考原文】\n\n").append(ragContext);
            userMessage.append("\n请结合以上古籍原文进行专业解读。");
        }
        userMessage.append("\n\n用户问题：").append(question);

        Prompt prompt = new Prompt(java.util.List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userMessage.toString())
        ));

        try {
            return chatModel.call(prompt).getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("AI 宫位解读失败", e);
            return "AI 解读服务暂时不可用，请稍后重试。错误信息：" + e.getMessage();
        }
    }

    // ========== Prompt 构建 ==========

    /**
     * RAG 检索古籍相关内容
     */
    private String buildRagContext(String question) {
        try {
            List<ZiweiRagResult> results = ragService.searchKnowledge(question, 3);
            if (results == null || results.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < results.size(); i++) {
                ZiweiRagResult r = results.get(i);
                sb.append("【参考").append(i + 1).append("】");
                if (r.bookTitle() != null && !r.bookTitle().isEmpty()) {
                    sb.append("出自《").append(r.bookTitle()).append("》");
                }
                if (r.chapter() != null && !r.chapter().isEmpty()) {
                    sb.append(" ").append(r.chapter());
                }
                sb.append("：\n").append(r.content()).append("\n\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            log.warn("RAG 检索失败，降级为仅基于模型知识解读: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 构建命盘文本数据
     */
    private String buildChartDataText(ZiweiChartRespVO chart) {
        StringBuilder sb = new StringBuilder();

        // 基本信息
        sb.append("【基本信息】\n");
        sb.append("八字：").append(chart.getYearPillar()).append("年 ")
                .append(chart.getMonthPillar()).append("月 ")
                .append(chart.getDayPillar()).append("日 ")
                .append(chart.getHourPillar()).append("时\n");
        sb.append("命宫：").append(chart.getMingGong()).append("\n");
        sb.append("身宫：").append(chart.getShenGong()).append("\n");
        sb.append("五行局：").append(chart.getWuxingJu()).append("\n");
        sb.append("性别：").append(chart.getGender() == 1 ? "男" : "女").append("\n\n");

        // 十二宫
        sb.append("【十二宫配置】\n");
        if (chart.getPalaces() != null) {
            for (ZiweiChartRespVO.PalaceVO palace : chart.getPalaces()) {
                sb.append(palace.getPalaceName()).append("（")
                        .append(palace.getDizhi()).append("宫，天干")
                        .append(palace.getTianGan()).append("）");
                if (palace.getIsShenGong() != null && palace.getIsShenGong()) {
                    sb.append("【身宫】");
                }
                sb.append("：\n");

                // 主星
                if (palace.getMajorStars() != null && !palace.getMajorStars().isEmpty()) {
                    sb.append("  主星：");
                    sb.append(palace.getMajorStars().stream()
                            .map(s -> formatStar(s))
                            .collect(Collectors.joining("、")));
                    sb.append("\n");
                }

                // 辅星
                if (palace.getAuxiliaryStars() != null && !palace.getAuxiliaryStars().isEmpty()) {
                    sb.append("  辅星：");
                    sb.append(palace.getAuxiliaryStars().stream()
                            .map(s -> formatStar(s))
                            .collect(Collectors.joining("、")));
                    sb.append("\n");
                }

                // 杂曜
                if (palace.getMinorStars() != null && !palace.getMinorStars().isEmpty()) {
                    sb.append("  杂曜：");
                    sb.append(palace.getMinorStars().stream()
                            .map(ZiweiChartRespVO.StarVO::getStarName)
                            .collect(Collectors.joining("、")));
                    sb.append("\n");
                }
            }
        }

        // 四化
        sb.append("\n【本命四化】\n");
        if (chart.getNatalSihua() != null) {
            ZiweiChartRespVO.NatalSihuaVO sihua = chart.getNatalSihua();
            appendSihuaStar(sb, "化禄", sihua.getHuaLu());
            appendSihuaStar(sb, "化权", sihua.getHuaQuan());
            appendSihuaStar(sb, "化科", sihua.getHuaKe());
            appendSihuaStar(sb, "化忌", sihua.getHuaJi());
        }

        // 大限
        sb.append("\n【大限运行】\n");
        if (chart.getDaxians() != null) {
            for (ZiweiChartRespVO.DaxianVO dx : chart.getDaxians()) {
                sb.append(dx.getAgeStart()).append("-").append(dx.getAgeEnd()).append("岁：")
                        .append(dx.getPalaceName()).append("（").append(dx.getDirection()).append("）\n");
            }
        }

        // 格局
        sb.append("\n【命格格局】\n");
        if (chart.getPatterns() != null && !chart.getPatterns().isEmpty()) {
            sb.append(String.join("、", chart.getPatterns())).append("\n");
        } else {
            sb.append("无特殊格局\n");
        }

        return sb.toString();
    }

    private String buildPalaceDataText(ZiweiChartRespVO chart, ZiweiChartRespVO.PalaceVO palace) {
        StringBuilder sb = new StringBuilder();
        sb.append("命盘八字：").append(chart.getYearPillar()).append(" ")
                .append(chart.getMonthPillar()).append(" ")
                .append(chart.getDayPillar()).append(" ")
                .append(chart.getHourPillar()).append("\n");
        sb.append("目标宫位：").append(palace.getPalaceName()).append("（")
                .append(palace.getDizhi()).append("宫，天干").append(palace.getTianGan()).append("）\n");

        if (palace.getMajorStars() != null && !palace.getMajorStars().isEmpty()) {
            sb.append("主星：");
            sb.append(palace.getMajorStars().stream().map(this::formatStar).collect(Collectors.joining("、")));
            sb.append("\n");
        }
        if (palace.getAuxiliaryStars() != null && !palace.getAuxiliaryStars().isEmpty()) {
            sb.append("辅星：");
            sb.append(palace.getAuxiliaryStars().stream().map(this::formatStar).collect(Collectors.joining("、")));
            sb.append("\n");
        }

        // 对宫信息
        int oppositeType = palace.getPalaceType() <= 6 ? palace.getPalaceType() + 6 : palace.getPalaceType() - 6;
        if (chart.getPalaces() != null) {
            for (ZiweiChartRespVO.PalaceVO p : chart.getPalaces()) {
                if (p.getPalaceType() == oppositeType) {
                    sb.append("对宫（").append(p.getPalaceName()).append("）：");
                    if (p.getMajorStars() != null && !p.getMajorStars().isEmpty()) {
                        sb.append(p.getMajorStars().stream().map(this::formatStar).collect(Collectors.joining("、")));
                    } else {
                        sb.append("无主星");
                    }
                    sb.append("\n");
                    break;
                }
            }
        }

        return sb.toString();
    }

    private String formatStar(ZiweiChartRespVO.StarVO star) {
        StringBuilder sb = new StringBuilder(star.getStarName());
        if (star.getBrightness() != null) {
            sb.append("[").append(star.getBrightness()).append("]");
        }
        if (star.getSihuaType() != null) {
            sb.append("(").append(star.getSihuaType()).append(")");
        }
        return sb.toString();
    }

    private void appendSihuaStar(StringBuilder sb, String label, ZiweiChartRespVO.StarVO star) {
        if (star != null) {
            sb.append(label).append("：").append(star.getStarName()).append("\n");
        }
    }

    /**
     * 系统提示词 - 整体命盘解读
     */
    private String buildSystemPrompt() {
        return """
                你是一位专业的紫微斗数命理师，精通紫微斗数经典理论（参考《紫微斗数全书》《骨髓赋》等古籍）。
                请根据用户提供的命盘数据进行专业、客观、有条理的解读。

                如果用户消息中包含【古籍参考原文】，请优先引用其中的古籍原文作为解读依据，
                并结合命盘实际数据进行分析比对。引用古籍时请注明出处。

                解读要求：
                1. 首先概述命盘的整体特点（命宫主星、五行局、格局等）
                2. 分析命宫及其三方四正的星曜配置，解读性格特质和人生主题
                3. 分析关键宫位：财帛宫（财运）、官禄宫（事业）、夫妻宫（婚姻感情）
                4. 解读本命四化（化禄、化权、化科、化忌）对人生的影响
                5. 分析大限走势，指出重要的人生阶段
                6. 如果命盘中有特殊格局，请说明其含义和影响
                7. 最后给出综合建议，帮助用户趋吉避凶

                注意事项：
                - 请使用通俗易懂的中文，避免过于晦涩的术语
                - 请保持客观中立，避免绝对化的断言
                - 请适当引用古籍经典作为佐证
                - 结尾请加上免责声明：以上解读仅供参考，命运掌握在自己手中
                """;
    }

    /**
     * 系统提示词 - 宫位解读
     */
    private String buildPalaceSystemPrompt() {
        return """
                你是一位专业的紫微斗数命理师。请根据用户提供的命盘和宫位数据，对该宫位进行专业解读。

                如果用户消息中包含【古籍参考原文】，请优先引用其中的古籍原文作为解读依据，
                并结合宫位实际星曜配置进行分析比对。引用古籍时请注明出处。

                解读要求：
                1. 分析该宫位的主星配置及其含义
                2. 分析辅星、杂曜的加持或削弱作用
                3. 说明星曜亮度（庙旺利陷）对该宫位的影响
                4. 如果有四化星落入该宫，说明其影响
                5. 结合对宫星曜进行综合分析（三方四正）
                6. 给出该宫位相关的具体建议

                请使用通俗易懂的中文，保持客观中立。结尾请加上免责声明。
                """;
    }

}
