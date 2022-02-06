package rip.orbit.nebula.profile.vip.menu.buttons;

import cc.fyre.proton.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.server.ServerProfile;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.ColorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/02/2022 / 8:20 PM
 * Nebula / rip.orbit.nebula.profile.vip.menu.buttons
 */
public class VIPColorButton extends Button {

	private ChatColor current;

	@Override
	public String getName(Player player) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
		return CC.translate(profile.getServerProfile().getVipStatusColor() + "&lVIP COLOR");
	}

	@Override
	public List<String> getDescription(Player player) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		current = profile.getServerProfile().getVipStatusColor();

		List<String> lore = new ArrayList<>();

		lore.add(" ");
		lore.add(" &fCurrently Selected: " + current + ColorUtil.COLOR_NAME_MAP.get(current));
		lore.add(" ");

		for (ChatColor color : ChatColor.values()) {
			if (color == ChatColor.STRIKETHROUGH) continue;
			if (color == ChatColor.UNDERLINE) continue;
			if (color == ChatColor.BOLD) continue;
			if (color == ChatColor.RESET) continue;
			if (color == ChatColor.MAGIC) continue;
			if (color == ChatColor.ITALIC) continue;

			if (profile.getServerProfile().getVipStatusColor() == color) {
				lore.add(CC.translate(" &f\u25b6 " + color + ColorUtil.COLOR_NAME_MAP.get(color)));
				continue;
			}
			lore.add(CC.translate(" " + color + ColorUtil.COLOR_NAME_MAP.get(color)));
		}

		lore.add(" ");
		lore.add(" &fClick to cycle thru your preferred color.");
		lore.add(" ");

		return CC.translate(lore);
	}

	@Override
	public byte getDamageValue(Player player) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		current = profile.getServerProfile().getVipStatusColor();
		return ColorUtil.COLOR_MAP.get(this.current).getDyeData();
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.INK_SACK;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		if (!profile.getServerProfile().isVIPStatus()) return;

		profile.getServerProfile().setVipStatusColor(next(profile.getServerProfile().getVipStatusColor()));
		profile.refreshDisplayName();
	}

	public ChatColor next(ChatColor currentColor) {

		int i = 0;
		int current = 0;

		List<ChatColor> colors = new ArrayList<>();
		for (ChatColor color : ChatColor.values()) {
			if (color == ChatColor.STRIKETHROUGH) continue;
			if (color == ChatColor.UNDERLINE) continue;
			if (color == ChatColor.BOLD) continue;
			if (color == ChatColor.RESET) continue;
			if (color == ChatColor.MAGIC) continue;
			if (color == ChatColor.ITALIC) continue;

			if (currentColor == color) {
				current = i;
			}
			++i;

			colors.add(color);
		}
		try {
			return colors.get(++current);
		} catch (Exception ignored) {
			return colors.get(0);
		}
	}

}
