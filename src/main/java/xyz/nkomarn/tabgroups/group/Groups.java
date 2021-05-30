package xyz.nkomarn.tabgroups.group;

import xyz.nkomarn.tabgroups.TabGroups;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Groups {

    private final TabGroups tabGroups;
    private final Map<String, Group> groups;
    private final LoadingCache<Player, Group> groupCache;

    public Groups(@NotNull TabGroups tabGroups) {
        this.tabGroups = tabGroups;
        this.groups = new ConcurrentHashMap<>();
        this.groupCache = CacheBuilder.newBuilder()
                .maximumSize(100L)
                .build(new GroupLoader(this));

        readConfig();
    }

    /**
     * Returns an unmodifiable view of all currently registered
     * groups defined in the configuration.
     *
     * @return A collection of registered groups.
     * @since 1.0
     */
    @NotNull
    public Collection<Group> getGroups() {
        return Collections.unmodifiableCollection(groups.values());
    }

    /**
     * Returns the highest weight group that a player may currently
     * be in. If the player is not in any groups, an empty optional
     * is returned instead.
     *
     * @param player The player context.
     * @return An optional view of the player's highest group.
     * @since 1.0
     */
    @NotNull
    public Optional<Group> getGroup(@NotNull Player player) {
        return Optional.ofNullable(groupCache.getUnchecked(player));
    }

    /**
     * Clears the group cache for the provided player, allowing
     * the latest group to be cached at the next group call.
     *
     * @since 1.0
     */
    public void invalidateCache(@NotNull Player player) {
        groupCache.invalidate(player);
    }

    private void readConfig() {
        var section = tabGroups.getConfig().getConfigurationSection("groups");

        if (section == null) {
            return;
        }

        var priority = new AtomicInteger(0);

        for (var key : section.getKeys(false)) {
            groups.put(key, new Group(section.getConfigurationSection(key), key, priority.getAndIncrement()));
        }
    }
}
