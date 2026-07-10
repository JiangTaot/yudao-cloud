package cn.iocoder.yudao.module.ziwei.service.rag.splitter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 紫微斗数古文定制切分器
 * <p>
 * 针对命理古籍（如《紫微斗数全集》《天纪》）的结构特征进行智能切分：
 * <ul>
 *   <li>优先按卷/篇/章大标题断开（如"卷之一""篇二""第一章"）</li>
 *   <li>赋文/歌诀保持完整（不截断对仗句）</li>
 *   <li>星曜条目独立成段（每颗星一个 chunk）</li>
 *   <li>双换行作为次级切分点</li>
 *   <li>重叠窗口保证上下文连贯</li>
 * </ul>
 *
 * @author JTWORLD
 */
@Slf4j
public class ZiweiTextSplitter {

    /** 每段目标大小（字符数，约 500-800 token） */
    private static final int DEFAULT_CHUNK_SIZE = 800;

    /** 段间重叠字符数 */
    private static final int DEFAULT_OVERLAP = 80;

    private final int chunkSize;
    private final int chunkOverlap;

    // ---- 古文切分正则 ----

    /** 卷标题：卷之一、卷二、卷第一 等 */
    private static final Pattern VOLUME_PATTERN = Pattern.compile(
            "(?:^|\\n)[\\s]*卷[之第]?[一二三四五六七八九十百千0-9]+[\\s\\n]");

    /** 篇/章标题：篇一、第二章 等 */
    private static final Pattern CHAPTER_PATTERN = Pattern.compile(
            "(?:^|\\n)[\\s]*(?:第[一二三四五六七八九十百千0-9]+[篇章节]|[篇章节][一二三四五六七八九十百千0-9]+)");

    /** 赋文/歌诀/口诀开头 */
    private static final Pattern VERSE_PATTERN = Pattern.compile(
            "(?:^|\\n)[\\s]*(?:赋曰|歌曰|诀曰|诗曰|赞曰|论曰|注曰|解曰|又曰)");

    /** 星曜独立条目：某星属某 开头 */
    private static final Pattern STAR_ENTRY_PATTERN = Pattern.compile(
            "(?:^|\\n)[\\s]*(?:紫微|天机|太阳|武曲|天同|廉贞|天府|太阴|贪狼|巨门|天相|天梁|七杀|破军"
                    + "|左辅|右弼|文昌|文曲|天魁|天钺|禄存|天马|擎羊|陀罗|火星|铃星|地空|地劫"
                    + "|天刑|天姚|红鸾|天喜|三台|八座|恩光|天贵|天才|天寿|天空|天哭|天虚"
                    + "|龙池|凤阁|孤辰|寡宿|咸池|华盖|破碎|天官|天福|解神|天厨|蜚廉"
                    + "|年解|劫煞|龙德|月德|封诰|台辅|天巫|阴煞|天月|旬空|截空"
                    + "|天使|天伤|大耗|天德|命宫|兄弟|夫妻|子女|财帛|疾厄|迁移|交友|官禄|田宅|福德|父母)"
                    + "\\s*[属为]");

    public ZiweiTextSplitter() {
        this(DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    public ZiweiTextSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = Math.min(chunkOverlap, chunkSize / 2);
    }

    /**
     * 切分文本
     *
     * @param text 原始文本
     * @return 切分后的文本段列表
     */
    public List<String> split(String text) {
        if (StrUtil.isEmpty(text)) {
            return Collections.emptyList();
        }
        return splitRecursive(text);
    }

    /**
     * 递归切分
     */
    private List<String> splitRecursive(String text) {
        List<String> chunks = new ArrayList<>();

        // 1. 文本较短，直接返回
        if (text.length() <= chunkSize) {
            chunks.add(text.trim());
            return chunks;
        }

        // 2. 尝试按卷标题切分
        List<String> splits = splitByPattern(text, VOLUME_PATTERN);
        if (splits.size() > 1) {
            return mergeSplits(splits);
        }

        // 3. 尝试按篇章标题切分
        splits = splitByPattern(text, CHAPTER_PATTERN);
        if (splits.size() > 1) {
            return mergeSplits(splits);
        }

        // 4. 尝试按赋文/歌诀开头切分
        splits = splitByPattern(text, VERSE_PATTERN);
        if (splits.size() > 1) {
            return mergeSplits(splits);
        }

        // 5. 尝试按星曜条目切分
        splits = splitByPattern(text, STAR_ENTRY_PATTERN);
        if (splits.size() > 1) {
            return mergeSplits(splits);
        }

        // 6. 按双换行（段落）切分
        String[] paragraphs = text.split("\\n\\s*\\n");
        if (paragraphs.length > 1) {
            return mergeParagraphs(paragraphs);
        }

        // 7. 按单换行切分
        String[] lines = text.split("\\n");
        if (lines.length > 1) {
            return mergeLines(lines);
        }

        // 8. 兜底：按字符数硬切
        return forceSplit(text);
    }

    /**
     * 按正则模式切分
     */
    private List<String> splitByPattern(String text, Pattern pattern) {
        List<String> result = new ArrayList<>();
        java.util.regex.Matcher matcher = pattern.matcher(text);

        int lastEnd = 0;
        while (matcher.find()) {
            int start = matcher.start();
            // 保存标题前的内容（如果有）
            if (start > lastEnd) {
                String before = text.substring(lastEnd, start).trim();
                if (StrUtil.isNotEmpty(before)) {
                    result.add(before);
                }
            }
            lastEnd = start; // 标题作为下一段的开头
        }

        // 剩余部分
        if (lastEnd < text.length()) {
            String remaining = text.substring(lastEnd).trim();
            if (StrUtil.isNotEmpty(remaining)) {
                result.add(remaining);
            }
        }
        return result;
    }

    /**
     * 合并切分后的段落
     */
    private List<String> mergeSplits(List<String> splits) {
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String split : splits) {
            String trimmed = split.trim();
            if (StrUtil.isEmpty(trimmed)) {
                continue;
            }

            // 单个片段太长，递归切分
            if (trimmed.length() > chunkSize) {
                // 先保存当前累积的
                if (current.length() > 0) {
                    chunks.add(current.toString().trim());
                    current.setLength(0);
                }
                // 递归处理长片段
                chunks.addAll(splitRecursive(trimmed));
                continue;
            }

            // 加上新片段后会超限，保存当前并开始新段
            if (current.length() > 0 && current.length() + trimmed.length() > chunkSize) {
                chunks.add(current.toString().trim());
                // 保留重叠部分
                String overlap = getOverlap(current.toString(), chunkOverlap);
                current.setLength(0);
                if (StrUtil.isNotEmpty(overlap)) {
                    current.append(overlap).append("\n");
                }
            }

            if (current.length() > 0) {
                current.append("\n");
            }
            current.append(trimmed);
        }

        // 最后一段
        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }
        return chunks;
    }

    /**
     * 合并段落（按双换行分好的）
     */
    private List<String> mergeParagraphs(String[] paragraphs) {
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String para : paragraphs) {
            String trimmed = para.trim();
            if (StrUtil.isEmpty(trimmed)) {
                continue;
            }

            if (trimmed.length() > chunkSize) {
                if (current.length() > 0) {
                    chunks.add(current.toString().trim());
                    current.setLength(0);
                }
                chunks.addAll(splitRecursive(trimmed));
                continue;
            }

            if (current.length() > 0 && current.length() + trimmed.length() > chunkSize) {
                chunks.add(current.toString().trim());
                String overlap = getOverlap(current.toString(), chunkOverlap);
                current.setLength(0);
                if (StrUtil.isNotEmpty(overlap)) {
                    current.append(overlap).append("\n");
                }
            }

            if (current.length() > 0) {
                current.append("\n\n");
            }
            current.append(trimmed);
        }

        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }
        return chunks;
    }

    /**
     * 合并行
     */
    private List<String> mergeLines(String[] lines) {
        return mergeParagraphs(lines);
    }

    /**
     * 强制按字符切分
     */
    private List<String> forceSplit(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            // 尽量在句子边界断开
            if (end < text.length()) {
                int boundary = findSentenceBoundary(text, end, start + chunkSize / 2);
                if (boundary > start) {
                    end = boundary;
                }
            }
            chunks.add(text.substring(start, end).trim());
            start = Math.max(start + chunkSize - chunkOverlap, start + 1);
        }
        log.warn("文本过长，已强制按字符切分，可能影响语义完整性");
        return chunks;
    }

    /**
     * 在给定范围内查找句子边界
     */
    private int findSentenceBoundary(String text, int preferredEnd, int minEnd) {
        // 从 preferredEnd 往前搜索句子结束符
        for (int i = preferredEnd; i >= minEnd; i--) {
            char c = text.charAt(i);
            if (c == '。' || c == '？' || c == '！' || c == '\n' || c == '；') {
                return i + 1;
            }
        }
        return preferredEnd;
    }

    /**
     * 获取文本末尾的重叠部分
     */
    private String getOverlap(String text, int overlapSize) {
        if (text.length() <= overlapSize) {
            return text;
        }
        return text.substring(text.length() - overlapSize);
    }

}
