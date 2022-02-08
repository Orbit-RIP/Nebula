package rip.orbit.nebula.profile.menu.profile.buttons;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.util.CC;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/02/2022 / 8:46 PM
 * Nebula / rip.orbit.nebula.profile.menu.profile.buttons
 */

@AllArgsConstructor
public class GlobalStatsButton extends Button {

	private Menu menu;
	private String name;
	private List<String> lore;
	private Material material;

	@Override
	public String getName(Player player) {
		return CC.translate(name);
	}

	@Override
	public List<String> getDescription(Player player) {
		return CC.translate(lore);
	}

	@Override
	public Material getMaterial(Player player) {
		return material;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		menu.openMenu(player);
	}
}
