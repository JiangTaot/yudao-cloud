package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.Star;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarBrightnessEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BrightnessCalculator 单元测试（基于文墨天机 QS 亮度数据）
 *
 * @author JTWORLD
 */
public class BrightnessCalculatorTest {

    private final BrightnessCalculator calculator = new BrightnessCalculator();

    // ═══════════ 紫微 ═══════════
    @Test
    void testZiweiAtWuMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_ZIWEI, DiZhiEnum.WU)); }
    @Test
    void testZiweiAtWeiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_ZIWEI, DiZhiEnum.WEI)); }
    @Test
    void testZiweiAtChouMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_ZIWEI, DiZhiEnum.CHOU)); }
    @Test
    void testZiweiAtZiPing() { assertEquals(StarBrightnessEnum.PING, calculator.calculate(Star.CODE_ZIWEI, DiZhiEnum.ZI)); }
    @Test
    void testZiweiAtYinWang() { assertEquals(StarBrightnessEnum.WANG, calculator.calculate(Star.CODE_ZIWEI, DiZhiEnum.YIN)); }

    // ═══════════ 太阳 ═══════════
    @Test
    void testTaiyangAtMaoMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TAIYANG, DiZhiEnum.MAO)); }
    @Test
    void testTaiyangAtHaiXian() { assertEquals(StarBrightnessEnum.XIAN, calculator.calculate(Star.CODE_TAIYANG, DiZhiEnum.HAI)); }
    @Test
    void testTaiyangAtZiXian() { assertEquals(StarBrightnessEnum.XIAN, calculator.calculate(Star.CODE_TAIYANG, DiZhiEnum.ZI)); }
    @Test
    void testTaiyangAtYouPing() { assertEquals(StarBrightnessEnum.PING, calculator.calculate(Star.CODE_TAIYANG, DiZhiEnum.YOU)); }
    @Test
    void testTaiyangAtXuBu() { assertEquals(StarBrightnessEnum.BU, calculator.calculate(Star.CODE_TAIYANG, DiZhiEnum.XU)); }

    // ═══════════ 太阴 ═══════════
    @Test
    void testTaiyinAtHaiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TAIYIN, DiZhiEnum.HAI)); }
    @Test
    void testTaiyinAtZiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TAIYIN, DiZhiEnum.ZI)); }
    @Test
    void testTaiyinAtMaoXian() { assertEquals(StarBrightnessEnum.XIAN, calculator.calculate(Star.CODE_TAIYIN, DiZhiEnum.MAO)); }
    @Test
    void testTaiyinAtYouWang() { assertEquals(StarBrightnessEnum.WANG, calculator.calculate(Star.CODE_TAIYIN, DiZhiEnum.YOU)); }

    // ═══════════ 天机 ═══════════
    @Test
    void testTianjiAtZiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TIANJI, DiZhiEnum.ZI)); }
    @Test
    void testTianjiAtWuMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TIANJI, DiZhiEnum.WU)); }

    // ═══════════ 武曲 ═══════════
    @Test
    void testWuquAtChenMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_WUQU, DiZhiEnum.CHEN)); }
    @Test
    void testWuquAtChouMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_WUQU, DiZhiEnum.CHOU)); }
    @Test
    void testWuquAtYouLi() { assertEquals(StarBrightnessEnum.LI, calculator.calculate(Star.CODE_WUQU, DiZhiEnum.YOU)); }

    // ═══════════ 天同 ═══════════
    @Test
    void testTiantongAtSiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TIANTONG, DiZhiEnum.SI)); }
    @Test
    void testTiantongAtHaiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TIANTONG, DiZhiEnum.HAI)); }

    // ═══════════ 廉贞 ═══════════
    @Test
    void testLianzhenAtYinMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_LIANZHEN, DiZhiEnum.YIN)); }
    @Test
    void testLianzhenAtShenMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_LIANZHEN, DiZhiEnum.SHEN)); }

    // ═══════════ 天府 ═══════════
    @Test
    void testTianfuAtZiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TIANFU, DiZhiEnum.ZI)); }
    @Test
    void testTianfuAtChenMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TIANFU, DiZhiEnum.CHEN)); }

    // ═══════════ 贪狼 ═══════════
    @Test
    void testTanlangAtChouMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TANLANG, DiZhiEnum.CHOU)); }
    @Test
    void testTanlangAtChenMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TANLANG, DiZhiEnum.CHEN)); }

    // ═══════════ 巨门 ═══════════
    @Test
    void testJumenAtYinMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_JUMEN, DiZhiEnum.YIN)); }
    @Test
    void testJumenAtWeiBu() { assertEquals(StarBrightnessEnum.BU, calculator.calculate(Star.CODE_JUMEN, DiZhiEnum.WEI)); }

    // ═══════════ 天相 ═══════════
    @Test
    void testTianxiangAtZiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_TIANXIANG, DiZhiEnum.ZI)); }
    @Test
    void testTianxiangAtMaoXian() { assertEquals(StarBrightnessEnum.XIAN, calculator.calculate(Star.CODE_TIANXIANG, DiZhiEnum.MAO)); }

    // ═══════════ 文昌 ═══════════
    @Test
    void testWenchangAtChouMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_WENCHANG, DiZhiEnum.CHOU)); }
    @Test
    void testWenchangAtSiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_WENCHANG, DiZhiEnum.SI)); }
    @Test
    void testWenchangAtMaoLi() { assertEquals(StarBrightnessEnum.LI, calculator.calculate(Star.CODE_WENCHANG, DiZhiEnum.MAO)); }

    // ═══════════ 左辅 ═══════════
    @Test
    void testZuofuAtChouMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_ZUOFU, DiZhiEnum.CHOU)); }
    @Test
    void testZuofuAtMaoXian() { assertEquals(StarBrightnessEnum.XIAN, calculator.calculate(Star.CODE_ZUOFU, DiZhiEnum.MAO)); }

    // ═══════════ 右弼 ═══════════
    @Test
    void testYoubiAtZiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_YOUBI, DiZhiEnum.ZI)); }
    @Test
    void testYoubiAtMaoXian() { assertEquals(StarBrightnessEnum.XIAN, calculator.calculate(Star.CODE_YOUBI, DiZhiEnum.MAO)); }

    // ═══════════ 禄存 ═══════════
    @Test
    void testLucunAtZiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_LUCUN, DiZhiEnum.ZI)); }
    @Test
    void testLucunAtChouNull() { assertNull(calculator.calculate(Star.CODE_LUCUN, DiZhiEnum.CHOU)); }

    // ═══════════ 火星铃星 ═══════════
    @Test
    void testHuoxingAtYinMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_HUOXING, DiZhiEnum.YIN)); }
    @Test
    void testLingxingAtYinMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_LINGXING, DiZhiEnum.YIN)); }

    // ═══════════ 地空地劫 ═══════════
    @Test
    void testDikongAtWuMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_DIKONG, DiZhiEnum.WU)); }
    @Test
    void testDijieAtWuMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_DIJIE, DiZhiEnum.WU)); }

    // ═══════════ isBright / isDim ═══════════
    @Test
    void testIsBright() {
        assertTrue(calculator.isBright(Star.CODE_ZIWEI, DiZhiEnum.WU));
        assertFalse(calculator.isBright(Star.CODE_TAIYANG, DiZhiEnum.HAI));
    }
    @Test
    void testIsDim() {
        assertTrue(calculator.isDim(Star.CODE_TAIYANG, DiZhiEnum.HAI));
        assertTrue(calculator.isDim(Star.CODE_TAIYIN, DiZhiEnum.MAO));
        assertFalse(calculator.isDim(Star.CODE_ZIWEI, DiZhiEnum.WU));
    }

    // ═══════════ 边界情况 ═══════════
    @Test
    void testUnknownStarReturnsNull() { assertNull(calculator.calculate("unknown", DiZhiEnum.ZI)); }
    @Test
    void testNullStarReturnsNull() { assertNull(calculator.calculate(null, DiZhiEnum.ZI)); }
    @Test
    void testNullBranchReturnsNull() { assertNull(calculator.calculate(Star.CODE_ZIWEI, null)); }

    // ═══════════ 扩展杂曜 ═══════════
    @Test
    void testTianfuxAtYinWang() { assertEquals(StarBrightnessEnum.WANG, calculator.calculate(Star.CODE_TIANFUX, DiZhiEnum.YIN)); }
    @Test
    void testTianfuxAtSiWang() { assertEquals(StarBrightnessEnum.WANG, calculator.calculate(Star.CODE_TIANFUX, DiZhiEnum.SI)); }
    @Test
    void testTianfuxAtChouNull() { assertNull(calculator.calculate(Star.CODE_TIANFUX, DiZhiEnum.CHOU)); }
    @Test
    void testBazuoAtChouMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_BAZUO, DiZhiEnum.CHOU)); }
    @Test
    void testTiancaiAtZiWang() { assertEquals(StarBrightnessEnum.WANG, calculator.calculate(Star.CODE_TIANCAI, DiZhiEnum.ZI)); }
    @Test
    void testGuchenAtYinPing() { assertEquals(StarBrightnessEnum.PING, calculator.calculate(Star.CODE_GUCHEN, DiZhiEnum.YIN)); }
    @Test
    void testGuchenAtSiXian() { assertEquals(StarBrightnessEnum.XIAN, calculator.calculate(Star.CODE_GUCHEN, DiZhiEnum.SI)); }
    @Test
    void testGuchenAtZiNull() { assertNull(calculator.calculate(Star.CODE_GUCHEN, DiZhiEnum.ZI)); }
    @Test
    void testHuagaiAtChenMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_HUAGAI, DiZhiEnum.CHEN)); }
    @Test
    void testJieshenAtZiMiao() { assertEquals(StarBrightnessEnum.MIAO, calculator.calculate(Star.CODE_JIESHEN, DiZhiEnum.ZI)); }
    @Test
    void testGuasuAtZiNull() { assertNull(calculator.calculate(Star.CODE_GUASU, DiZhiEnum.ZI)); }
}
