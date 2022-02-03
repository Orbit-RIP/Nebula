package rip.orbit.nebula.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 4:26 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation
 */

@Getter
@AllArgsConstructor
public class Notification {

	private final int id;
	private final String title;
	private final String message;
	private final long sentAt;
	private final List<UUID> markedReadPlayers;
	@Setter private boolean deleted;

	public String getSentAtDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getTimeZone("EST"));
		return sdf.format(new Date(sentAt));
	}

	public String getTitle() {
		try {
			return this.title;
		} catch (Exception ignored) {
			return "";
		}
	}

	public String getMessage() {
		try {
			return this.message;
		} catch (Exception ignored) {
			return "";
		}
	}

}
