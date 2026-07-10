package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.StarPosition;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarBrightnessEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 天府系8星安放
 * <p>
 * 天府星 = 紫微星的镜像对宫（寅←→申, 卯←→酉, ...）
 * 确定天府星后，顺时针安放其余7星：太阴、贪狼、巨门、天相、天梁、七杀、破军
 *
 * @author JTWORLD
 */
public class TianfuStarPlacer {

    // 天府系8星顺序（顺时针排列）
    private static final String[] TIANFU_GROUP = {"tianfu", "taiyin", "tanlang", "jumen", "tianxiang", "tianliang", "qisha", "pojun"};

    private static final String[] TIANFU_NAMES = {"天府", "太阴", "贪狼", "巨门", "天相", "天梁", "七杀", "破军"};

    /**
     * 定位天府星所在地支（紫微星的轴对称位置）
     * <p>
     * iztro 公式：tianfuIndex = fixIndex(12 - ziweiIztroIndex)
     * 转为绝对地支索引：tianfuAbs = (16 - ziweiAbs) % 12
     * <p>
     * 注意：天府星不是紫微星的对宫（180°），而是关于寅轴的镜像对称。
     *
     * @param ziweiDiZhi 紫微星所在地支
     * @return 天府星所在地支
     */
    public DiZhiEnum findTianfuPosition(DiZhiEnum ziweiDiZhi) {
        // iztro 公式：tianfu = fixIndex(12 - (ziweiAbs - 2))
        //         = (16 - ziweiAbs) % 12
        int ziweiAbs = ziweiDiZhi.getIndex();
        int tianfuAbs = (16 - ziweiAbs) % 12;
        if (tianfuAbs < 0) tianfuAbs += 12;
        return DiZhiEnum.ofIndex(tianfuAbs);
    }

    /**
     * 安放天府系8星到对应宫位（顺时针排列）
     * <p>
     * 天府 → 太阴(顺1) → 贪狼(顺2) → 巨门(顺3) → ...
     * 具体步数：天府0步, 太阴1步, 贪狼2步, 巨门3步, 天相4步, 天梁5步, 七杀6步, 破军10步
     *
     * @param tianfuDiZhi 天府星所在地支
     */
    public Map<String, DiZhiEnum> placeTianfuGroup(DiZhiEnum tianfuDiZhi) {
        Map<String, DiZhiEnum> result = new LinkedHashMap<>();

        // 步数：[天府=0, 太阴=1, 贪狼=2, 巨门=3, 天相=4, 天梁=5, 七杀=6, 破军=10]
        int[] steps = {0, 1, 2, 3, 4, 5, 6, 10};

        for (int i = 0; i < TIANFU_GROUP.length; i++) {
            DiZhiEnum pos = tianfuDiZhi.moveClockwise(steps[i]);
            result.put(TIANFU_GROUP[i], pos);
        }

        return result;
    }

    /**
     * 获取天府系星曜名称
     */
    public static String getStarName(int index) {
        return TIANFU_NAMES[index];
    }

    /**
     * 创建星曜落位对象
     */
    public StarPosition createStarPosition(String starCode, StarBrightnessEnum brightness) {
        String starName = switch (starCode) {
            case "tianfu" -> "天府"; case "taiyin" -> "太阴"; case "tanlang" -> "贪狼";
            case "jumen" -> "巨门"; case "tianxiang" -> "天相"; case "tianliang" -> "天梁";
            case "qisha" -> "七杀"; case "pojun" -> "破军";
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
