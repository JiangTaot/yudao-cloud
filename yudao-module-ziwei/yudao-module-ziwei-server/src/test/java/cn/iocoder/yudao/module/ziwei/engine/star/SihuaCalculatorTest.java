package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.enums.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SihuaCalculator 单元测试
 *
 * @author JTWORLD
 */
public class SihuaCalculatorTest {

    private final SihuaCalculator calculator = new SihuaCalculator();

    @Test
    void testJiaSihua() {
        // 甲: 廉贞化禄 破军化权 武曲化科 太阳化忌
        var sihua = calculator.calculateNatalSihua(TianGanEnum.JIA);
        assertEquals(4, sihua.size());
        assertEquals("lianzhen", sihua.get(0).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_LU, sihua.get(0).getSihuaType());
        assertEquals("pojun", sihua.get(1).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_QUAN, sihua.get(1).getSihuaType());
        assertEquals("wuqu", sihua.get(2).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_KE, sihua.get(2).getSihuaType());
        assertEquals("taiyang", sihua.get(3).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_JI, sihua.get(3).getSihuaType());
    }

    @Test
    void testYiSihua() {
        // 乙: 天机化禄 天梁化权 紫微化科 太阴化忌
        var sihua = calculator.calculateNatalSihua(TianGanEnum.YI);
        assertEquals(4, sihua.size());
        assertEquals("tianji", sihua.get(0).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_LU, sihua.get(0).getSihuaType());
        assertEquals("tianliang", sihua.get(1).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_QUAN, sihua.get(1).getSihuaType());
        assertEquals("ziwei", sihua.get(2).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_KE, sihua.get(2).getSihuaType());
        assertEquals("taiyin", sihua.get(3).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_JI, sihua.get(3).getSihuaType());
    }

    @Test
    void testBingSihua() {
        // 丙: 天同化禄 天机化权 文昌化科 廉贞化忌
        var sihua = calculator.calculateNatalSihua(TianGanEnum.BING);
        assertEquals("tiantong", sihua.get(0).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_LU, sihua.get(0).getSihuaType());
        assertEquals("tianji", sihua.get(1).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_QUAN, sihua.get(1).getSihuaType());
        assertEquals("wenchang", sihua.get(2).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_KE, sihua.get(2).getSihuaType());
        assertEquals("lianzhen", sihua.get(3).getStarCode());
        assertEquals(SihuaTypeEnum.HUA_JI, sihua.get(3).getSihuaType());
    }

    @Test
    void testAllTenStemsHaveFourSihua() {
        for (TianGanEnum gan : TianGanEnum.values()) {
            var sihua = calculator.calculateNatalSihua(gan);
            assertNotNull(sihua);
            assertEquals(4, sihua.size(), "天干" + gan.getName() + "应有4个四化");
            assertEquals(SihuaTypeEnum.HUA_LU, sihua.get(0).getSihuaType());
            assertEquals(SihuaTypeEnum.HUA_QUAN, sihua.get(1).getSihuaType());
            assertEquals(SihuaTypeEnum.HUA_KE, sihua.get(2).getSihuaType());
            assertEquals(SihuaTypeEnum.HUA_JI, sihua.get(3).getSihuaType());
        }
    }

}
