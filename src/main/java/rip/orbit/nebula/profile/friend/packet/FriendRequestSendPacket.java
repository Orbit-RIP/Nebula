package rip.orbit.nebula.profile.friend.packet;

import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 10:03 PM
 * Orbit Dev / rip.orbit.nebula.profile.friend.packet
 */
public class FriendRequestSendPacket implements Packet {

	@Getter
	private JsonObject jsonObject;

	public FriendRequestSendPacket(UUID sender, UUID target) {
		this.jsonObject = new JsonObject();
		this.jsonObject.addProperty("sender", sender.toString());
		this.jsonObject.addProperty("target", target.toString());
	}

	@Override
	public int id() {
		return 293;
	}

	@Override
	public JsonObject serialize() {
		return this.jsonObject;
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public UUID sender() {
		return UUID.fromString(this.jsonObject.get("sender").getAsString());
	}

	public UUID target() {
		return UUID.fromString(this.jsonObject.get("target").getAsString());
	}

}
