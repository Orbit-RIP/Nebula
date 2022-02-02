package cc.fyre.neutron.profile.attributes.grant;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.attributes.api.Executable;
import cc.fyre.neutron.profile.attributes.api.Pardonable;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.util.FormatUtil;
import cc.fyre.proton.Proton;
import lombok.Data;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Grant implements Executable, Pardonable {
	
	private final UUID uuid;
	private final UUID rankUuid;
	private final UUID executor;
	private final Long executedAt;
	private final String executedReason;

	private UUID pardoner;
	private Long pardonedAt;
	private String pardonedReason;
	private Long duration;
	private List<String> scopes = new ArrayList<>();

	public Grant(Rank rank, UUID executor, Long duration, String reason) {
		this.uuid = UUID.randomUUID();
		this.rankUuid = rank.getUuid();
		this.executor = executor;
		this.duration = duration;
		this.executedAt = System.currentTimeMillis();
		this.executedReason = reason;
	}

	public Grant(Document document) {
		this.uuid = UUID.fromString(document.getString("uuid"));
		this.rankUuid = UUID.fromString(document.getString("rankUuid"));
		this.executor = UUID.fromString(document.getString("executor"));
		this.executedAt = document.getLong("executedAt");
		this.executedReason = document.getString("executedReason");
		this.scopes = Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("scopes"), ArrayList.class);

		if (document.containsKey("pardoner") && document.containsKey("pardonedAt") && document.containsKey("pardonedReason")) {
			this.pardoner = UUID.fromString(document.getString("pardoner"));
			this.pardonedAt = document.getLong("pardonedAt");
			this.pardonedReason = document.getString("pardonedReason");
		}

		this.duration = document.getLong("duration");
	}

	public Document toDocument() {

		final Document toReturn = new Document();

		toReturn.put("uuid", this.uuid.toString());
		toReturn.put("rankUuid", this.rankUuid.toString());
		toReturn.put("executor", this.executor.toString());
		toReturn.put("executedAt", this.executedAt);
		toReturn.put("executedReason", this.executedReason);
		toReturn.put("scopes", Proton.PLAIN_GSON.toJson(this.scopes));

		if (this.isPardoned()) {
			toReturn.put("pardoner", this.pardoner.toString());
			toReturn.put("pardonedAt", this.pardonedAt);
			toReturn.put("pardonedReason", this.pardonedReason);
		}

		toReturn.put("duration", this.duration);

		return toReturn;
	}

	@Override
	public boolean isPardoned() {
		return this.pardoner != null && this.pardonedAt != null && this.pardonedReason != null;
	}

	public Rank getRank() {
		return Neutron.getInstance().getRankHandler().fromUuid(this.rankUuid);
	}

	public boolean isPermanent() {
		return this.duration == Integer.MAX_VALUE;
	}

	public boolean hasExpired() {
		return (!this.isPermanent()) && (System.currentTimeMillis() >= this.executedAt + this.duration);
	}

	public Long getExpiredAt() {
		return this.executedAt + this.duration;
	}

	public Long getRemaining() {
		return (this.executedAt + this.duration) - System.currentTimeMillis();
	}

	public boolean isActive() {

		if (this.getRemaining() < 0) {
			return false;
		}

		return !this.isPardoned();
	}

	public String getRemainingString() {

		if (this.duration == Integer.MAX_VALUE) {
			return "Never";
		}

		return ChatColor.YELLOW + FormatUtil.millisToRoundedTime((this.duration + this.executedAt) - System.currentTimeMillis(), true);
	}

}
