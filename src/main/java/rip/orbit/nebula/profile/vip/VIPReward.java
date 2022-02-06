package rip.orbit.nebula.profile.vip;

import cc.fyre.proton.util.Callback;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/02/2022 / 8:08 PM
 * Nebula / rip.orbit.nebula.profile.vip
 */
public abstract class VIPReward<T> {

	public abstract String getId();
	public abstract String getDisplay();
	public abstract Callback<T> callbackAction();

}
