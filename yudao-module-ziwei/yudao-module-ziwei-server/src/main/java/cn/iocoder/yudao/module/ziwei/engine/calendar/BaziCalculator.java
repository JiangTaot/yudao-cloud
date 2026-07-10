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
 * 使用 nlf.calendar 库的 Exact 方法，对齐文墨天机标准：
 * <ul>
 *   <li>年柱：以立春交接时刻为界（getYearGanExact / getYearZhiExact）</li>
 *   <li>月柱：以节气交接时刻为界（getMonthGanExact / getMonthZhiExact）</li>
 *   <li>日柱：晚子时(23:00-23:59)算次日（getDayGanExact / getDayZhiExact）</li>
 *   <li>时柱：自动处理早/晚子时五鼠遁（getTimeGan / getTimeZhi）</li>
 * </ul>
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

        // 年柱：立春交接时刻为界（对齐文墨天机）
        Pillar yearPillar = new Pillar(
                TianGanEnum.ofName(lunar.getYearGanExact()),
                DiZhiEnum.ofName(lunar.getYearZhiExact()));

        // 月柱：节气交接时刻为界（对齐文墨天机）
        Pillar monthPillar = new Pillar(
                TianGanEnum.ofName(lunar.getMonthGanExact()),
                DiZhiEnum.ofName(lunar.getMonthZhiExact()));

        // 日柱：晚子时(23:00-23:59)日柱算次日（对齐文墨天机）
        Pillar dayPillar = new Pillar(
                TianGanEnum.ofName(lunar.getDayGanExact()),
                DiZhiEnum.ofName(lunar.getDayZhiExact()));

        // 时柱：自动处理早/晚子时五鼠遁
        Pillar hourPillar = new Pillar(
                TianGanEnum.ofName(lunar.getTimeGan()),
                DiZhiEnum.ofName(lunar.getTimeZhi()));

        return Bazi.builder()
                .yearPillar(yearPillar)
                .monthPillar(monthPillar)
                .dayPillar(dayPillar)
                .hourPillar(hourPillar)
                .build();
    }

    // ========== 五虎遁（月干计算）—— 保留供 PalaceSetupEngine 等使用 ==========

    /**
     * 五虎遁法计算月干
     * <p>
     * 甲己之年丙作首，乙庚之岁戊为头，
     * 丙辛之岁寻庚上，丁壬壬寅顺水流，
     * 若问戊癸何处起，甲寅之上好追求。
     */
    public static TianGanEnum calcMonthGanByWuHuDun(TianGanEnum yearGan, DiZhiEnum monthZhi) {
        if (yearGan == null || monthZhi == null) return TianGanEnum.JIA;

        TianGanEnum firstMonthGan = switch (yearGan) {
            case JIA, JI -> TianGanEnum.BING;
            case YI, GENG -> TianGanEnum.WU;
            case BING, XIN -> TianGanEnum.GENG;
            case DING, REN -> TianGanEnum.REN;
            case WU, GUI -> TianGanEnum.JIA;
        };

        int offset = (monthZhi.getIndex() - DiZhiEnum.YIN.getIndex() + 12) % 12;
        return firstMonthGan.ofIndex((firstMonthGan.getIndex() + offset) % 10);
    }

    // ========== 五鼠遁（时干计算）—— 保留供其他地方使用 ==========

    /**
     * 五鼠遁法计算时干
     * <p>
     * 甲己还加甲，乙庚丙作初，丙辛从戊起，
     * 丁壬庚子居，戊癸何方发，壬子是真途。
     */
    public static TianGanEnum calcHourGanByWuShuDun(TianGanEnum dayGan, DiZhiEnum hourZhi) {
        if (dayGan == null || hourZhi == null) return TianGanEnum.JIA;

        TianGanEnum ziShiGan = switch (dayGan) {
            case JIA, JI -> TianGanEnum.JIA;
            case YI, GENG -> TianGanEnum.BING;
            case BING, XIN -> TianGanEnum.WU;
            case DING, REN -> TianGanEnum.GENG;
            case WU, GUI -> TianGanEnum.REN;
        };

        int offset = hourZhi.getIndex();
        return ziShiGan.ofIndex((ziShiGan.getIndex() + offset) % 10);
    }

}
