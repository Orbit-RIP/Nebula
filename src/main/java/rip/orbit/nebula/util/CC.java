package rip.orbit.nebula.util;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class CC {

	public static final String BLUE = ChatColor.BLUE.toString();
	public static final String AQUA = ChatColor.AQUA.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GOLD = ChatColor.GOLD.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String BLACK = ChatColor.BLACK.toString();
	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String ITALIC = ChatColor.ITALIC.toString();
	public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
	public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
	public static final String RESET = ChatColor.RESET.toString();
	public static final String MAGIC = ChatColor.MAGIC.toString();
	public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public static final String DARK_RED = ChatColor.DARK_RED.toString();
	public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
	public static final String UNICODE_VERTICAL_BAR = CC.GRAY + StringEscapeUtils.unescapeJava("\u2503");
	public static final String UNICODE_CAUTION = StringEscapeUtils.unescapeJava("\u26a0");
	public static final String UNICODE_ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
	public static final String UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("\u25B6");
	public static final String UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
	public static final String UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");
	public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");
	public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
	public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
	public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "----------------------";

	public static String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static List<String> translate(String... format) {
		return translate(Arrays.asList(format));
	}

	public static String translate(String string, Object... format) {
		return String.format(translate(string), format);
	}

	public static List<String> translate(List<String> lore) {
		ArrayList<String> toAdd = new ArrayList();

		for (String lor : lore) {
			toAdd.add(translate(lor));
		}

		return toAdd;
	}

	public static ItemStack getCustomHead(String name, int amount, String url, String... lore) {
		ItemStack stack = getCustomHead(name, amount, url);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(translate(Arrays.asList(lore)));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack getCustomHead(String name, int amount, String url) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);

		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

		try {
			if (url.length() < 16) {
				skullMeta.setOwner(url);
			} else {
				GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
				byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", "https://textures.minecraft.net/texture/" + url).getBytes());
				gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

				try {
					Field field = skullMeta.getClass().getDeclaredField("profile");
					field.setAccessible(true);
					field.set(skullMeta, gameProfile);
				} catch (Exception ignored) {

				}
			}
			skullMeta.setDisplayName(translate(name));
			skull.setItemMeta(skullMeta);
		} catch (Exception ignored) {

		}

		return skull;
	}

}
