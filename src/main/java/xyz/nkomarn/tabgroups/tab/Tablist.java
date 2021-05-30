package xyz.nkomarn.tabgroups.tab;

import xyz.nkomarn.tabgroups.TabGroups;
import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import de.myzelyam.api.vanish.VanishAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.tabgroups.util.MessageUtils;

import java.util.Optional;

public class Tablist implements Listener {

    private final TabGroups tabGroups;
    private final String header;
    private final String footer;

    public Tablist(@NotNull TabGroups tabGroups) {
        this.tabGroups = tabGroups;

        var config = tabGroups.getConfig().getConfigurationSection("tablist");
        this.header = MessageUtils.formatColors(config.getString("header", ""), true);
        this.footer = MessageUtils.formatColors(config.getString("footer", ""), true);

        tabGroups.getServer().getPluginManager().registerEvents(this, tabGroups);
    }

    public void updateName(@NotNull Player player) {
        tabGroups.getGroups().getGroup(player).ifPresent(group -> {
            player.playerListName(Component.text()
                    .append(group.getBadge())
                    .append(Component.text(player.getName()).color(group.getTabColor()))
                    .build());
        });
    }

    public void update() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(tabGroups, this::update0, 5L);
    }

    private void update0() {
        var players = getAdjustedPlayerCount();
        var text = header.replace("[players]", players + " " + getPlayersString(players));

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setPlayerListHeader(text);
            player.setPlayerListFooter(footer);
        });
    }

    private int getAdjustedPlayerCount() {
        try {
            return Bukkit.getOnlinePlayers().size() - VanishAPI.getInvisiblePlayers().size();
        } catch (NoClassDefFoundError exception) {
            return Bukkit.getOnlinePlayers().size();
        }
    }

    @NotNull
    private String getPlayersString(int players) {
        return players == 1 ? "player" : "players";
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerVanish(PlayerVanishStateChangeEvent event) {
        Optional.ofNullable(Bukkit.getPlayer(event.getUUID())).ifPresent(this::updateName);
        update();
    }
}
