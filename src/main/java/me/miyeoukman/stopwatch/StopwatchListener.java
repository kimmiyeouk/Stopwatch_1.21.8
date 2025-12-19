package me.miyeoukman.stopwatch;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

    // 모든 플레이어가 공유하는 변수 (static처럼 동작)
    private long startTime = 0;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    private boolean isUiVisible = false; // UI 표시 여부도 전역으로 관리

    // StopwatchListener.java 상단 변수부
    public static long currentMillis = 0; // 외부에서 접근 가능한 실시간 밀리초
    public static boolean isRunningGlobal = false;

    public StopwatchListener(Stopwatch plugin) {
        this.plugin = plugin;
        startTimerTask();
    }

    @EventHandler
    public void onClockClick(PlayerInteractEvent event) {
        // [중요] 이벤트가 두 번(왼손, 오른손) 발생하는 것을 방지합니다.
        // 오직 주 손(Main Hand)의 동작만 처리합니다.
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (event.getItem() == null || event.getItem().getType() != Material.CLOCK) return;

        Action action = event.getAction();
        String tag = plugin.getConfig().getString("target-tag", "stopwatch_user");

        // 태그를 가진 사람만 스톱워치를 조작할 수 있게 제한 (선택 사항)
        if (!player.getScoreboardTags().contains(tag)) return;

        // 스톱워치 조작 시, 시계 아이템의 기본 사용 동작(예: 팔 휘두르기 외의 상호작용)을 막습니다.
        event.setCancelled(true);

        // 1. 우클릭: 시작 / 중단
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (!isRunning) {
                startTime = System.currentTimeMillis() - elapsedTime;
                isRunning = true;
                broadcastToTaggedPlayers("§a[!] 스톱워치 시작 (조작: " + player.getName() + ")");
            } else {
                elapsedTime = System.currentTimeMillis() - startTime;
                isRunning = false;
                broadcastToTaggedPlayers("§c[!] 스톱워치 중단 (조작: " + player.getName() + ")");
            }
        }

        // 2. 좌클릭: UI 토글 또는 리셋
        else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            // [수정 4] 스톱워치가 동작 중일 때는 좌클릭 무시
            if (isRunning) return;

            // [수정 3] UI 토글과 리셋이 겹치지 않게 로직 분리
            // 시간이 0인 상태(리셋된 상태)면 UI 토글만 수행
            if (elapsedTime == 0) {
                isUiVisible = !isUiVisible;
                String status = isUiVisible ? "§a표시" : "§c숨김";
                broadcastToTaggedPlayers("§e[!] 스톱워치 UI가 " + status + "§e 되었습니다.");
            }
            // 시간이 0이 아닌 상태(멈춰있는 상태)면 리셋 수행
            else {
                elapsedTime = 0;
                broadcastToTaggedPlayers("§b[!] 스톱워치가 리셋되었습니다.");
            }
        }
    }

    private void startTimerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 실시간 시간 계산 결과 업데이트
                currentMillis = isRunning ? System.currentTimeMillis() - startTime : elapsedTime;
                isRunningGlobal = isRunning;

                // [수정 2] UI가 켜져 있을 때만 시간 계산 및 표시
                if (!isUiVisible) return;

                long currentDisplayTime = isRunning ? System.currentTimeMillis() - startTime : elapsedTime;
                String formattedTime = formatTime(currentDisplayTime);
                String tag = plugin.getConfig().getString("target-tag", "stopwatch_user");

                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    // 태그를 가진 모든 플레이어에게 동일한 시간 표시
                    if (p.getScoreboardTags().contains(tag)) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f§l" + formattedTime));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // 모든 태그 소지자에게 메시지 전송
    private void broadcastToTaggedPlayers(String message) {
        String tag = plugin.getConfig().getString("target-tag", "stopwatch_user");
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.getScoreboardTags().contains(tag)) {
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