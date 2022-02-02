package cc.fyre.neutron.timer;

import lombok.Data;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/02/2022 / 2:10 AM
 * Orbit Dev / cc.fyre.neutron.timer
 */

@Data
public class Timer {

	private final String name;
	private String display;
	private long createdAt;
	private long duration;
	private String command;

	public Timer(String name) {
		this.name = name;
	}

}
