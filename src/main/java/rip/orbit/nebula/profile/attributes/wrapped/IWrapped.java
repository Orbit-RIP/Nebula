package rip.orbit.nebula.profile.attributes.wrapped;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import org.bukkit.Material;
import rip.orbit.nebula.util.JsonDeserializer;
import rip.orbit.nebula.util.JsonSerializer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 6:52 PM
 * Orbit Dev / rip.orbit.nebula.profile.stat
 */

@Data
public class IWrapped {

	private WrappedType wrappedType;
	private String name;
	private int kills = 0;
	private int deaths = 0;
	private int highestKillStreak = 0;
	private int uniqueLogins = 0;
	private Map<Material, Integer> usedMap;
	private Map<String, Integer> partnerItemUsedMap;
	private String playTime;

	public IWrapped(String name, WrappedType type) {
		this.wrappedType = type;
		this.name = name;

		this.usedMap = new ConcurrentHashMap<>();
		this.partnerItemUsedMap = new ConcurrentHashMap<>();
	}

	public JsonObject serialize() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("type", this.wrappedType.name());
		jsonObject.addProperty("name", this.name);
		jsonObject.addProperty("kills", this.kills);
		jsonObject.addProperty("deaths", this.deaths);
		jsonObject.addProperty("highestKillStreak", this.highestKillStreak);
		jsonObject.addProperty("uniqueLogins", this.uniqueLogins);
		jsonObject.addProperty("playTime", this.playTime);

		for (Map.Entry<Material, Integer> entry : usedMap.entrySet()) {
			jsonObject.addProperty("used-" + entry.getKey().name(), entry.getValue());
		}

		for (Map.Entry<String, Integer> entry : partnerItemUsedMap.entrySet()) {
			jsonObject.addProperty("items-" + entry.getKey(), entry.getValue());
		}

		return jsonObject;
	}

	public static IWrapped deserialize(JsonObject jsonObject) {

		IWrapped iWrapped = new IWrapped(jsonObject.get("name").getAsString(), WrappedType.valueOf(jsonObject.get("type").getAsString()));

		iWrapped.setKills(jsonObject.get("kills").getAsInt());
		iWrapped.setDeaths(jsonObject.get("deaths").getAsInt());
		iWrapped.setHighestKillStreak(jsonObject.get("highestKillStreak").getAsInt());
		iWrapped.setUniqueLogins(jsonObject.get("uniqueLogins").getAsInt());
		iWrapped.setPlayTime(jsonObject.get("playTime").getAsString());

		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getKey().contains("used-")) {
				String mat = entry.getKey().replace("used-", "");
				iWrapped.getUsedMap().put(Material.valueOf(mat), entry.getValue().getAsInt());
			}
		}

		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (entry.getKey().contains("items-")) {
				String mat = entry.getKey().replace("items-", "");
				iWrapped.getPartnerItemUsedMap().put(mat, entry.getValue().getAsInt());
			}
		}

		return iWrapped;
	}

	public String mostUsedPartnerItem() {
		List<Map.Entry<String, Integer>> items = this.partnerItemUsedMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());

		if (items.size() == 0) {
			return "None";
		}

		return items.get(0).getKey();
	}

	public String mostUsedItem() {
		List<Map.Entry<Material, Integer>> items = this.usedMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());

		if (items.size() == 0) {
			return "None";
		}

		return items.get(0).getKey().name();
	}

}
