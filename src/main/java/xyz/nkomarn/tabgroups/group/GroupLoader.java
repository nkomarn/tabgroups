package xyz.nkomarn.tabgroups.group;

import com.google.common.cache.CacheLoader;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * A {@link CacheLoader} implementation specifically tailored
 * to load the highest weight group that a given player may
 * currently be in.
 *
 * @see CacheLoader
 * @since 1.0
 */
public class GroupLoader extends CacheLoader<Player, Group> {

    private final Groups groups;

    public GroupLoader(@NotNull Groups groups) {
        this.groups = groups;
    }

    @Override
    public Group load(@NotNull Player player) {
        return groups.getGroups().stream()
                .filter(group -> hasGroup(player, group))
                .min(Comparator.comparingInt(Group::getPriority))
                .orElse(null);
    }

    private boolean hasGroup(@NotNull Player player, @NotNull Group group) {
        return player.hasPermission("group." + group.getId());
    }
}
