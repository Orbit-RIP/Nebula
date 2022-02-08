package rip.orbit.nebula.timer.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import com.mongodb.client.model.Filters;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.timer.Timer;
import rip.orbit.nebula.timer.TimerHandler;
import rip.orbit.nebula.timer.packet.TimerUpdatePacket;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.DurationWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/02/2022 / 7:37 PM
 * Nebula / rip.orbit.nebula.timer.command
 */
public class GlobalTimerCommand {

	@Command(names = {"globaltimer create"}, permission = "op")
	public static void create(CommandSender sender, @Parameter(name = "name") String name, @Parameter(name = "duration") DurationWrapper duration, @Parameter(name = "display - for space") String display, @Parameter(name = "command", wildcard = true) String command) {


		TimerHandler timerHandler = Nebula.getInstance().getTimerHandler();

		if (timerHandler.byName(name) != null) {
			sender.sendMessage(CC.translate("&cThat timer already exists!"));
			return;
		}

		Timer timer = new Timer(name);
		timer.setCreatedAt(System.currentTimeMillis());
		timer.setDisplay(display);
		timer.setDuration(duration.getDuration());
		timer.setCommand(command);

		timerHandler.getTimers().add(timer);

		Proton.getInstance().getPidginHandler().sendPacket(new TimerUpdatePacket(timerHandler.getTimers()));

		timerHandler.save(timer);

		sender.sendMessage(CC.translate("&aTimer created"));
	}

	@Command(names = {"globaltimer delete"}, permission = "op")
	public static void delete(CommandSender sender, @Parameter(name = "name") String name) {

		TimerHandler timerHandler = Nebula.getInstance().getTimerHandler();

		Timer timer = timerHandler.byName(name);
		if (timer == null) {
			sender.sendMessage(CC.translate("&cThat timer doesn't exist!"));
			return;
		}

		timerHandler.getTimers().remove(timer);

		Proton.getInstance().getPidginHandler().sendPacket(new TimerUpdatePacket(timerHandler.getTimers()));

		timerHandler.getCollection().deleteOne(Filters.eq("name", timer.getName()));

		sender.sendMessage(CC.translate("&aTimer deleted"));
	}

	@Command(names = "globaltimers", permission = "op")
	public static void globaltimers(Player sender) {
		new PaginatedMenu() {

			@Override
			public String getPrePaginatedTitle(Player player) {
				return CC.translate("&6Global Timers");
			}

			@Override
			public Map<Integer, Button> getAllPagesButtons(Player player) {
				Map<Integer, Button> buttons = new HashMap<>();

				int i = -1;
				for (Timer timer : Nebula.getInstance().getTimerHandler().getTimers()) {
					buttons.put(++i, new Button() {
						@Override
						public String getName(Player player) {
							return CC.translate("&6" + timer.getName());
						}

						@Override
						public List<String> getDescription(Player player) {
							return CC.translate(Arrays.asList(
									"&7&m--------------",
									"&6Display: &r" + timer.getDisplay(),
									"&6Command: &f" + timer.getCommand(),
									" ",
									"&6Time Left: &f" + TimeUtils.formatIntoDetailedString((int) (timer.getTimeLeft() / 1000)),
									"&6Duration: &f" + TimeUtils.formatIntoDetailedString((int) (timer.getDuration() / 1000)),
									" ",
									"&7&oClick to delete this globaltimer.",
									"&7&m--------------"
							));
						}

						@Override
						public Material getMaterial(Player player) {
							return Material.WATCH;
						}

						@Override
						public void clicked(Player player, int slot, ClickType clickType) {
							delete(sender, timer.getName());
						}
					});
				}

				return buttons;
			}

			@Override
			public boolean isAutoUpdate() {
				return true;
			}
		}.openMenu(sender);
	}

}
