package xyz.nkomarn.tabgroups.group;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a player permission group. Each group has
 * specific attributes, including visual attributes,
 * that distinguish players in a group from other groups.
 *
 * @since 1.0
 */
public class Group {

    private final String id;
    private final String name;
    private final TextColor badgeColor;
    private final TextColor tabColor;
    private final int priority;
    private final Component badge;

    /**
     * Creates a new group based on configured parameters.
     *
     * @param section  The group's configuration section.
     * @param priority The order priority of this group.
     */
    public Group(@NotNull ConfigurationSection section, @NotNull String id, int priority) {
        this.id = id;
        this.name = section.getString("name");
        this.badgeColor = parseColor(section.getString("color.badge", "#ffffff"));
        this.tabColor = parseColor(section.getString("color.tab", "#ffffff"));
        this.badge = createBadge(section);
        this.priority = priority;
    }

    /**
     * Returns the internal identifier for this group. This
     * identifier matches precisely with the permission
     * name for this group and should only be used for
     * such permission checks.
     *
     * @return The string identifier.
     * @since 1.0
     */
    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public TextColor getBadgeColor() {
        return badgeColor;
    }

    @NotNull
    public TextColor getTabColor() {
        return tabColor;
    }

    @NotNull
    public Component getBadge() {
        return badge;
    }

    @NotNull
    private Component createBadge(@NotNull ConfigurationSection section) {
        if (!section.contains("badge")) {
            return Component.empty();
        }

        return Component.text(section.getString("badge", ""))
                .color(getBadgeColor());
    }

    /**
     * Returns the sorting priority of this group. Higher
     * priority groups will return a lower number for their
     * priority, down to zero for the highest priority group
     * in the configuration.
     *
     * @return The priority number.
     * @since 1.0
     */
    public int getPriority() {
        return priority;
    }

    @NotNull
    private TextColor parseColor(@Nullable String color) {
        if (color == null) {
            return NamedTextColor.WHITE;
        }

        return Objects.requireNonNull(TextColor.fromHexString(color));
    }
}
