package cn.iocoder.yudao.module.ziwei.engine.calendar;

import com.nlf.calendar.Solar;
import com.nlf.calendar.Lunar;

/**
 * 阴历转换器
 * <p>
 * 封装 lunar-java 库（cn.6tail:lunar），提供公历↔农历转换
 *
 * @author JTWORLD
 */
public class LunarCalendarConverter {

    /**
     * 公历转换结果
     */
    public record LunarResult(
            int lunarYear,      // 农历年
            int lunarMonth,     // 农历月（正数）
            int lunarDay,       // 农历日
            boolean isLeapMonth,// 是否闰月
            String yearTianGan, // 年天干
            String yearDiZhi    // 年地支
    ) {}

    /**
     * 公历 → 农历
     *
     * @param year   公历年
     * @param month  公历月 (1-12)
     * @param day    公历日
     * @param hour   公历时 (0-23)
     * @param minute 公历分
     */
    public LunarResult solarToLunar(int year, int month, int day, int hour, int minute) {
        Solar solar = new Solar(year, month, day, hour, minute, 0);
        Lunar lunar = solar.getLunar();
        return buildResult(lunar);
    }

    /**
     * 公历 → 农历（简化版）
     */
    public LunarResult solarToLunar(int year, int month, int day) {
        return solarToLunar(year, month, day, 12, 0);
    }

    /**
     * 通过公历日期直接获取 Lunar 对象
     */
    public Lunar getLunar(int year, int month, int day, int hour, int minute) {
        Solar solar = new Solar(year, month, day, hour, minute, 0);
        return solar.getLunar();
    }

    private LunarResult buildResult(Lunar lunar) {
        int lunarMonth = lunar.getMonth();
        boolean isLeap = lunarMonth < 0;
        return new LunarResult(
                lunar.getYear(),
                isLeap ? -lunarMonth : lunarMonth,
                lunar.getDay(),
                isLeap,
                lunar.getYearGan(),
                lunar.getYearZhi()
        );
    }

}
