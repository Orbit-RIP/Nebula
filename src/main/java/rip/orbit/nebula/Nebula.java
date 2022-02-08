package rip.orbit.nebula;

import cc.fyre.proton.Proton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import rip.orbit.nebula.command.parameter.*;
import rip.orbit.nebula.database.MongoHandler;
import rip.orbit.nebula.listener.ChatListener;
import rip.orbit.nebula.notifications.NotificationHandler;
import rip.orbit.nebula.prefix.Prefix;
import rip.orbit.nebula.prefix.PrefixHandler;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.ProfileHandler;
import rip.orbit.nebula.profile.attributes.rollback.RollbackType;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.rank.RankHandler;
import rip.orbit.nebula.server.ServerHandler;
import rip.orbit.nebula.timer.TimerHandler;
import rip.orbit.nebula.util.DurationWrapper;

@Getter
public class Nebula extends JavaPlugin {

	@Getter
	private static Nebula instance;

	private Network network;

	private MongoHandler mongoHandler;

	private RankHandler rankHandler;
	private PrefixHandler prefixHandler;
	private ProfileHandler profileHandler;
	private ServerHandler serverHandler;
	private NotificationHandler notificationHandler;
	private TimerHandler timerHandler;

	private boolean isTestServer = false;

	@Override
	public void onEnable() {
		instance = this;

		this.saveDefaultConfig();

		this.network = Network.valueOf(this.getConfig().getString("network").toUpperCase());

		this.mongoHandler = new MongoHandler(this);

		this.notificationHandler = new NotificationHandler();
		this.serverHandler = new ServerHandler();
		this.timerHandler = new TimerHandler();
		this.rankHandler = new RankHandler(this);
		this.prefixHandler = new PrefixHandler(this);
		this.profileHandler = new ProfileHandler(this);

		Proton.getInstance().getCommandHandler().registerParameterType(Rank.class, new RankParameter());
		Proton.getInstance().getCommandHandler().registerParameterType(Prefix.class, new PrefixParameter());
		Proton.getInstance().getCommandHandler().registerParameterType(ChatColor.class, new ChatColorParameter());
		Proton.getInstance().getCommandHandler().registerParameterType(RollbackType.class, new RollbackParameter());
		Proton.getInstance().getCommandHandler().registerParameterType(DurationWrapper.class, new DurationWrapperParameter());

		Proton.getInstance().getCommandHandler().registerPackage(this, "rip.orbit.nebula.command");
		Proton.getInstance().getCommandHandler().registerAll(this);

		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
	}

	@Override
	public void onDisable() {
		for (Profile profile : getProfileHandler().getCache().values()) {
			profile.getServerProfile().setOnline(false);
			profile.getServerProfile().setLastServer(getNetwork().getName());
			profile.getServerProfile().setLastLogin(System.currentTimeMillis());
			profile.save();
			System.out.println("Updated to offline " + profile.getName());
		}
	}

	@AllArgsConstructor
	@Getter
	public enum Network {

		ORBIT(
				getInstance().getConfig().getString("servername"), getInstance().getConfig().getString("networkname"), ChatColor.valueOf(getInstance().getConfig().getString("mainColor")), ChatColor.valueOf(getInstance().getConfig().getString("secondColor")), ChatColor.valueOf(getInstance().getConfig().getString("alternativeColor")),
				getInstance().getConfig().getString("domain"), getInstance().getConfig().getString("store"), getInstance().getConfig().getString("discord"), getInstance().getConfig().getString("teamspeak")
		);

		private String name;
		private String networkName;
		private ChatColor mainColor;
		private ChatColor secondColor;
		private ChatColor alternativeColor;

		private String domain;
		private String storeLink;
		private String discordLink;
		private String teamSpeakLink;
	}

}
