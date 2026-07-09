package cn.iocoder.yudao.module.ziwei.engine;

import cn.iocoder.yudao.module.ziwei.engine.calendar.BaziCalculator;
import cn.iocoder.yudao.module.ziwei.engine.model.Bazi;
import cn.iocoder.yudao.module.ziwei.engine.model.Pillar;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 八字排盘测试
 *
 * @author JTWORLD
 */
class BaziCalculatorTest {

    private final BaziCalculator calculator = new BaziCalculator();

    @Test
    @DisplayName("八字排盘 - 1990年6月15日14:30")
    void testCalculate19900615() {
        Bazi bazi = calculator.calculate(1990, 6, 15, 14, 30);

        assertNotNull(bazi);
        assertNotNull(bazi.getYearPillar());
        assertNotNull(bazi.getMonthPillar());
        assertNotNull(bazi.getDayPillar());
        assertNotNull(bazi.getHourPillar());

        // 验证年柱：1990年为庚午年
        assertEquals("庚", bazi.getYearTianGan());
        assertEquals("午", bazi.getYearDiZhi());
        assertEquals("庚午", bazi.getYearPillar().toString());

        System.out.println("八字: " + bazi);
    }

    @Test
    @DisplayName("五虎遁 - 月干计算")
    void testWuHuDun() {
        // 甲年 → 正月丙寅
        assertEquals(TianGanEnum.BING, BaziCalculator.calcMonthGanByWuHuDun(TianGanEnum.JIA, DiZhiEnum.YIN));
        // 乙年 → 正月戊寅
        assertEquals(TianGanEnum.WU, BaziCalculator.calcMonthGanByWuHuDun(TianGanEnum.YI, DiZhiEnum.YIN));
        // 丙年 → 正月庚寅
        assertEquals(TianGanEnum.GENG, BaziCalculator.calcMonthGanByWuHuDun(TianGanEnum.BING, DiZhiEnum.YIN));
    }

    @Test
    @DisplayName("五鼠遁 - 时干计算")
    void testWuShuDun() {
        // 甲日 → 子时甲子
        assertEquals(TianGanEnum.JIA, BaziCalculator.calcHourGanByWuShuDun(TianGanEnum.JIA, DiZhiEnum.ZI));
        // 乙日 → 子时丙子
        assertEquals(TianGanEnum.BING, BaziCalculator.calcHourGanByWuShuDun(TianGanEnum.YI, DiZhiEnum.ZI));
        // 丙日 → 子时戊子
        assertEquals(TianGanEnum.WU, BaziCalculator.calcHourGanByWuShuDun(TianGanEnum.BING, DiZhiEnum.ZI));
    }

    @Test
    @DisplayName("时辰转换 - 小时转地支")
    void testHourToDiZhi() {
        assertEquals(DiZhiEnum.ZI, DiZhiEnum.ofHour(0));
        assertEquals(DiZhiEnum.ZI, DiZhiEnum.ofHour(23));
        assertEquals(DiZhiEnum.CHOU, DiZhiEnum.ofHour(1));
        assertEquals(DiZhiEnum.YIN, DiZhiEnum.ofHour(3));
        assertEquals(DiZhiEnum.WU, DiZhiEnum.ofHour(12));
        assertEquals(DiZhiEnum.HAI, DiZhiEnum.ofHour(21));
    }

}
