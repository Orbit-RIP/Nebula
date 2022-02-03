package rip.orbit.nebula.notifications.packet;

import cc.fyre.proton.pidgin.packet.Packet;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class NotificationUpdatePacket implements Packet {

	@Getter
	private JsonObject jsonObject;

	public NotificationUpdatePacket(int id) {
		this.jsonObject = new JsonObject();
		this.jsonObject.addProperty("id", id);
	}

	@Override
	public int id() {
		return 9239;
	}

	@Override
	public JsonObject serialize() {
		return this.jsonObject;
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

    public int notiId() {
		return this.jsonObject.get("id").getAsInt();
    }
}
