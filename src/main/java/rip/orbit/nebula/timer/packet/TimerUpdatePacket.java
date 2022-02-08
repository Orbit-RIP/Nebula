package rip.orbit.nebula.timer.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import rip.orbit.nebula.timer.Timer;
import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/02/2022 / 2:34 AM
 * Orbit Dev / rip.orbit.nebula.timer.packet
 */

@AllArgsConstructor
@NoArgsConstructor
public class TimerUpdatePacket implements Packet {

	@Getter
	private JsonObject jsonObject;

	public TimerUpdatePacket(List<Timer> timers) {
		this.jsonObject = new JsonObject();

		JsonArray jsonElements = new JsonArray();
		timers.forEach(timer -> jsonElements.add(timer.serialize()));

		this.jsonObject.addProperty("timers", jsonElements.toString());
	}

	@Override
	public int id() {
		return 203030;
	}

	@Override
	public JsonObject serialize() {
		return this.jsonObject;
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public List<Timer> timers() {
		List<Timer> timers = new ArrayList<>();
		for (JsonElement element : new JsonParser().parse(this.jsonObject.get("timers").getAsString()).getAsJsonArray()) {
			timers.add(Timer.deserialize(element.getAsJsonObject()));
		}
		return timers;
	}

}
