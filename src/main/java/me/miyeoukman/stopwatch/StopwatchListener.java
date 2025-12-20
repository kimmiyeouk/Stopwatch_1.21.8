package me.miyeoukman.stopwatch;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;



public class StopwatchListener implements Listener {

    private final Stopwatch plugin;

    // 모든 플레이어가 공유하는 변수
    private long startTime = 0;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    private boolean isUiVisible = false;
    private String timerColor;
    private final String targetTag; // 태그 이름 캐싱

    // StopwatchListener.java 상단 변수부
    public static volatile long currentMillis = 0; // volatile 추가로 스레드 안전성 확보
    public static volatile boolean isRunningGlobal = false;

    public StopwatchListener(Stopwatch plugin) {
        this.plugin = plugin;
        // 설정값 캐싱
        this.targetTag = plugin.getConfig().getString("target-tag", "stopwatch");
        String colorCode = plugin.getConfig().getString("timer-color", "&f&l");
        this.timerColor = ChatColor.translateAlternateColorCodes('&', colorCode);
        startTimerTask();
    }

    // 색상 변경 메서드 (명령어에서 호출)
    public void updateTimerColor(String newColorCode) {
        this.timerColor = ChatColor.translateAlternateColorCodes('&', newColorCode);
    }

    @EventHandler
    public void onClockClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (event.getItem() == null || event.getItem().getType() != Material.CLOCK) return;

        // 캐싱된 태그 사용
        if (!player.getScoreboardTags().contains(targetTag)) return;

        event.setCancelled(true);
        Action action = event.getAction();

        // 1. 우클릭: 시작 / 중단
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (!isRunning) {
                // [수정] System.nanoTime() 사용 (단조 시계)
                startTime = (System.nanoTime() / 1_000_000) - elapsedTime;
                isRunning = true;
                broadcastToTaggedPlayers("§a[!] 스톱워치 시작 (조작: " + player.getName() + ")");
            } else {
                elapsedTime = (System.nanoTime() / 1_000_000) - startTime;
                isRunning = false;
                broadcastToTaggedPlayers("§c[!] 스톱워치 중단 (조작: " + player.getName() + ")");
            }
        }

        // 2. 좌클릭: UI 토글 또는 리셋
        else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (isRunning) return;

            if (elapsedTime == 0) {
                isUiVisible = !isUiVisible;
                String status = isUiVisible ? "§a표시" : "§c숨김";
                broadcastToTaggedPlayers("§e[!] 스톱워치 UI가 " + status + "§e 되었습니다.");
            } else {
                elapsedTime = 0;
                broadcastToTaggedPlayers("§b[!] 스톱워치가 리셋되었습니다.");
            }
        }
    }

    private void startTimerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 실시간 시간 계산 (한 번만 수행)
                // [수정] System.nanoTime() 사용
                currentMillis = isRunning ? (System.nanoTime() / 1_000_000) - startTime : elapsedTime;
                isRunningGlobal = isRunning;

                if (!isUiVisible) return;

                String formattedTime = formatTime(currentMillis);

                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getScoreboardTags().contains(targetTag)) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(timerColor + formattedTime));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // 모든 태그 소지자에게 메시지 전송
    private void broadcastToTaggedPlayers(String message) {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.getScoreboardTags().contains(targetTag)) {
                p.sendMessage(message);
            }
        }
    }

    private String formatTime(long millis) {
        long hours = millis / 3600000;
        long mins = (millis % 3600000) / 60000;
        long secs = (millis % 60000) / 1000;
        long ms = millis % 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, mins, secs, ms);
    }
}