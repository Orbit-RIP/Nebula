package rip.orbit.nebula.timer;

import com.google.gson.JsonObject;
import lombok.Data;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/02/2022 / 2:10 AM
 * Orbit Dev / rip.orbit.nebula.timer
 */

@Data
public class Timer {

	private final String name;
	private String display;
	private long createdAt;
	private long duration;
	private String command;

	public Timer(String name) {
		this.name = name;
	}

	public long getTimeLeft() {
		return (this.createdAt + duration) - System.currentTimeMillis();
	}

	public JsonObject serialize() {

		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("name", this.name);
		jsonObject.addProperty("display", this.display);
		jsonObject.addProperty("createdAt", this.createdAt);
		jsonObject.addProperty("duration", this.duration);
		jsonObject.addProperty("command", this.command);

		return jsonObject;
	}

	public static Timer deserialize(JsonObject object) {

		Timer timer = new Timer(object.get("name").getAsString());

		timer.setDisplay(object.get("display").getAsString());
		timer.setCommand(object.get("command").getAsString());
		timer.setCreatedAt(object.get("createdAt").getAsLong());
		timer.setDuration(object.get("duration").getAsLong());

		return timer;
	}

}
