package rip.orbit.nebula.profile.attributes.server;

import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.bukkit.ChatColor;

@Data
public class ServerProfile {

	private boolean online;

	public ServerProfile(boolean online, boolean claimedNameMC, Long firstLogin, Long lastLogin, String lastServer) {
		this.online = online;
		this.claimedNameMC = claimedNameMC;
		this.firstLogin = firstLogin;
		this.lastLogin = lastLogin;
		this.lastServer = lastServer;
	}

	private boolean claimedNameMC;
	private boolean vipStatus;
	private Long firstLogin, lastLogin, vipStatusDuration = 0L, vipStatusAddedAt = 0L;
	private String lastServer;
	private ChatColor vipStatusColor = ChatColor.GOLD;

	public ServerProfile(Document document) {
		this.online = document.getBoolean("online");
		this.claimedNameMC = document.getBoolean("claimedNameMC");
		this.firstLogin = document.getLong("firstLogin");
		this.lastLogin = document.getLong("lastLogin");
		this.lastServer = document.getString("lastServer");
		if (document.containsKey("vipStatus")) {
			this.vipStatus = document.getBoolean("vipStatus");
		}
		if (vipStatus) {
			this.vipStatusDuration = document.getLong("vipStatusDuration");
			this.vipStatusAddedAt = document.getLong("vipStatusAddedAt");
			this.vipStatusColor = ChatColor.valueOf(document.getString("vipStatusColor"));
		}
	}

	public Document toDocument() {

		final Document toReturn = new Document();

		toReturn.put("online", this.online);
		toReturn.put("firstLogin", this.firstLogin);
		toReturn.put("lastLogin", this.lastLogin);
		toReturn.put("lastServer", this.lastServer);
		toReturn.put("claimedNameMC", this.claimedNameMC);
		toReturn.put("vipStatus", this.vipStatus);
		toReturn.put("vipStatusAddedAt", this.vipStatusAddedAt);
		toReturn.put("vipStatusDuration", this.vipStatusDuration);
		toReturn.put("vipStatusColor", this.vipStatusColor.name());

		return toReturn;
	}

	public long getLastSeen() {
		return System.currentTimeMillis() - this.lastLogin;
	}

	public long getFirstSeen() {
		return System.currentTimeMillis() - this.firstLogin;
	}


	public String getLastSeenString() {
		return TimeUtils.formatIntoDetailedString((int) (this.getLastSeen() / 1000));
	}

	public String getFirstLoginString() {
		return TimeUtils.formatIntoDetailedString((int) ((this.getFirstSeen() / 1000)));
	}

	public boolean isVIPStatus() {
		return (this.vipStatusDuration + this.vipStatusAddedAt > System.currentTimeMillis());
	}

	public Long getRemainingVIPStatus() {
		return (this.vipStatusDuration + this.vipStatusAddedAt) - System.currentTimeMillis();
	}

	public String getVIPRemainingString() {
		return TimeUtils.formatIntoDetailedString((int) ((this.getRemainingVIPStatus() / 1000)));
	}

}
