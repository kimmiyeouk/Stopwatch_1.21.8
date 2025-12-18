package me.miyeoukman.stopwatch;

public class StopwatchAPI {

    /**
     * 스톱워치의 전체 밀리초(ms)를 가져옵니다.
     */
    public static long getTotalMillis() {
        return StopwatchListener.currentMillis;
    }

    /**
     * 현재 스톱워치가 작동 중인지 확인합니다.
     */
    public static boolean isRunning() {
        return StopwatchListener.isRunningGlobal;
    }

    /**
     * 시간(Hours) 단위만 가져옵니다.
     */
    public static long getHours() {
        return getTotalMillis() / 3600000;
    }

    /**
     * 분(Minutes) 단위만 가져옵니다. (0~59)
     */
    public static long getMinutes() {
        return (getTotalMillis() % 3600000) / 60000;
    }

    /**
     * 초(Seconds) 단위만 가져옵니다. (0~59)
     */
    public static long getSeconds() {
        return (getTotalMillis() % 60000) / 1000;
    }

    /**
     * 밀리초(Milliseconds) 단위만 가져옵니다. (0~999)
     */
    public static long getMilliseconds() {
        return getTotalMillis() % 1000;
    }

    /**
     * 전체 포맷된 문자열을 가져옵니다. (00:00:00.000)
     */
    public static String getFormattedTime() {
        return String.format("%02d:%02d:%02d.%03d", getHours(), getMinutes(), getSeconds(), getMilliseconds());
    }
}