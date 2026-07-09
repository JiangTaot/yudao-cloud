package cn.iocoder.yudao.module.ziwei.engine.calendar;

import cn.iocoder.yudao.module.ziwei.engine.model.Bazi;
import cn.iocoder.yudao.module.ziwei.engine.model.Pillar;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;
import com.nlf.calendar.Solar;
import com.nlf.calendar.Lunar;

/**
 * 八字排盘计算器
 * <p>
 * 计算年柱、月柱、日柱、时柱
 *
 * @author JTWORLD
 */
public class BaziCalculator {

    /**
     * 计算八字
     *
     * @param year   公历年
     * @param month  公历月 (1-12)
     * @param day    公历日
     * @param hour   公历时 (0-23)
     * @param minute 公历分
     * @return Bazi 对象（四柱）
     */
    public Bazi calculate(int year, int month, int day, int hour, int minute) {
        Solar solar = new Solar(year, month, day, hour, minute, 0);
        Lunar lunar = solar.getLunar();

        Pillar yearPillar = calcYearPillar(year, month, day, lunar);
        Pillar monthPillar = calcMonthPillar(year, month, day, lunar);
        Pillar dayPillar = calcDayPillar(year, month, day, lunar);
        Pillar hourPillar = calcHourPillar(hour, dayPillar);

        return Bazi.builder()
                .yearPillar(yearPillar)
                .monthPillar(monthPillar)
                .dayPillar(dayPillar)
                .hourPillar(hourPillar)
                .build();
    }

    /**
     * 计算年柱
     * <p>
     * 以立春为界：立春前用上年干支，立春后用当年干支
     */
    private Pillar calcYearPillar(int year, int month, int day, Lunar lunar) {
        // lunar-java 的 getYearGan/getYearZhi 已考虑了立春界限
        String gan = lunar.getYearGan();
        String zhi = lunar.getYearZhi();
        return new Pillar(TianGanEnum.ofName(gan), DiZhiEnum.ofName(zhi));
    }

    /**
     * 计算月柱
     * <p>
     * 以节气为分界（寅月=立春, 卯月=惊蛰, ...）
     * 月干用五虎遁：根据年干确定正月（寅月）的月干
     */
    private Pillar calcMonthPillar(int year, int month, int day, Lunar lunar) {
        // 月支：lunar-java 的 getMonthZhi() 返回月地支（已按节气分界）
        String monthZhi = lunar.getMonthZhi();
        DiZhiEnum diZhi = DiZhiEnum.ofName(monthZhi);

        // 月干：使用五虎遁法，根据年干确定
        String yearGan = lunar.getYearGan();
        TianGanEnum yearTianGan = TianGanEnum.ofName(yearGan);

        // 五虎遁：甲己之年丙作首，乙庚之岁戊为头...
        TianGanEnum monthGan = calcMonthGanByWuHuDun(yearTianGan, diZhi);

        return new Pillar(monthGan, diZhi);
    }

    /**
     * 计算日柱
     * <p>
     * 基于儒略日序，以 1900-01-01 = 甲戌日 为参考点
     * 前后偏移天数 ÷ 60，取余数查表
     */
    private Pillar calcDayPillar(int year, int month, int day, Lunar lunar) {
        // lunar-java 直接提供了日柱干支
        String dayGan = lunar.getDayGan();
        String dayZhi = lunar.getDayZhi();
        return new Pillar(TianGanEnum.ofName(dayGan), DiZhiEnum.ofName(dayZhi));
    }

    /**
     * 计算时柱
     * <p>
     * 时支：固定按小时对应（23:00-01:00=子时, 01:00-03:00=丑时, ...）
     * 时干：用五鼠遁法，根据日干确定子时的时干
     */
    private Pillar calcHourPillar(int solarHour, Pillar dayPillar) {
        // 时支：按小时对应
        DiZhiEnum hourZhi = DiZhiEnum.ofHour(solarHour);

        // 时干：用五鼠遁法，根据日干确定子时（第一个时辰）的天干
        TianGanEnum dayGan = dayPillar.getTianGan();
        TianGanEnum hourGan = calcHourGanByWuShuDun(dayGan, hourZhi);

        return new Pillar(hourGan, hourZhi);
    }

    // ========== 五虎遁（月干计算） ==========

    /**
     * 五虎遁法计算月干
     * <p>
     * 甲己之年丙作首 → 甲年或己年，正月（寅月）月干为丙
     * 乙庚之岁戊为头 → 乙年或庚年，正月月干为戊
     * 丙辛之岁寻庚上 → 丙年或辛年，正月月干为庚
     * 丁壬壬寅顺水流 → 丁年或壬年，正月月干为壬
     * 若问戊癸何处起，甲寅之上好追求 → 戊年或癸年，正月月干为甲
     */
    public static TianGanEnum calcMonthGanByWuHuDun(TianGanEnum yearGan, DiZhiEnum monthZhi) {
        if (yearGan == null || monthZhi == null) return TianGanEnum.JIA;

        // 正月（寅月）的月干起始
        TianGanEnum firstMonthGan = switch (yearGan) {
            case JIA, JI -> TianGanEnum.BING;   // 甲己 → 丙
            case YI, GENG -> TianGanEnum.WU;     // 乙庚 → 戊
            case BING, XIN -> TianGanEnum.GENG;  // 丙辛 → 庚
            case DING, REN -> TianGanEnum.REN;   // 丁壬 → 壬
            case WU, GUI -> TianGanEnum.JIA;     // 戊癸 → 甲
        };

        // 从寅月（index=0）到当前月支，天干递增
        // 寅=0, 卯=1, ..., 丑=11
        int offset = (monthZhi.getIndex() - DiZhiEnum.YIN.getIndex() + 12) % 12;
        return firstMonthGan.ofIndex((firstMonthGan.getIndex() + offset) % 10);
    }

    // ========== 五鼠遁（时干计算） ==========

    /**
     * 五鼠遁法计算时干
     * <p>
     * 甲己还加甲 → 甲日或己日，子时的时干为甲
     * 乙庚丙作初 → 乙日或庚日，子时的时干为丙
     * 丙辛从戊起 → 丙日或辛日，子时的时干为戊
     * 丁壬庚子居 → 丁日或壬日，子时的时干为庚
     * 戊癸何方发，壬子是真途 → 戊日或癸日，子时的时干为壬
     */
    public static TianGanEnum calcHourGanByWuShuDun(TianGanEnum dayGan, DiZhiEnum hourZhi) {
        if (dayGan == null || hourZhi == null) return TianGanEnum.JIA;

        // 子时的时干起始
        TianGanEnum ziShiGan = switch (dayGan) {
            case JIA, JI -> TianGanEnum.JIA;     // 甲己 → 甲
            case YI, GENG -> TianGanEnum.BING;    // 乙庚 → 丙
            case BING, XIN -> TianGanEnum.WU;     // 丙辛 → 戊
            case DING, REN -> TianGanEnum.GENG;   // 丁壬 → 庚
            case WU, GUI -> TianGanEnum.REN;      // 戊癸 → 壬
        };

        // 从子时到当前时支，天干递增
        // 地支 index: 子=0, 丑=1, ..., 亥=11
        int offset = hourZhi.getIndex();
        return ziShiGan.ofIndex((ziShiGan.getIndex() + offset) % 10);
    }

}
