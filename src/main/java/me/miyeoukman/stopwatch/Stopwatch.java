package me.miyeoukman.stopwatch;

import org.bukkit.plugin.java.JavaPlugin;

public class Stopwatch extends JavaPlugin {

    private StopwatchListener listener;

    @Override
    public void onEnable() {
        // 1. 기본 설정 파일(config.yml) 생성
        saveDefaultConfig();

        // 리스너 인스턴스 생성 및 등록
        this.listener = new StopwatchListener(this);
        getServer().getPluginManager().registerEvents(this.listener, this);

        // 명령어 등록
        getCommand("swcolor").setExecutor(new StopwatchCommand(this));

        // 2. 로그 출력
        getLogger().info("Stopwatch 플러그인이 활성화되었습니다!");

        // 여기에 나중에 이벤트 리스너와 스케줄러를 등록할 거예요.
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopwatch 플러그인이 비활성화되었습니다.");
    }

    public StopwatchListener getListener() {
        return listener;
    }
}