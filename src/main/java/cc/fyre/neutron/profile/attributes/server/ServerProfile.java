package cc.fyre.neutron.profile.attributes.server;

import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;

@AllArgsConstructor
@Data
public class ServerProfile {

	private boolean online;
	private Long firstLogin, lastLogin;
	private String lastServer;

	public ServerProfile(Document document) {
		this.online = document.getBoolean("online");
		this.firstLogin = document.getLong("firstLogin");
		this.lastLogin = document.getLong("lastLogin");
		this.lastServer = document.getString("lastServer");
	}

	public Document toDocument() {

		final Document toReturn = new Document();

		toReturn.put("online", this.online);
		toReturn.put("firstLogin", this.firstLogin);
		toReturn.put("lastLogin", this.lastLogin);
		toReturn.put("lastServer", this.lastServer);

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

}
