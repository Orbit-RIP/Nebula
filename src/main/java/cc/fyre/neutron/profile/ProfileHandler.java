package cc.fyre.neutron.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.grant.listener.GrantListener;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantApplyPacket;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantRemovePacket;
import cc.fyre.neutron.profile.attributes.note.Note;
import cc.fyre.neutron.profile.attributes.note.packet.NoteApplyPacket;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentExecutePacket;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentPardonPacket;
import cc.fyre.neutron.profile.attributes.rollback.Rollback;
import cc.fyre.neutron.profile.attributes.server.ServerProfile;
import cc.fyre.neutron.profile.disguise.DisguiseProfile;
import cc.fyre.neutron.profile.event.GrantExpireEvent;
import cc.fyre.neutron.profile.packet.PermissionAddPacket;
import cc.fyre.neutron.profile.packet.PermissionRemovePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import cc.fyre.proton.util.MojangUtil;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.uuid.UUIDCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ProfileHandler implements Listener, PacketListener {

	@Getter
	private Map<UUID, Profile> cache = new HashMap<>();
	@Getter
	private Map<UUID, String> currentIpSession = new HashMap<>();

	@Getter
	private Neutron instance;

	@Getter
	private MongoCollection<Document> collection;

	public ProfileHandler(Neutron instance) {
		this.instance = instance;
		this.collection = instance.getMongoHandler().getMongoDatabase().getCollection(NeutronConstants.PROFILE_COLLECTION);

		Proton.getInstance().getPidginHandler().registerPacket(NoteApplyPacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(GrantApplyPacket.class);

		Proton.getInstance().getPidginHandler().registerPacket(PermissionAddPacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(PermissionRemovePacket.class);

		Proton.getInstance().getPidginHandler().registerPacket(PunishmentExecutePacket.class);
		Proton.getInstance().getPidginHandler().registerPacket(PunishmentPardonPacket.class);

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

			final Grant grant = new Grant(Neutron.getInstance().getRankHandler().getDefaultRank(), UUIDCache.CONSOLE_UUID, (long) Integer.MAX_VALUE, "Profile Created");
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

			final Prefix prefix = Neutron.getInstance().getPrefixHandler().fromUuid(UUID.fromString(document.getString("activePrefix")));

			if (prefix != null) {
				profile.setActivePrefix(prefix);
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

	public void save(Profile profile) {

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

		document.put("activeGrant", profile.getActiveGrant().getUuid().toString());

		if (profile.getActivePrefix() != null) {
			document.put("activePrefix", profile.getActivePrefix().getUuid().toString());
		}

		document.put("notes", Proton.PLAIN_GSON.toJson(profile.getNotes().stream().map(note -> note.toDocument().toJson()).collect(Collectors.toList())));
		document.put("grants", Proton.PLAIN_GSON.toJson(profile.getGrants().stream().map(grant -> grant.toDocument().toJson()).collect(Collectors.toList())));
		document.put("rollbacks", Proton.PLAIN_GSON.toJson(profile.getRollbacks().stream().map(rollback -> rollback.toDocument().toJson()).collect(Collectors.toList())));
		document.put("permissions", Proton.PLAIN_GSON.toJson(profile.getPermissions()));
		document.put("punishments", Proton.PLAIN_GSON.toJson(profile.getPunishments().stream().map(iPunishment -> iPunishment.toDocument().toJson()).collect(Collectors.toList())));

		if (profile.getAuthSecret() != null) {
			document.put("authSecret", profile.getAuthSecret());
		}

		this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () ->
				this.collection.replaceOne(Filters.eq("uuid", profile.getUuid().toString()), document, new ReplaceOptions().upsert(true))
		);

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

		final Profile profile = new Profile(event.getUniqueId(), event.getName());

		profile.setIpAddress(event.getAddress().getHostAddress());

		RemoveAblePunishment punishment = null;

		if (profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
			punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST);
		} else if (profile.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
			punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BAN);
		}

		if (punishment != null) {
			if (Neutron.getInstance().isTestServer()) {
				Neutron.getInstance().getServer().broadcastMessage(event.getName() + ChatColor.GRAY + " would've been kicked for being banned however the server is in developer mode.");
				Neutron.getInstance().getServer().broadcastMessage(ChatColor.RED + "DEVELOPER: " + ChatColor.GRAY + event.getName() + " has tried to join but is " + punishment.getType().getExecutedContext() + " the punishment expires " + punishment.getRemainingString());
			} else {
				event.disallow(
						AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
						ChatColor.RED.toString() + "Your account is " + punishment.getType().getExecutedContext() + " from the " + Neutron.getInstance().getNetwork().getNetworkName() + " Network"
								+ (punishment.isPermanent() ? "" : "\n\n" + ChatColor.RED + "Expires: " + ChatColor.YELLOW + punishment.getRemainingString())
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
			if (Neutron.getInstance().isTestServer()) {
				Neutron.getInstance().getServer().broadcastMessage(event.getName() + ChatColor.GRAY + " would've been kicked for joining with an alt that has been banned however the server is in developer mode.");
				Neutron.getInstance().getServer().broadcastMessage(ChatColor.RED + "DEVELOPER: " + ChatColor.GRAY + event.getName() + " is related to " + altName + " who has been " + altPunishment.getType().getExecutedContext() + " previously.");
			} else {
				event.disallow(
						AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
						ChatColor.RED.toString() + "Your account is " + altPunishment.getType().getExecutedContext() + " due to a punishment related to " + altName
								+ (altPunishment.isPermanent() ? "" : "\n\n" + ChatColor.RED + "Expires: " + altPunishment.getRemainingString())
				);
			}
			return;
		}

		final List<Profile> onlineAlts = alts.stream().filter(alt -> alt.getServerProfile().isOnline()).collect(Collectors.toList());

		if (onlineAlts.size() >= 4) {
			event.disallow(
					AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
					ChatColor.RED.toString() + "Spam bot prevention, please contact an admin if you think this is an error."
			);
			return;
		}

		profile.getServerProfile().setOnline(true);
		profile.getServerProfile().setLastLogin(System.currentTimeMillis());
		profile.getServerProfile().setLastServer(Neutron.getInstance().getConfig().getString("server"));

		profile.save();

		this.cache.put(event.getUniqueId(), profile);
		this.currentIpSession.put(event.getUniqueId(), event.getAddress().getHostAddress());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {

		final Player player = event.getPlayer();

		final Profile profile = this.fromUuid(player.getUniqueId());

		profile.setup(event);

		if (profile.getDisguiseProfile() == null && player.isDisguised()) {
			player.undisguise();
		} else if (profile.getDisguiseProfile() != null) {

			player.disguise(profile.getDisguiseProfile().getName());

		}

		if (player.hasPermission(NeutronConstants.STAFF_PERMISSION)) {

			if (profile.getAuthSecret() == null) {
				player.setMetadata("Locked", new FixedMetadataValue(this.instance, ChatColor.RED + "Please set up your two-factor authentication using \"/2fasetup\"."));
				player.sendMessage(ChatColor.RED + "Please set up your two-factor authentication using \"/2fasetup\".");
				return;
			}

			if (profile.getIpAddress().equals(this.currentIpSession.get(player.getUniqueId()))) {
				return;
			}

			player.setMetadata("Locked", new FixedMetadataValue(this.instance, ChatColor.RED + "Please provide your two-factor code. Type \"/auth <code>\" to authenticate."));
			player.sendMessage(ChatColor.RED + "Please provide your two-factor code. Type \"/auth <code>\" to authenticate.");
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {

		final Player player = event.getPlayer();

		final Profile profile = this.fromUuid(player.getUniqueId());

		profile.getServerProfile().setOnline(false);
		profile.getServerProfile().setLastLogin(System.currentTimeMillis());
		profile.getServerProfile().setLastServer(Neutron.getInstance().getConfig().getString("server"));

		profile.save();

		this.cache.remove(player.getUniqueId());

		if (player.isDisguised()) {
			player.undisguise();
		}

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

		final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

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

		final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final RemoveAblePunishment activePunishment = profile.getActivePunishment(punishment.getType());

		activePunishment.setPardonedAt(punishment.getPardonedAt());
		activePunishment.setPardonedReason(punishment.getPardonedReason());
		activePunishment.setPardonedSilent(punishment.getPardonedSilent());
		activePunishment.setPardoner(punishment.getPardoner());

	}

	@IncomingPacketHandler
	public void onGrantApply(GrantApplyPacket packet) {

		final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final Grant grant = new Grant(packet.document());

		profile.getGrants().add(grant);
		profile.recalculateGrants();
	}

	@IncomingPacketHandler
	public void onGrantRemove(GrantRemovePacket packet) {

		final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final Grant grant = new Grant(packet.document());

		final Grant toRemove = profile.getGrant(grant.getUuid());

		toRemove.setPardoner(toRemove.getPardoner());
		toRemove.setPardonedAt(toRemove.getPardonedAt());
		toRemove.setPardonedReason(toRemove.getPardonedReason());

	}

	@IncomingPacketHandler
	public void onPermissionAdd(PermissionAddPacket packet) {

		final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(packet.uuid());

		if (profile.getPermissions().contains(packet.permission())) {
			return;
		}

		profile.getPermissions().add(packet.permission());

		player.recalculatePermissions();
	}

	@IncomingPacketHandler
	public void onPermissionRemove(PermissionRemovePacket packet) {

		final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(packet.uuid());

		if (!profile.getPermissions().contains(packet.permission())) {
			return;
		}

		profile.getPermissions().remove(packet.permission());

		player.recalculatePermissions();
	}

	@IncomingPacketHandler
	public void onNoteApply(NoteApplyPacket packet) {

		final Player player = Neutron.getInstance().getServer().getPlayer(packet.uuid());

		if (player == null) {
			return;
		}

		final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

		final Note note = new Note(packet.document());

		profile.getNotes().add(note);
	}

}
