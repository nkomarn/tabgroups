package com.firestartermc.kerosene.util;

import com.firestartermc.kerosene.Kerosene;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.logging.Level;
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

    public static void sendComponent(@NotNull Player player, @NotNull Component component) {
        Kerosene.getKerosene().getAudience(player).sendMessage(component);
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

    /**
     * Serializes a given {@link ItemStack} into JSON data, which can be used in chat
     * {@link TextComponent}s to send a hoverable item message to a player. This method
     * outputs identical data to what is sent when an item is displayed in a vanilla
     * death message as a tooltip.
     *
     * This is especially useful when trying to share a given {@link ItemStack}'s attributes
     * in a chat message.
     *
     * @param itemStack the item to serialize into JSON
     * @return the item serialized into JSON data
     * @see <a href="https://www.spigotmc.org/threads/tut-item-tooltips-with-the-chatcomponent-api.65964/">source</a>
     * @since 4.0
     */
    @NotNull
    public static String getJsonFromItemStack(ItemStack itemStack) {
        var craftItemStackClazz = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
        var asNMSCopyMethod = ReflectionUtils.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
        var nmsItemStackClazz = ReflectionUtils.getNMSClass("ItemStack");
        var nbtTagCompoundClazz = ReflectionUtils.getNMSClass("NBTTagCompound");
        var saveNmsItemStackMethod = ReflectionUtils.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.WARNING, "failed to serialize itemstack to nms item", t);
            return "null";
        }

        return itemAsJsonObject.toString();
    }
}
