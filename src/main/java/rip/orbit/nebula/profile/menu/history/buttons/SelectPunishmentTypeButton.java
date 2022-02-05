package rip.orbit.nebula.profile.menu.history.buttons;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.IPunishment;
import rip.orbit.nebula.profile.attributes.punishment.IPunishmentType;
import rip.orbit.nebula.profile.attributes.punishment.comparator.PunishmentDateComparator;
import rip.orbit.nebula.profile.attributes.punishment.impl.Punishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.menu.history.HistoryMenu;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.ColorUtil;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/02/2022 / 1:34 AM
 * Orbit Dev / rip.orbit.nebula.profile.menu.button
 */

@AllArgsConstructor
public class SelectPunishmentTypeButton extends Button {

	private IPunishmentType punishmentType;
	private UUID uuid;

	@Override
	public String getName(Player var1) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid, true);
		return CC.translate(punishmentType.getDisplay() + "&7: " + getSortedPunishments(profile).size());
	}

	@Override
	public List<String> getDescription(Player var1) {
		return null;
	}

	@Override
	public byte getDamageValue(Player player) {
		return ColorUtil.COLOR_MAP.get(punishmentType.getChatColor()).getWoolData();
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.WOOL;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		new HistoryMenu(this.uuid, punishmentType).openMenu(player);
	}

	public List<IPunishment> getSortedPunishments(Profile profile) {
		return profile.getPunishments().stream().sorted(new PunishmentDateComparator().reversed()).filter(iPunishment -> (iPunishment instanceof RemoveAblePunishment ? ((RemoveAblePunishment) iPunishment).getType() == punishmentType : ((Punishment) iPunishment).getType() == punishmentType)).collect(Collectors.toList());
	}

}
