//package rip.orbit.nebula.timer.packet;
//
//import rip.orbit.nebula.timer.Timer;
//import cc.fyre.proton.pidgin.packet.Packet;
//import com.google.gson.JsonObject;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * @author LBuddyBoy (lbuddyboy.me)
// * 01/02/2022 / 2:34 AM
// * Orbit Dev / rip.orbit.nebula.timer.packet
// */
//
//@AllArgsConstructor
//@NoArgsConstructor
//public class TimerUpdatePacket implements Packet {
//
//	@Getter
//	private JsonObject jsonObject;
//
//	public TimerUpdatePacket(List<Timer> timers) {
//		this.jsonObject = new JsonObject();
//		this.jsonObject.addProperty("name", );
//	}
//
//	@Override
//	public int id() {
//		return 1;
//	}
//
//	@Override
//	public JsonObject serialize() {
//		return this.jsonObject;
//	}
//
//	@Override
//	public void deserialize(JsonObject jsonObject) {
//		this.jsonObject = jsonObject;
//	}
//
//	public UUID uuid() {
//		return UUID.fromString(this.jsonObject.get("uuid").getAsString());
//	}
//
//}
