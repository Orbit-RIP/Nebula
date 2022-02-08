package rip.orbit.nebula.profile.attributes.wrapped.menu.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.profile.attributes.wrapped.IWrapped;
import rip.orbit.nebula.util.CC;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/02/2022 / 6:18 PM
 * Nebula / rip.orbit.nebula.profile.attributes.wrap.menu.button
 */

@AllArgsConstructor
public class ExampleWrapButton extends Button {

	private IWrapped exampleWrap;

	@Override
	public String getName(Player player) {
		return exampleWrap.getName();
	}

	@Override
	public List<String> getDescription(Player player) {
		return CC.translate(Arrays.asList(
				"",
				"&6Kills: &f" + exampleWrap.getKills(),
				"&6Deaths: &f" + exampleWrap.getDeaths(),
				"&6Play Time: &f" + exampleWrap.getPlayTime(),
				""
		));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.PAPER;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {

	}
}
