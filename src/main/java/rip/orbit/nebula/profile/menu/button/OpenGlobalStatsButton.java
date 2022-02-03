package rip.orbit.nebula.profile.menu.button;

import rip.orbit.nebula.profile.menu.GlobalStatsMenu;
import rip.orbit.nebula.util.CC;
import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 8:02 PM
 * Orbit Dev / rip.orbit.nebula.profile.menu.button
 */

@AllArgsConstructor
public class OpenGlobalStatsButton extends Button {

	private UUID uuid;

	@Override
	public String getName(Player var1) {
		return CC.translate("&6&lGlobal Statistics");
	}

	@Override
	public List<String> getDescription(Player var1) {
		return CC.translate(Arrays.asList(
				"&7&m-----------------",
				"&6&l┃ &fClick to view your global statistics.",
				"&7&m-----------------"
		));
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.DIAMOND;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		new GlobalStatsMenu(this.uuid).openMenu(player);
	}
}
