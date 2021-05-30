package xyz.nkomarn.tabgroups.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Miscellaneous chat message utility methods.
 * <p>
 * This class provides methods for interacting with common chat and
 * user interface elements. It also provides easy-to-use, thread-safe utilities
 * that can be safely used in the {@link BukkitScheduler} or executors.
 *
 * @author Firestarter Minecraft Servers
 * @since 4.0
 */
@ThreadSafe
public final class MessageUtils {

    private static final Pattern RGB_PATTERN = Pattern.compile("&#([A-Fa-f0-9]){6}");

    private MessageUtils() {
    }

    /**
     * Formats the {@code text} using the alternate color code character '&',
     * formatting it into a color format usable in messages.
     * <p>
     * This method is intended to make it easier to send color formatted messages
     * to players.
     *
     * @param text the text for which to translate colors
     * @return the color-translated text
     * @since 5.0
     */
    @NotNull
    public static String formatColors(@NotNull String text) {
        return formatColors(text, false);
    }

    /**
     * Formats the {@code text} using the alternate color code character '&',
     * formatting it into a color format usable in messages. This method also
     * supports RGB hex color code formatting in the format '&#FFFFFF'.
     * <p>
     * This method is intended to make it easier to send color formatted messages
     * to players.
     *
     * @param text the text for which to translate colors
     * @param rgb  whether RGB color codes should be translated
     * @return the color-translated text
     * @since 5.0
     */
    @NotNull
    public static String formatColors(@NotNull String text, boolean rgb) {
        if (rgb) {
            var matcher = RGB_PATTERN.matcher(text);
            while (matcher.find()) {
                var color = ChatColor.of(matcher.group().substring(1));
                var pre = text.substring(0, matcher.start());
                var post = text.substring(matcher.end());
                text = pre + color + post;
                matcher = RGB_PATTERN.matcher(text);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Splits the {@code text} at a given {@code lineSize}, turning it into a
     * {@link List<String>}. Each entry of the returned list is one line of the
     * split string.
     * <p>
     * This method is useful for user interfaces, where lines must be limited
     * to a given size to be readable on all screen sizes.
     *
     * @param text     the text to split into lines
     * @param lineSize the maximum character size of a single line
     * @return list of split text lines
     * @since 4.0
     */
    @NotNull
    public static List<String> splitString(@NotNull String text, int lineSize) {
        var matcher = Pattern.compile("\\b.{1," + (lineSize - 1) + "}\\b\\W?").matcher(text);
        return matcher.results()
                .map(MatchResult::group)
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
