package rip.orbit.nebula.profile.attributes.wrapped.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.wrapped.IWrapped;
import rip.orbit.nebula.profile.attributes.wrapped.menu.button.ExampleWrapButton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/02/2022 / 6:17 PM
 * Nebula / rip.orbit.nebula.profile.attributes.wrap.menu
 */

@AllArgsConstructor
public class ExampleWrapMenu extends Menu {

	private UUID target;

	@Override
	public String getTitle(Player player) {
		return "Example Wraps";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.target, true);

		int i = -1;
		for (IWrapped wrap : profile.getWraps()) {
			buttons.put(++i, new ExampleWrapButton(wrap));
		}

		return buttons;
	}

}
