package xyz.nkomarn.tabgroups;

import xyz.nkomarn.tabgroups.command.TabGroupsCommand;
import xyz.nkomarn.tabgroups.group.Groups;
import xyz.nkomarn.tabgroups.listener.ConnectionListener;
import xyz.nkomarn.tabgroups.tab.Tablist;
import xyz.nkomarn.tabgroups.team.Teams;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class TabGroups extends JavaPlugin {

    private Groups groups;
    private Teams teams;
    private Tablist tablist;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        groups = new Groups(this);
        teams = new Teams(this);
        tablist = new Tablist(this);

        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ConnectionListener(this), this);

        getCommand("tabgroups").setExecutor(new TabGroupsCommand(this));
        getServer().getServicesManager().register(TabGroups.class, this, this, ServicePriority.Normal);
        getServer().getOnlinePlayers().forEach(player -> teams.createTeam(player));
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> teams.invalidateTeam(player));
    }

    @NotNull
    public Groups getGroups() {
        return groups;
    }

    @NotNull
    public Teams getTeams() {
        return teams;
    }

    @NotNull
    public Tablist getTablist() {
        return tablist;
    }
}
