package rip.orbit.nebula.profile;

import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import cc.fyre.proton.util.MojangUtil;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.uuid.UUIDCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.NebulaConstants;
import rip.orbit.nebula.prefix.Prefix;
import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.profile.attributes.grant.listener.GrantListener;
import rip.orbit.nebula.profile.attributes.grant.packet.GrantApplyPacket;
import rip.orbit.nebula.profile.attributes.grant.packet.GrantRemovePacket;
import rip.orbit.nebula.profile.attributes.note.Note;
import rip.orbit.nebula.profile.attributes.note.packet.NoteApplyPacket;
import rip.orbit.nebula.profile.attributes.punishment.IPunishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.Punishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.attributes.punishment.packet.PunishmentExecutePacket;
import rip.orbit.nebula.profile.attributes.punishment.packet.PunishmentPardonPacket;
import rip.orbit.nebula.profile.attributes.rollback.Rollback;
import rip.orbit.nebula.profile.attributes.server.ServerProfile;
import rip.orbit.nebula.profile.disguise.DisguiseProfile;
import rip.orbit.nebula.profile.event.GrantExpireEvent;
import rip.orbit.nebula.profile.friend.FriendRequest;
import rip.orbit.nebula.profile.friend.packet.FriendRemovePacket;
import rip.orbit.nebula.profile.friend.packet.FriendRequestAcceptPacket;
import rip.orbit.nebula.profile.friend.packet.FriendRequestRemovePacket;
import rip.orbit.nebula.profile.friend.packet.FriendRequestSendPacket;
import rip.orbit.nebula.profile.packet.PermissionAddPacket;
import rip.orbit.nebula.profile.packet.PermissionRemovePacket;
import rip.orbit.nebula.profile.stat.GlobalStatistic;
import rip.orbit.nebula.profile.vip.VIPReward;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.EncryptionHandler;
import rip.orbit.nebula.util.JavaUtils;
import rip.orbit.nebula.util.fanciful.FancyMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ProfileHandler implements Listener, PacketListener {

	@Getter
	private Map<UUID, Profile> cache = new HashMap<>();
	@Getter
	private Map<UUID, String> currentIpSession = new HashMap<>();
	@Getter private List<VIPReward> vipRewards = new ArrayList<>();

	@Getter
	private Nebula instance;

	@Getter
	private MongoCollection<Document> collection;

	public ProfileHandler(Nebula instance) {
		this.instance = instance;
		this.collection = instance.getMongoHandler().getMongoDatabase().getCollection(NebulaConstants.PROFILE_COLLECTION);

		Proton.getInstance().getPidginHandler().registerPacket(NoteApplyPacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(GrantApplyPacket.class);

		Proton.getInstance().getPidginHandler().registerPacket(PermissionAddPacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(PermissionRemovePacket.class);

		Proton.getInstance().getPidginHandler().registerPacket(PunishmentExecutePacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(PunishmentPardonPacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(FriendRemovePacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(FriendRequestRemovePacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(FriendRequestAcceptPacket.class);

		Proton.getInstance().getPidginHandler().registerListener(this);

		instance.getServer().getPluginManager().registerEvents(this, instance);
		instance.getServer().getPluginManager().registerEvents(new GrantListener(), instance);
		instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, () -> {

			for (Profile profile : this.cache.values()) {

				if (profile.getActiveGrant().isActive()) {
					continue;
				}

				final Grant oldGrant = profile.getActiveGrant();

				profile.recalculateGrants();

				if (profile.getPlayer() != null) {
					new GrantExpireEvent(profile, oldGrant, profile.getActiveGrant()).call();
				}

			}
		}, 20, 20);
	}

	public void load(Profile profile) {
		this.load(profile, profile.findDocument());
	}

	public void load(Profile profile, Document document) {

		if (document == null) {

			final Grant grant = new Grant(Nebula.getInstance().getRankHandler().getDefaultRank(), UUIDCache.CONSOLE_UUID, (long) Integer.MAX_VALUE, "Profile Created");
			grant.getScopes().add("GLOBAL");

			profile.setActiveGrant(grant);
			profile.getGrants().add(grant);
			profile.getServerProfile().setFirstLogin(System.currentTimeMillis());

			profile.save();
			return;
		}

		if (profile.getName() == null && document.containsKey("name")) {
			profile.setName(document.getString("name"));
		}

		if (profile.getIpAddress() == null && document.containsKey("ipAddress") && document.getString("ipAddress") != null) {
			profile.setIpAddress(document.getString("ipAddress"));
		}

		for (String key : document.keySet()) {
			for (GlobalStatistic statistic : profile.getGlobalStatistics()) {
				if (key.contains(statistic.getStatType().name() + "-")) {
					statistic.setKills(document.getInteger(statistic.getStatType().name() + "-kills"));
					statistic.setDeaths(document.getInteger(statistic.getStatType().name() + "-deaths"));
					statistic.setKillStreak(document.getInteger(statistic.getStatType().name() + "-killStreak"));
					statistic.setHighestKillStreak(document.getInteger(statistic.getStatType().name() + "-highestKillStreak"));
					statistic.setSeasonsPlayed(document.getInteger(statistic.getStatType().name() + "-seasonsPlayed"));
					statistic.getPastTeams().addAll(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString(statistic.getStatType().name() + "-pastTeams"), ArrayList.class));
				}
			}
		}

		if (document.containsKey("serverProfile")) {
			profile.setServerProfile(new ServerProfile(document.get("serverProfile", Document.class)));
		}

		if (document.containsKey("disguiseProfile")) {
			profile.setDisguiseProfile(new DisguiseProfile(document.get("disguiseProfile", Document.class)));
		}

		if (document.containsKey("chatColor")) {
			profile.setChatColor(ChatColor.valueOf(document.getString("chatColor")));
		}

		if (document.containsKey("siblings")) {
			profile.setSiblings(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("siblings"), ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList()));
		}

		if (document.containsKey("blocked")) {
			profile.setBlocked(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("blocked"), ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList()));
		}

		if (document.containsKey("friends")) {
			profile.setFriends(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("friends"), ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList()));
		}

		if (document.containsKey("notes")) {
			profile.setNotes(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("notes"), ArrayList.class).stream()
					.map(Document::parse)
					.map(Note::new)
					.collect(Collectors.toList())
			);
		}

		if (document.containsKey("rollbacks")) {
			profile.setRollbacks(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("rollbacks"), ArrayList.class).stream()
					.map(Document::parse)
					.map(Rollback::new)
					.collect(Collectors.toList())
			);
		}

		if (document.containsKey("grants")) {
			profile.setGrants(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("grants"), ArrayList.class).stream()
					.map(Document::parse)
					.map(Grant::new)
					.filter(grant -> grant.getRank() != null)
					.collect(Collectors.toList())
			);
		}

		if (document.containsKey("activeGrant")) {

			final Grant grant = profile.getGrant(UUID.fromString(document.getString("activeGrant")));

			if (grant != null) {
				profile.setActiveGrant(grant);
			}

		}

		if (document.containsKey("activePrefix")) {

			final Prefix prefix = Nebula.getInstance().getPrefixHandler().fromUuid(UUID.fromString(document.getString("activePrefix")));

			if (prefix != null) {
				profile.setActivePrefix(prefix);
			}

		}

		if (document.containsKey("friendRequests")) {
			for (JsonElement jsonElement : new JsonParser().parse(document.getString("friendRequests")).getAsJsonArray()) {
				if ((jsonElement.getAsJsonObject().get("sentAt").getAsLong() + JavaUtils.parse("5m")) - System.currentTimeMillis() < 0)
					continue;
				profile.getFriendRequests().add(FriendRequest.deserialize(jsonElement.getAsJsonObject()));
			}
		}

		if (document.containsKey("permissions")) {
			profile.setPermissions(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("permissions"), ArrayList.class));
		}

		if (document.containsKey("punishments")) {
			//TODO: Shitty but will do for now
			profile.setPunishments(Proton.PLAIN_GSON.<List<String>>fromJson(document.getString("punishments"), ArrayList.class).stream()
					.map(string -> Document.parse(string).getString("iType").equalsIgnoreCase("NORMAL") ? new Punishment(Document.parse(string)) : new RemoveAblePunishment(Document.parse(string)))
					.collect(Collectors.toList())
			);
		}

		if (document.containsKey("authSecret")) {
			profile.setAuthSecret(document.getString("authSecret"));
		}

		profile.recalculateGrants();
	}

	public void save(Profile profile, boolean task) {

		final Document document = new Document();

		document.put("uuid", profile.getUuid().toString());
		document.put("name", profile.getName());

		if (profile.getIpAddress() != null) {
			document.put("ipAddress", profile.getIpAddress());
		}

		document.put("serverProfile", profile.getServerProfile().toDocument());

		if (profile.getDisguiseProfile() != null) {
			document.put("disguiseProfile", profile.getDisguiseProfile().toDocument());
		}

		document.put("chatColor", profile.getChatColor().name());
		document.put("siblings", Proton.PLAIN_GSON.toJson(profile.getSiblings().stream().map(UUID::toString).collect(Collectors.toList())));
		document.put("blocked", Proton.PLAIN_GSON.toJson(profile.getBlocked().stream().map(UUID::toString).collect(Collectors.toList())));
		document.put("friends", Proton.PLAIN_GSON.toJson(profile.getFriends().stream().map(UUID::toString).collect(Collectors.toList())));

		JsonArray friendrequests = new JsonArray();
		profile.getFriendRequests().forEach(request -> friendrequests.add(request.serialize()));
		document.put("friendRequests", friendrequests.toString());

		document.put("activeGrant", profile.getActiveGrant().getUuid().toString());

		if (profile.getActivePrefix() != null) {
			document.put("activePrefix", profile.getActivePrefix().getUuid().toString());
		}

		for (GlobalStatistic statistic : profile.getGlobalStatistics()) {
			document.put(statistic.getStatType().name() + "kills", statistic.getKills());
			document.put(statistic.getStatType().name() + "deaths", statistic.getDeaths());
			document.put(statistic.getStatType().name() + "killStreak", statistic.getKillStreak());
			document.put(statistic.getStatType().name() + "highestKillStreak", statistic.getHighestKillStreak());
			document.put(statistic.getStatType().name() + "seasonsPlayed", statistic.getSeasonsPlayed());
			document.put(statistic.getStatType().name() + "pastTeams", Proton.PLAIN_GSON.toJson(statistic.getPastTeams()));
		}

		document.put("notes", Proton.PLAIN_GSON.toJson(profile.getNotes().stream().map(note -> note.toDocument().toJson()).collect(Collectors.toList())));
		document.put("grants", Proton.PLAIN_GSON.toJson(profile.getGrants().stream().map(grant -> grant.toDocument().toJson()).collect(Collectors.toList())));
		document.put("rollbacks", Proton.PLAIN_GSON.toJson(profile.getRollbacks().stream().map(rollback -> rollback.toDocument().toJson()).collect(Collectors.toList())));
		document.put("permissions", Proton.PLAIN_GSON.toJson(profile.getPermissions()));
		document.put("punishments", Proton.PLAIN_GSON.toJson(profile.getPunishments().stream().map(iPunishment -> iPunishment.toDocument().toJson()).collect(Collectors.toList())));

		if (profile.getAuthSecret() != null) {
			document.put("authSecret", profile.getAuthSecret());
		}

		if (task) {
			this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () ->
					this.collection.replaceOne(Filters.eq("uuid", profile.getUuid().toString()), document, new ReplaceOptions().upsert(true))
			);
		} else {
			this.collection.replaceOne(Filters.eq("uuid", profile.getUuid().toString()), document, new ReplaceOptions().upsert(true));
		}

	}

	public CompletableFuture<List<Document>> findAltsAsync(Profile profile) {
		return CompletableFuture.supplyAsync(() -> this.findAlts(profile));
	}

	public List<Document> findAlts(Profile profile) {

		final List<Document> toReturn = new ArrayList<>();

		for (Document document : this.collection.find(Filters.eq("ipAddress", profile.getIpAddress()))) {

			final UUID uuid = UUID.fromString(document.getString("uuid"));

			if (profile.getUuid().equals(uuid)) {
				continue;
			}

			toReturn.add(document);
		}

		return toReturn;

	}

	public Profile fromUuid(UUID uuid) {
		return this.fromUuid(uuid, false);
	}

	public Profile fromUuid(UUID uuid, boolean outsideOfCache) {
		if (this.cache.containsKey(uuid)) {
			return this.cache.get(uuid);
		}

		if (outsideOfCache) {
			return new Profile(uuid, null);
		}

		return null;
	}

	public Profile fromName(String name) {
		return this.fromName(name, false, false);
	}

	public Profile fromName(String name, boolean requestMojangAPI, boolean async) {
		return this.fromName(name, requestMojangAPI, requestMojangAPI, async);
	}

	public Profile fromName(String name, boolean requestUuidCache, boolean requestMojangAPI, boolean async) {

		final Profile cachedProfile = this.cache.values().stream().filter(profile -> profile.getName().equalsIgnoreCase(name)).findAny().orElse(null);

		if (cachedProfile != null) {
			return cachedProfile;
		}

		if (requestUuidCache) {

			if (async) {
				return this.fromUuidCacheAsync(name, requestMojangAPI).join();
			}

			return this.fromUuidCache(name, requestMojangAPI);
		}

		return null;
	}

	private Profile fromUuidCache(String name, boolean requestMojangAPI) {

		final UUID uuid = UUIDUtils.uuid(name);

		if (uuid != null) {
			return new Profile(uuid, name);
		}

		if (!requestMojangAPI) {
			return null;
		}

		try {

			final UUID uuidRequest = MojangUtil.getFromMojang(name);

			//Cache him inside redis so we don't have to send a request to Mojang for this player again
			Proton.getInstance().getUuidCache().updateAll(uuidRequest, name);

			return new Profile(uuidRequest, name);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	private CompletableFuture<Profile> fromUuidCacheAsync(String name, boolean requestMojangAPI) {
		return CompletableFuture.supplyAsync(() -> this.fromUuidCache(name, requestMojangAPI));
	}

	//TODO: better way of fetching banned alts
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {

		if (this.cache.containsKey(event.getUniqueId())) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.translate("&cSomeone seems to already be on this account. If this is an error contact support."));
			return;
		}

		Profile profile = new Profile(event.getUniqueId(), event.getName());

		profile.setIpAddress(EncryptionHandler.encryptUsingKey(event.getAddress().getHostAddress()));

		RemoveAblePunishment punishment = null;

		if (profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
			punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST);
		} else if (profile.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
			punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BAN);
		}

		if (punishment != null) {
			if (Nebula.getInstance().isTestServer()) {
				Nebula.getInstance().getServer().broadcastMessage(event.getName() + ChatColor.GRAY + " would've been kicked for being banned however the server is in developer mode.");
				Nebula.getInstance().getServer().broadcastMessage(ChatColor.RED + "DEVELOPER: " + ChatColor.GRAY + event.getName() + " has tried to join but is " + punishment.getType().getExecutedContext() + " the punishment expires " + punishment.getRemainingString());
			} else {
				event.disallow(
						AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
						ChatColor.RED.toString() + "Your account is " + punishment.getType().getExecutedContext() + " from the Orbit Network "
								+ (punishment.isPermanent() ? "\nThis type of punishment does not expire" + ChatColor.GRAY.toString() + CC.ITALIC + "\nYou can appeal this at http://discord.orbit.rip": "\n\n" + ChatColor.RED + "This punishment expires in: " + ChatColor.YELLOW + punishment.getRemainingString())
				);
			}
			return;
		}

		final List<Profile> alts = profile.findAlts().stream().map(Profile::new).collect(Collectors.toList());

		String altName = null;
		RemoveAblePunishment altPunishment = null;

		for (Profile alt : alts) {

			if (alt.getPunishments().isEmpty() || alt.getActivePunishments().isEmpty() || profile.isSibling(alt)) {
				continue;
			}

			if (alt.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
				altName = alt.getFancyName();
				altPunishment = alt.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST);
				break;
			} else if (alt.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
				altName = alt.getFancyName();
				altPunishment = alt.getActivePunishment(RemoveAblePunishment.Type.BAN);
				break;
			}

		}

		if (altPunishment != null && altName != null) {
			if (Nebula.getInstance().isTestServer()) {
				Nebula.getInstance().getServer().broadcastMessage(event.getName() + ChatColor.GRAY + " would've been kicked for joining with an alt that has been banned however the server is in developer mode.");
				Nebula.getInstance().getServer().broadcastMessage(ChatColor.RED + "DEVELOPER: " + ChatColor.GRAY + event.getName() + " is related to " + altName + " who has been " + altPunishment.getType().getExecutedContext() + " previously.");
			} else {
				event.disallow(
						AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
						ChatColor.RED.toString() + "Your account is " + altPunishment.getType().getExecutedContext() + " due to a punishment related to " + altName
								+ (altPunishment.isPermanent() ? "" : "\n\n" + ChatColor.RED + "Expires: " + altPunishment.getRemainingString())
				);
			}
			return;
		}

//		final List<Profile> onlineAlts = alts.stream().filter(alt -> alt.getServerProfile().isOnline()).collect(Collectors.toList());
//
//		if (onlineAlts.size() >= 4) {
//			event.disallow(
//					AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
//					ChatColor.RED.toString() + "Spam bot prevention, please contact an admin if you think this is an error."
//			);
//			return;
//		}

		profile.getServerProfile().setOnline(true);
		profile.getServerProfile().setLastLogin(System.currentTimeMillis());
		profile.getServerProfile().setLastServer(Nebula.getInstance().getConfig().getString("server.name"));

		profile.save();

		this.cache.put(event.getUniqueId(), profile);
		this.currentIpSession.put(event.getUniqueId(), event.getAddress().getHostAddress());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {

		final Player player = event.getPlayer();

		final Profile profile = this.fromUuid(player.getUniqueId(), true);

		profile.setup(event);

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {

		final Player player = event.getPlayer();

		final Profile profile = this.fromUuid(player.getUniqueId());

		if (profile == null) return;

		profile.getServerProfile().setOnline(false);
		profile.getServerProfile().setLastLogin(System.currentTimeMillis());
		profile.getServerProfile().setLastServer(Nebula.getInstance().getConfig().getString("server.name"));

		profile.save();

		this.cache.remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {

		final Player player = event.getPlayer();

		final Profile profile = this.fromUuid(player.getUniqueId());

		profile.getServerProfile().setOnline(false);
		profile.getServerProfile().setLastLogin(System.currentTimeMillis());
		profile.getServerProfile().setLastServer(Nebula.getInstance().getConfig().getString("server.name"));

		profile.save();

		this.cache.remove(player.getUniqueId());

	}

	@IncomingPacketHandler
	public void onPunishmentExecute(PunishmentExecutePacket packet) {
		final Document document = packet.document();

		final IPunishment.Type iType = IPunishment.Type.valueOf(document.getString("iType"));

		IPunishment punishment = null;

		if (iType == IPunishment.Type.NORMAL) {
			punishment = new Punishment(document);
		} else if (iType == IPunishment.Type.REMOVE_ABLE) {
			punishment = new RemoveAblePunishment(document);
		}

		punishment.broadcast(packet.punishedFancyName());

		if (packet.broadCastOnly()) {
			return;
		}

		final Player player = Nebula.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		profile.getPunishments().add(punishment);

		punishment.execute(player);
	}

	@IncomingPacketHandler
	public void onPunishmentPardon(PunishmentPardonPacket packet) {

		final Document document = packet.document();

		final RemoveAblePunishment punishment = new RemoveAblePunishment(document);

		punishment.broadcast(packet.punishedFancyName());

		if (packet.broadCastOnly()) {
			return;
		}

		final Player player = Nebula.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final RemoveAblePunishment activePunishment = profile.getActivePunishment(punishment.getType());

		activePunishment.setPardonedAt(punishment.getPardonedAt());
		activePunishment.setPardonedReason(punishment.getPardonedReason());
		activePunishment.setPardonedSilent(punishment.getPardonedSilent());
		activePunishment.setPardoner(punishment.getPardoner());

	}

	@IncomingPacketHandler
	public void onGrantApply(GrantApplyPacket packet) {

		final Player player = Nebula.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final Grant grant = new Grant(packet.document());

		profile.getGrants().add(grant);
		profile.recalculateGrants();
	}

	@IncomingPacketHandler
	public void onGrantRemove(GrantRemovePacket packet) {

		final Player player = Nebula.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final Grant grant = new Grant(packet.document());

		final Grant toRemove = profile.getGrant(grant.getUuid());

		toRemove.setPardoner(toRemove.getPardoner());
		toRemove.setPardonedAt(toRemove.getPardonedAt());
		toRemove.setPardonedReason(toRemove.getPardonedReason());

	}

	@IncomingPacketHandler
	public void onPermissionAdd(PermissionAddPacket packet) {

		final Player player = Nebula.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(packet.uuid());

		if (profile.getPermissions().contains(packet.permission())) {
			return;
		}

		profile.getPermissions().add(packet.permission());

		player.recalculatePermissions();
	}

	@IncomingPacketHandler
	public void onPermissionRemove(PermissionRemovePacket packet) {

		final Player player = Nebula.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(packet.uuid());

		if (!profile.getPermissions().contains(packet.permission())) {
			return;
		}

		profile.getPermissions().remove(packet.permission());

		player.recalculatePermissions();
	}

	@IncomingPacketHandler
	public void onNoteApply(NoteApplyPacket packet) {

		final Player player = Nebula.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final Note note = new Note(packet.document());

		profile.getNotes().add(note);
	}

	@IncomingPacketHandler
	public void onFriendRequestSend(FriendRequestSendPacket packet) {

		Player target = Bukkit.getPlayer(packet.target());
		String name = Proton.getInstance().getUuidCache().name(packet.sender());
		if (target != null) {
			target.sendMessage(CC.translate("&7&m---------------------"));
			FancyMessage message = new FancyMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + name + " &fhas just sent you a friend request. You have &65 minutes&f to accept it."));
			message.tooltip(CC.translate("&7Click here to accept the friend request."));
			message.command("/friend accept " + name);
			message.send(target);
			target.sendMessage(CC.translate("&7&m---------------------"));

			Profile profile = getCache().get(target.getUniqueId());

			FriendRequest request = new FriendRequest();
			request.setSender(packet.sender());
			request.setTarget(packet.target());
			request.setSentAt(System.currentTimeMillis());

			profile.getFriendRequests().add(request);
			profile.save();
		}

	}

	@IncomingPacketHandler
	public void onFriendRemove(FriendRemovePacket packet) {

		Player target = Bukkit.getPlayer(packet.target());
		String name = Proton.getInstance().getUuidCache().name(packet.sender());
		if (target != null) {

			target.sendMessage(CC.translate("&7&m---------------------"));
			target.sendMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + name + " &fhas just removed you as a friend."));
			target.sendMessage(CC.translate("&7&m---------------------"));

			Profile profile = getCache().get(target.getUniqueId());

			profile.getFriends().remove(Proton.getInstance().getUuidCache().uuid(name));
			profile.save();
		}

	}

	@IncomingPacketHandler
	public void onFriendRequestRemove(FriendRequestRemovePacket packet) {

		Player target = Bukkit.getPlayer(packet.target());
		String name = Proton.getInstance().getUuidCache().name(packet.sender());
		if (target != null) {

			target.sendMessage(CC.translate("&7&m---------------------"));
			target.sendMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + name + " &fhas just rejected your friend request."));
			target.sendMessage(CC.translate("&7&m---------------------"));

			Profile profile = getCache().get(target.getUniqueId());

			profile.getFriendRequests().remove(profile.getFriendRequestFromSender(Proton.getInstance().getUuidCache().uuid(name)));
			profile.save();

		}

	}

	@IncomingPacketHandler
	public void onFriendRequestAcceptPacket(FriendRequestAcceptPacket packet) {

		Player target = Bukkit.getPlayer(packet.target());
		String name = Proton.getInstance().getUuidCache().name(packet.sender());
		if (target != null) {

			target.sendMessage(CC.translate("&7&m---------------------"));
			target.sendMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + name + " &fhas just accepted your friend request."));
			target.sendMessage(CC.translate("&7&m---------------------"));

			Profile profile = getCache().get(target.getUniqueId());

			profile.getFriendRequests().removeIf(friendRequest -> packet.sender().toString().equals(target.toString()));
			profile.save();

		}

	}
}
