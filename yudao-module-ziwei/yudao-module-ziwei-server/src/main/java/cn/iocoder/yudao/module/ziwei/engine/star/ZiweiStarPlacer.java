package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.StarPosition;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarBrightnessEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 紫微星及紫微系6星安放
 * <p>
 * 紫微星位置由农历日 + 五行局数决定，使用 iztro 商余迭代算法。
 * 确定紫微星后，逆时针安放其余5星：天机、太阳、武曲、天同、廉贞。
 *
 * @author JTWORLD
 */
public class ZiweiStarPlacer {

    // 紫微系6星顺序（逆时针排列，对应从紫微位置逆数的宫位数）
    // iztro: ziweiGroup = [ziwei, tianji, (空), taiyang, wuqu, tiantong, (空), (空), lianzhen]
    private static final String[] ZIWEI_GROUP = {"ziwei", "tianji", "taiyang", "wuqu", "tiantong", "lianzhen"};

    private static final String[] ZIWEI_NAMES = {"紫微", "天机", "太阳", "武曲", "天同", "廉贞"};

    /**
     * 定位紫微星所在地支（iztro 商余迭代法）
     * <p>
     * 口诀："局数除日数，商数宫前走；若见数无余，便要起虎口；日数小于局，还直宫中守。"
     * <p>
     * 算法：
     * <ol>
     * <li>不断在农历日上加 offset（从0开始递增），直到 (日 + offset) 能被局数整除</li>
     * <li>商 = (日 + offset) / 局数，商 %= 12</li>
     * <li>紫微初始位置 = 商 - 1（以寅为0的iztro索引）</li>
     * <li>offset 为偶数：紫微位置 += offset（逆时针）</li>
     * <li>offset 为奇数：紫微位置 -= offset（顺时针）</li>
     * <li>最后将 iztro 索引（0=寅）转换为绝对地支索引（0=子）</li>
     * </ol>
     *
     * @param lunarDay       农历日 (1-30)
     * @param wuxingJuNumber 五行局数 (2-6)
     * @return 紫微星所在地支
     */
    public DiZhiEnum findZiweiPosition(int lunarDay, int wuxingJuNumber) {
        int offset = -1;
        int quotient = 0;
        int remainder;

        // 不断加 offset 直到日数能被局数整除
        do {
            offset++;
            int divisor = lunarDay + offset;
            quotient = divisor / wuxingJuNumber;
            remainder = divisor % wuxingJuNumber;
        } while (remainder != 0);

        // 商取模12，减1得到以寅为0的初始紫微索引
        quotient %= 12;
        int ziweiIztroIndex = quotient - 1;

        // 根据 offset 奇偶决定方向
        if (offset % 2 == 0) {
            // 偶数：紫微逆时针移 offset 位（iztro 索引增加）
            ziweiIztroIndex += offset;
        } else {
            // 奇数：紫微顺时针移 offset 位（iztro 索引减少）
            ziweiIztroIndex -= offset;
        }

        // iztro 索引（0=寅）转为绝对地支索引（0=子）：absIndex = (iztroIndex + 2) % 12
        ziweiIztroIndex = ((ziweiIztroIndex % 12) + 12) % 12;  // fixIndex
        int absIndex = (ziweiIztroIndex + 2) % 12;

        return DiZhiEnum.ofIndex(absIndex);
    }

    /**
     * 安放紫微系6星到对应宫位（逆时针排列）
     * <p>
     * 紫微 → 天机(逆1) → 空 → 太阳(逆3) → 武曲(逆4) → 天同(逆5) → 空 → 空 → 廉贞(逆8)
     * <p>
     * 返回 Map：(星曜编码 → 所在地支)
     *
     * @param ziweiDiZhi 紫微星所在地支
     */
    public Map<String, DiZhiEnum> placeZiweiGroup(DiZhiEnum ziweiDiZhi) {
        Map<String, DiZhiEnum> result = new LinkedHashMap<>();

        // iztro 紫微系偏移（逆时针步数）：紫微=0, 天机=1, 太阳=3, 武曲=4, 天同=5, 廉贞=8
        int[] steps = {0, 1, 3, 4, 5, 8};
        for (int i = 0; i < ZIWEI_GROUP.length; i++) {
            DiZhiEnum pos = ziweiDiZhi.moveCounterClockwise(steps[i]);
            result.put(ZIWEI_GROUP[i], pos);
        }

        return result;
    }

    /**
     * 获取紫微系星曜的名称
     */
    public static String getStarName(int index) {
        return ZIWEI_NAMES[index];
    }

    /**
     * 获取紫微系星曜编码
     */
    public static String getStarCode(int index) {
        return ZIWEI_GROUP[index];
    }

    /**
     * 创建星曜落位对象
     */
    public StarPosition createStarPosition(String starCode, StarBrightnessEnum brightness) {
        String starName = switch (starCode) {
            case "ziwei" -> "紫微"; case "tianji" -> "天机"; case "taiyang" -> "太阳";
            case "wuqu" -> "武曲"; case "tiantong" -> "天同"; case "lianzhen" -> "廉贞";
            default -> starCode;
        };
        return StarPosition.builder()
                .starCode(starCode)
                .starName(starName)
                .starType(StarTypeEnum.MAJOR)
                .brightness(brightness)
                .build();
    }

}
