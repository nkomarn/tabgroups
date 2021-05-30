package xyz.nkomarn.tabgroups.listener;

import xyz.nkomarn.tabgroups.TabGroups;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class ConnectionListener implements Listener {

    private final TabGroups tabGroups;

    public ConnectionListener(@NotNull TabGroups tabGroups) {
        this.tabGroups = tabGroups;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        tabGroups.getTablist().update();
        tabGroups.getTablist().updateName(event.getPlayer());
        tabGroups.getTeams().createTeam(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        tabGroups.getTablist().update();
        tabGroups.getGroups().invalidateCache(event.getPlayer());
    }
}
