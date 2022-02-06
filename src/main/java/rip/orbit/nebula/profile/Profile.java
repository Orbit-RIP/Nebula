package rip.orbit.nebula.profile;

import cc.fyre.proton.util.qr.TotpUtil;
import cc.fyre.proton.uuid.UUIDCache;
import com.mongodb.client.model.Filters;
import lombok.Data;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.prefix.Prefix;
import rip.orbit.nebula.profile.attributes.ProfilePermissible;
import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.profile.attributes.grant.comparator.GrantDateComparator;
import rip.orbit.nebula.profile.attributes.grant.comparator.GrantWeightComparator;
import rip.orbit.nebula.profile.attributes.note.Note;
import rip.orbit.nebula.profile.attributes.punishment.IPunishment;
import rip.orbit.nebula.profile.attributes.punishment.comparator.PunishmentDateComparator;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.attributes.rollback.Rollback;
import rip.orbit.nebula.profile.attributes.server.ServerProfile;
import rip.orbit.nebula.profile.disguise.DisguiseProfile;
import rip.orbit.nebula.profile.friend.FriendRequest;
import rip.orbit.nebula.profile.stat.GlobalStatistic;
import rip.orbit.nebula.profile.stat.StatType;
import rip.orbit.nebula.rank.Rank;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Profile {

	private final UUID uuid;

	private String name, ipAddress, authSecret;
	private ServerProfile serverProfile = new ServerProfile(true, false, System.currentTimeMillis(), System.currentTimeMillis(), Nebula.getInstance().getConfig().getString("server.name"));
	private DisguiseProfile disguiseProfile;

	private ChatColor chatColor;
	private Grant activeGrant;
	private Prefix activePrefix;

	private List<UUID> siblings, blocked, friends;
	private List<FriendRequest> friendRequests;
	private List<Note> notes;
	private List<Grant> grants;
	private List<Rollback> rollbacks;
	private List<String> permissions;
	private List<IPunishment> punishments;

	private List<GlobalStatistic> globalStatistics = new ArrayList<>();

	private ProfilePermissible permissible;

	public Profile(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;

		this.ipAddress = null;

		this.disguiseProfile = null;

		this.chatColor = ChatColor.WHITE;

		this.siblings = new ArrayList<>();

		this.activeGrant = new Grant(Nebula.getInstance().getRankHandler().getDefaultRank(), UUIDCache.CONSOLE_UUID, (long) Integer.MAX_VALUE, "Default Grant");
		this.activeGrant.getScopes().add("GLOBAL");
		this.activePrefix = null;

		this.friendRequests = new ArrayList<>();
		this.friends = new ArrayList<>();
		this.rollbacks = new ArrayList<>();
		this.blocked = new ArrayList<>();
		this.notes = new ArrayList<>();
		this.grants = new ArrayList<>();
		this.permissions = new ArrayList<>();
		this.punishments = new ArrayList<>();

		this.authSecret = null;

		this.globalStatistics.add(new GlobalStatistic(StatType.HCF));
		this.globalStatistics.add(new GlobalStatistic(StatType.PRACTICE));
		this.globalStatistics.add(new GlobalStatistic(StatType.KITS));
		this.load();
	}

	public Profile(Document document) {
		this.uuid = UUID.fromString(document.getString("uuid"));
		this.load(document);
	}

	public void load() {
		this.getHandler().load(this);
	}

	public void load(Document document) {
		this.getHandler().load(this, document);
	}

	public void save() {
		this.getHandler().save(this, false);
	}

	public void save(boolean task) {
		this.getHandler().save(this, task);
	}

	public List<Document> findAlts() {
		return this.getHandler().findAlts(this);
	}

	public List<Document> findAltsAsync() {
		return this.getHandler().findAltsAsync(this).join();
	}

	public Document findDocument() {
		return this.getHandler().getCollection().find(Filters.eq("uuid", this.uuid.toString())).first();
	}

	public ProfileHandler getHandler() {
		return Nebula.getInstance().getProfileHandler();
	}

	public Note getNote(UUID uuid) {
		return this.notes.stream().filter(note -> note.getUuid().equals(uuid)).findAny().orElse(null);
	}

	public Grant getGrant(UUID uuid) {
		return this.grants.stream().filter(grant -> grant.getUuid().equals(uuid)).findAny().orElse(null);
	}

	public List<Grant> getActiveGrants() {
		return this.grants.stream().filter(Grant::isActive).collect(Collectors.toList());
	}

	public Grant getActiveGrant(Rank rank) {
		return this.getActiveGrants().stream().filter(grant -> grant.getRank().getUuid() == rank.getUuid()).sorted(new GrantWeightComparator().thenComparing(new GrantDateComparator())).findFirst().orElse(null);
	}

	public IPunishment getPunishment(UUID uuid) {
		return this.punishments.stream().filter(iPunishment -> iPunishment.getUuid().equals(uuid)).findAny().orElse(null);
	}

	public List<RemoveAblePunishment> getActivePunishments() {
		return this.punishments.stream().filter(iPunishment -> iPunishment instanceof RemoveAblePunishment).map(RemoveAblePunishment.class::cast).filter(removeAblePunishment -> removeAblePunishment.isActive()).collect(Collectors.toList());
	}

	public RemoveAblePunishment getActivePunishment(RemoveAblePunishment.Type type) {
		return this.getActivePunishments().stream().filter(removeAblePunishment -> removeAblePunishment.getType() == type).sorted(new PunishmentDateComparator()).findFirst().orElse(null);
	}

	public String getFancyName() {
		final Player player = this.getPlayer();

		if (player != null) {
			return player.getDisplayName();
		}

		if (this.getActiveRank().getName().equals("VIP")) {
			return this.getServerProfile().getVipStatusColor().toString() + this.name;
		}

		return this.getActiveRank().getColor().toString() + (this.getActiveRank().getSecondColor() != null ? this.getActiveRank().getSecondColor() : "") + this.name;
	}

	public String getNameWithRank() {
		final Player player = this.getPlayer();

		if (player != null) {
			return player.getDisplayName();
		}

		return this.getActiveRank().getPrefix() + (this.getActiveRank().getSecondColor() != null ? this.getActiveRank().getSecondColor() : "") + this.name;
	}

	public Player getPlayer() {
		return Nebula.getInstance().getServer().getPlayer(this.uuid);
	}

	public boolean isSibling(Profile profile) {
		return this.siblings.contains(profile.getUuid()) || profile.getSiblings().contains(this.uuid);
	}

	public List<String> getEffectivePermissions() {

		final List<String> toReturn = new ArrayList<>(this.permissions);

		this.getActiveGrants().stream().map(Grant::getRank).map(Rank::getEffectivePermissions).forEach(toReturn::addAll);

		return toReturn;
	}

	public void recalculateGrants() {

		final List<Grant> grants = this.getActiveGrants().stream()
				.sorted(new GrantWeightComparator().reversed().thenComparing(new GrantDateComparator().reversed())).collect(Collectors.toList());

		for (Grant grant : grants) {
			if (grant.getScopes().contains("GLOBAL") || grant.getScopes().contains(Nebula.getInstance().getNetwork().getName())) {
				this.setActiveGrant(grant);
				break;
			}
		}

		this.refreshDisplayName(this.getPlayer());
	}

	public void setup(PlayerJoinEvent event) {

		final Player player = event.getPlayer();

		this.permissible = new ProfilePermissible(player);

		this.permissible.recalculatePermissions();

		this.refreshDisplayName(player);
	}

	public Rank getActiveRank() {
		return this.getActiveGrant().getRank();
	}

	public Grant getActiveGrant() {
		return this.disguiseProfile == null ? this.activeGrant : new Grant(this.disguiseProfile.getRank(), UUIDCache.CONSOLE_UUID, (long) Integer.MAX_VALUE, "Disguised");
	}

	public void refreshDisplayName() {
		this.refreshDisplayName(this.getPlayer());
	}

	public void refreshDisplayName(Player player) {

		if (player == null) {
			return;
		}

		String displayName;

		if (this.grants.stream().anyMatch(grant -> grant.getRank().getName().equals("VIP"))) {
			displayName = this.serverProfile.getVipStatusColor() + player.getName();
		} else if (this.activeGrant.getRank().getSecondColor() != null) {
			displayName = this.getActiveRank().getColor().toString() + this.activeGrant.getRank().getSecondColor() + player.getName();
		} else {
			displayName = this.getActiveRank().getColor() + player.getName();
		}

		if (Nebula.getInstance().getConfig().getBoolean("fancyName.displayName")) {

			if (player.getName().length() <= 14) {
				player.setDisplayName(displayName);
			}

		}

		if (Nebula.getInstance().getConfig().getBoolean("fancyName.tabListName")) {

			if (player.getName().length() <= 14) {
				player.setPlayerListName(displayName);
			}

		}

	}

	public boolean verifyCode(int code) {

		if (this.authSecret == null) {
			return false;
		}

		try {
			return TotpUtil.validateCurrentNumber(this.authSecret, code, 250);
		} catch (GeneralSecurityException ex) {
			return false;
		}

	}

	public FriendRequest getFriendRequestFromSender(UUID sender) {
		for (FriendRequest request : this.friendRequests) {
			if (request.getSender().toString().equals(sender.toString())) {
				if (!request.isExpired()) {
					return request;
				}
			}
		}
		return null;
	}

	public FriendRequest getFriendRequestFromTarget(UUID target) {
		for (FriendRequest request : this.friendRequests) {
			if (request.getTarget().toString().equals(target.toString())) {
				if (!request.isExpired()) {
					return request;
				}
			}
		}
		return null;
	}

}
