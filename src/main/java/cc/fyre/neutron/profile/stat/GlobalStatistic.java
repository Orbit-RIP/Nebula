package cc.fyre.neutron.profile.stat;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 6:52 PM
 * Orbit Dev / cc.fyre.neutron.profile.stat
 */

@Data
public class GlobalStatistic {

	private StatType statType;
	private int kills = 0;
	private int deaths = 0;
	private int killStreak = 0;
	private int highestKillStreak = 0;
	private int seasonsPlayed = 0;
	private List<String> pastTeams = new ArrayList<>();

	public GlobalStatistic(StatType type) {
		this.statType = type;
	}

}
