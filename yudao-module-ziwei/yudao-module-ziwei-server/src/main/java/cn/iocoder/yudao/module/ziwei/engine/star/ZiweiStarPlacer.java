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
 * 紫微星位置由农历日 + 五行局数决定，使用标准商余算法。
 * 确定紫微星后，逆时针安放其余5星：天机、太阳、武曲、天同、廉贞。
 *
 * @author JTWORLD
 */
public class ZiweiStarPlacer {

    // 紫微系6星顺序（逆时针排列）
    private static final String[] ZIWEI_GROUP = {"ziwei", "tianji", "taiyang", "wuqu", "tiantong", "lianzhen"};

    private static final String[] ZIWEI_NAMES = {"紫微", "天机", "太阳", "武曲", "天同", "廉贞"};

    /**
     * 定位紫微星所在地支
     * <p>
     * 算法（标准商余法）：
     * <pre>
     * 1. 日数 ÷ 局数 = 商 X, 余 Y
     * 2. 从寅宫(index=2)开始：
     *    - Y=0: 顺数 X-1 位
     *    - Y奇数: 顺数 X+Y-1 位
     *    - Y偶数: 逆数 局数-Y+1 位，再顺数 X 位
     *    (等价简化公式)
     * </pre>
     *
     * @param lunarDay      农历日 (1-30)
     * @param wuxingJuNumber 五行局数 (2-6)
     * @return 紫微星所在地支
     */
    public DiZhiEnum findZiweiPosition(int lunarDay, int wuxingJuNumber) {
        int X = lunarDay / wuxingJuNumber;  // 商
        int Y = lunarDay % wuxingJuNumber;  // 余

        int offset;
        if (Y == 0) {
            // 整除：从寅顺数 X-1 位
            offset = X - 1;
        } else if (Y % 2 == 1) {
            // 奇数余：从寅顺数 X+Y-1 位
            offset = X + Y - 1;
        } else {
            // 偶数余：从寅顺数 X+Y 位（等价于从寅顺数 X+Y 再逆局数+Y）
            // 简化：从寅顺数 X + Y/2 位后，再逆 局数/2 位...
            // 使用简化公式
            offset = X + Y;
        }

        // 从寅宫(index=2)顺时针移动 offset 位
        return DiZhiEnum.YIN.moveClockwise(offset);
    }

    /**
     * 安放紫微系6星到对应宫位（逆时针排列）
     * <p>
     * 返回 Map：(星曜编码 → 所在地支)
     *
     * @param ziweiDiZhi 紫微星所在地支
     */
    public Map<String, DiZhiEnum> placeZiweiGroup(DiZhiEnum ziweiDiZhi) {
        Map<String, DiZhiEnum> result = new LinkedHashMap<>();

        // 紫微系6星从紫微位置开始，逆时针依次安放
        // 紫微 → 天机(逆1) → 太阳(逆1) → 武曲(逆1) → 天同(逆1) → 廉贞(逆2)
        // 间隔：[0, 1, 2, 3, 4, 6] 逆时针步数

        int[] steps = {0, 1, 2, 3, 4, 6};
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
