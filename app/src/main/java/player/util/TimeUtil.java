package player.util;


public class TimeUtil {
    /**
     * 毫秒时间
     * Long类型时间转换成视频时长
     */
    public static String formatIsMS(Long ms) {
        if (ms == null) {
            return null;
        }
        long hour = ms / (60 * 60 * 1000);
        long minute = (ms - hour * 60 * 60 * 1000) / (60 * 1000);
        long second = (ms - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
        return (hour == 0 ? "00" : (hour > 10 ? hour : ("0" + hour))) + ":" + (minute == 0 ? "00"
                : (minute > 10 ? minute : ("0" + minute))) + ":" + (second == 0 ? "00" : (second > 10 ? second : ("0" + second)));
    }

    /**
     * 时间为秒
     * Long类型时间转换成视频时长
     */
    public static String formatInSecond(Long seconds) {
        if (seconds == null) {
            return null;
        }
        long hour = seconds / (60 * 60);
        long minute = (seconds - hour * 60 * 60) / 60;
        long second = seconds - hour * 60 * 60 - minute * 60;
        return (hour == 0 ? "00" : (hour > 10 ? hour : ("0" + hour))) + ":" + (minute == 0 ? "00"
                : (minute > 10 ? minute : ("0" + minute))) + ":" + (second == 0 ? "00" : (second > 10 ? second : ("0" + second)));

    }
}
