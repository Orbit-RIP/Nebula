package rip.orbit.nebula.timer;

import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.timer.packet.TimerUpdatePacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/02/2022 / 2:12 AM
 * Orbit Dev / rip.orbit.nebula.timer
 */

@Getter
@Setter
public class TimerHandler implements PacketListener {

	private List<Timer> timers;
	private final MongoCollection<Document> collection;

	public TimerHandler() {
		this.timers = new ArrayList<>();
		this.collection = Nebula.getInstance().getMongoHandler().getMongoDatabase().getCollection("timers");

		Proton.getInstance().getPidginHandler().registerPacket(TimerUpdatePacket.class);
		Proton.getInstance().getPidginHandler().registerListener(this);

		for (Document document : this.collection.find()) {
			if (document == null) continue;
			load(document);
		}

		Bukkit.getScheduler().runTaskTimerAsynchronously(Nebula.getInstance(), () -> {

			for (Timer timer : this.timers) {
				if (timer.getTimeLeft() <= 0) {
					Bukkit.getScheduler().runTask(Nebula.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), timer.getCommand()));
				}
			}

		}, 20, 20);

	}

	public Timer byName(String name) {
		for (Timer timer : timers) {
			if (timer.getName().equals(name)) {
				return timer;
			}
		}
		return null;
	}

	public void load(Document document) {

		Timer timer = new Timer(document.getString("name"));
		timer.setDisplay(document.getString("display"));
		timer.setCommand(document.getString("command"));
		timer.setCreatedAt(document.getLong("createdAt"));
		timer.setDuration(document.getLong("duration"));

		this.timers.add(timer);
	}

	public void save(Timer timer) {

		Document document = this.collection.find(Filters.eq("name", timer.getName())).first();

		if (document == null) {
			return;
		}

		document.put("name", timer.getName());
		document.put("display", timer.getDisplay());
		document.put("command", timer.getCommand());
		document.put("duration", timer.getDuration());
		document.put("createdAt", timer.getCreatedAt());

		this.collection.replaceOne(Filters.eq("name", timer.getName()), document, new ReplaceOptions().upsert(true));

	}

	@IncomingPacketHandler
	public void timerUpdate(TimerUpdatePacket packet) {
		Nebula.getInstance().getTimerHandler().setTimers(packet.timers());
	}

}
