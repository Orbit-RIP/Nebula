package rip.orbit.nebula.profile.friend;

import com.google.gson.JsonObject;
import lombok.Data;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 9:47 PM
 * Orbit Dev / rip.orbit.nebula.command.profile.friend
 */

@Data
public class FriendRequest {

	private UUID sender;
	private UUID target;
	private long sentAt;

	public boolean isExpired() {
		return (this.sentAt + System.currentTimeMillis()) - System.currentTimeMillis() < (5 * 60) * 1000;
	}

	public static FriendRequest deserialize(JsonObject jsonObject) {

		FriendRequest request = new FriendRequest();
		request.setSender(UUID.fromString(jsonObject.get("sender").getAsString()));
		request.setTarget(UUID.fromString(jsonObject.get("target").getAsString()));
		request.setSentAt(jsonObject.get("sentAt").getAsLong());

		return request;
	}

	public JsonObject serialize() {

		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("sender", this.sender.toString());
		jsonObject.addProperty("target", this.target.toString());
		jsonObject.addProperty("sentAt", this.sentAt);

		return jsonObject;
	}

}
