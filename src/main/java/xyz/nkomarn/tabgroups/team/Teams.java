package xyz.nkomarn.tabgroups.team;

import xyz.nkomarn.tabgroups.TabGroups;
import xyz.nkomarn.tabgroups.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.tabgroups.util.ConcurrentUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Teams {

    private final TabGroups tabGroups;
    private final Map<Player, Team> teams;
    private final Scoreboard scoreboard;

    public Teams(@NotNull TabGroups tabGroups) {
        this.tabGroups = tabGroups;
        this.teams = new ConcurrentHashMap<>();
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public void createTeam(@NotNull Player player) {
        ConcurrentUtils.ensureMain(() -> createTeam0(player));
    }

    private void createTeam0(@NotNull Player player) {
        var priority = tabGroups.getGroups().getGroup(player)
                .map(Group::getPriority)
                .orElse(99);

        var name = priority + trimName(player.getName());
        var team = scoreboard.getTeam(name);

        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        team.addEntry(player.getName());
        teams.put(player, team);
    }

    @NotNull
    public Optional<Team> getTeam(@NotNull Player player) {
        return Optional.ofNullable(teams.get(player));
    }

    public void invalidateTeam(@NotNull Player player) {
        Optional.ofNullable(teams.remove(player)).ifPresent(Team::unregister);
    }

    @NotNull
    private String trimName(@NotNull String name) {
        return name.substring(0, Math.min(14, name.length()));
    }
}
