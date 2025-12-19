package me.miyeoukman.stopwatch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StopwatchCommand implements CommandExecutor {

    private final Stopwatch plugin;

    public StopwatchCommand(Stopwatch plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 권한 확인
        if (!sender.hasPermission("stopwatch.color")) {
            sender.sendMessage("§c[Stopwatch] 이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        // 인자 확인 (예: /swcolor &a&l)
        if (args.length == 0) {
            sender.sendMessage("§e[Stopwatch] 사용법: /swcolor <색상코드>");
            sender.sendMessage("§7예시: /swcolor &a&l (초록색 굵게)");
            return true;
        }

        String inputColor = args[0];

        // 1. Config에 저장
        plugin.getConfig().set("timer-color", inputColor);
        plugin.saveConfig();

        // 2. 현재 실행 중인 리스너에 즉시 반영
        if (plugin.getListener() != null) {
            plugin.getListener().updateTimerColor(inputColor);
        }

        sender.sendMessage("§a[Stopwatch] 타이머 색상이 변경되었습니다: " + ChatColor.translateAlternateColorCodes('&', inputColor) + "00:00:00");
        return true;
    }
}
