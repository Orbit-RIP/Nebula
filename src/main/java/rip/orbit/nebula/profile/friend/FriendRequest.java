package rip.orbit.nebula.profile.friend;

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

}
