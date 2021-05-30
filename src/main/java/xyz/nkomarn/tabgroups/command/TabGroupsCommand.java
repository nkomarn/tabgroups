package xyz.nkomarn.tabgroups.command;

import xyz.nkomarn.tabgroups.TabGroups;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TabGroupsCommand implements TabExecutor {

    private final TabGroups tabGroups;

    public TabGroupsCommand(TabGroups tabGroups) {
        this.tabGroups = tabGroups;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("tabgroups.admin")) {
            return true;
        }

        var groups = tabGroups.getGroups();

        for (var player : Bukkit.getOnlinePlayers()) {
            groups.invalidateCache(player);
            groups.getGroup(player).ifPresent(group -> {
                sender.sendMessage(player.getName() + ": " + group.getId());

                var text = group.getBadge()
                        .append(Component.text(player.getName()).color(group.getTabColor()));

                player.playerListName(text);
            });
        }

        sender.sendMessage("Reloaded all player groups.");
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
