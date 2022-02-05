package rip.orbit.nebula.profile.menu.button;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.util.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 8:02 PM
 * Orbit Dev / rip.orbit.nebula.profile.menu.button
 */

@AllArgsConstructor
public class ProfileViewButton extends Button {

	private UUID uuid;

	@Override
	public String getName(Player var1) {
		return CC.translate("&6&l" + Proton.getInstance().getUuidCache().name(this.uuid));
	}

	@Override
	public List<String> getDescription(Player var1) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.uuid, true);

		List<String> lore = new ArrayList<>();

		lore.add("&7&m-----------------");
		lore.add("&6&l┃ &fRank: &r" + profile.getActiveRank().getFancyName());
		if (!profile.getActiveGrant().isPermanent()) {
			lore.add("  &6&l┃ &fDuration&f: &6" + profile.getActiveGrant().getRemainingString());
			lore.add("&7&m-----------------");
		}
		if (profile.getServerProfile().isOnline()) {
			lore.add("&6&l┃ &fCurrent Server: &6" + profile.getServerProfile().getLastServer());
			lore.add("&6&l┃ &fOnline Since: &6" + profile.getServerProfile().getLastSeenString());
			lore.add("&6&l┃ &fFirst Login: &6" + profile.getServerProfile().getFirstLoginString());
		} else {
			lore.add("&6&l┃ &fLast Server: &6" + profile.getServerProfile().getLastServer());
			lore.add("&6&l┃ &fLast Seen: &6" + profile.getServerProfile().getLastSeenString() + " ago");
		}
		lore.add("&7&m-----------------");

		return CC.translate(lore);
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.SKULL_ITEM;
	}

	@Override
	public byte getDamageValue(Player player) {
		return 3;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		ItemStack stack = super.getButtonItem(player);
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		meta.setOwner(Proton.getInstance().getUuidCache().name(this.uuid));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		player.closeInventory();
	}
}
