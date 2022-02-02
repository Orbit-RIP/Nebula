package cc.fyre.neutron.profile.attributes.punishment.impl;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.attributes.api.Pardonable;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.IPunishmentType;
import cc.fyre.neutron.util.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RemoveAblePunishment implements IPunishment, Pardonable {

    @Override
    public IPunishment.Type getIType() {
        return IPunishment.Type.REMOVE_ABLE;
    }

    @Getter private UUID uuid;
    @Getter private Type type;

    @Getter private UUID executor;
    @Getter private Long executedAt;
    @Getter private String executedReason;
    @Getter private Boolean executedSilent;

    @Getter @Setter private UUID pardoner;
    @Getter @Setter private Long pardonedAt;
    @Getter @Setter private String pardonedReason;
    @Getter @Setter private Boolean pardonedSilent;

    @Getter private Long duration;

    public RemoveAblePunishment(Type type,UUID executor,Long duration,String reason,Boolean silent) {
        this.uuid = UUID.randomUUID();
        this.executor = executor;
        this.type = type;
        this.duration = duration;
        this.executedAt = System.currentTimeMillis();
        this.executedReason = reason;
        this.executedSilent = silent;
    }

    public RemoveAblePunishment(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.executor = UUID.fromString(document.getString("executor"));
        this.executedAt = document.getLong("executedAt");
        this.executedReason = document.getString("executedReason");
        this.executedSilent = document.getBoolean("executedSilent");

        if (document.containsKey("pardoner") && document.containsKey("pardonedAt") && document.containsKey("pardonedReason") && document.containsKey("pardonedSilent")) {
            this.pardoner = UUID.fromString(document.getString("pardoner"));
            this.pardonedAt = document.getLong("pardonedAt");
            this.pardonedReason = document.getString("pardonedReason");
            this.pardonedSilent = document.getBoolean("pardonedSilent");
        }

        this.duration = document.getLong("duration");

        this.type = Type.valueOf(document.getString("type"));
    }

    @Override
    public Document toDocument() {

        final Document toReturn = new Document();

        toReturn.put("uuid",this.uuid.toString());
        toReturn.put("executor",this.executor.toString());
        toReturn.put("executedAt",this.executedAt);
        toReturn.put("executedReason",this.executedReason);
        toReturn.put("executedSilent",this.executedSilent);

        if (this.isPardoned()) {
            toReturn.put("pardoner",this.pardoner.toString());
            toReturn.put("pardonedAt",this.pardonedAt);
            toReturn.put("pardonedReason",this.pardonedReason);
            toReturn.put("pardonedSilent",this.pardonedSilent);
        }

        toReturn.put("duration",this.duration);

        toReturn.put("type",this.type.name().toUpperCase());
        toReturn.put("iType",this.getIType().name().toUpperCase());

        return toReturn;
    }

    public boolean isPermanent() {
        return this.duration == Integer.MAX_VALUE;
    }

    @Override
    public boolean isPardoned() {
        return this.pardoner != null && this.pardonedAt != null && this.pardonedReason != null && this.executedSilent != null;
    }

    public Long getExpiredAt() {
        return this.executedAt + this.duration;
    }

    public Long getRemaining() {
        return (this.executedAt + this.duration) - System.currentTimeMillis();
    }

    public boolean isActive() {

        if (this.getRemaining() < 0) {
            return false;
        }

        if (this.isPardoned()) {
            return false;
        }

        return true;
    }

    public String getRemainingString() {

        if (this.duration == (long) Integer.MAX_VALUE) {
            return "Never";
        }

        return ChatColor.YELLOW + FormatUtil.millisToRoundedTime(this.getRemaining(),true);
    }

    public String getDurationString() {

        if (this.duration == (long) Integer.MAX_VALUE) {
            return "Never";
        }

        return ChatColor.YELLOW + FormatUtil.millisToRoundedTime(this.duration,true);

    }

    @Override
    public void execute(Player player) {
        if (Neutron.getInstance().isTestServer()) {
            Neutron.getInstance().getServer().broadcastMessage(player.getName() + ChatColor.GRAY + " would have been punished however the network is in developer mode so nothing actually happened.");
            return;
        }

        if (this.type == Type.MUTE) {
            player.sendMessage(ChatColor.RED + "You have been muted.");
            player.sendMessage(ChatColor.RED + "Expires: " + ChatColor.YELLOW + this.getDurationString());
        } else if (this.type == Type.BAN) {

            final String kickMessage = ChatColor.RED + "Your account has been banned from the " + Neutron.getInstance().getNetwork().getNetworkName() + " Network." + (this.isPermanent() ? "":
                    "\n " + ChatColor.RED + "Expires: " + ChatColor.YELLOW + FormatUtil.millisToRoundedTime(this.duration,true)
            );

            Neutron.getInstance().getServer().getScheduler().runTaskLater(Neutron.getInstance(),() -> player.kickPlayer(kickMessage),5L);
        } else if (this.type == Type.BLACKLIST) {

            final String kickMessage = ChatColor.RED + "Your account has been blacklisted from the " + Neutron.getInstance().getNetwork().getNetworkName() +  " Network.";

            Neutron.getInstance().getServer().getScheduler().runTaskLater(Neutron.getInstance(),() -> player.kickPlayer(kickMessage),5L);

        }

    }

    @AllArgsConstructor
    public enum Type implements IPunishmentType {

        BAN("Ban","banned","unbanned"),
        MUTE("Mute","muted","unmuted"),
        BLACKLIST("Blacklist","blacklisted","unblacklisted");

        @Getter private String readable;
        @Getter private String executedContext;
        @Getter private String pardonedContext;

        public Type next(Type type) {

            final Type[] values = values();
            final int length = values.length - 1;
            final int ordinal = type.ordinal();

            return values[((ordinal + 1 > length) ? 0 : ordinal + 1)];
        }

    }
}
