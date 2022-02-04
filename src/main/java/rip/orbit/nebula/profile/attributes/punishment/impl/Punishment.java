package rip.orbit.nebula.profile.attributes.punishment.impl;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.attributes.punishment.IPunishment;
import rip.orbit.nebula.profile.attributes.punishment.IPunishmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.nebula.util.CC;

import java.util.UUID;

public class Punishment implements IPunishment {

    @Override
    public IPunishment.Type getIType() {
        return IPunishment.Type.NORMAL;
    }

    @Getter private UUID uuid;
    @Getter private Type type;

    @Getter private UUID executor;
    @Getter private Long executedAt;
    @Getter private String executedReason;
    @Getter private Boolean executedSilent;

    public Punishment(Type type,UUID executor,String reason,Boolean executedSilent) {
        this.uuid = UUID.randomUUID();
        this.type = type;
        this.executor = executor;
        this.executedAt = System.currentTimeMillis();
        this.executedReason = reason;
        this.executedSilent = executedSilent;
    }

    public Punishment(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.executor = UUID.fromString(document.getString("executor"));
        this.executedAt = document.getLong("executedAt");
        this.executedReason = document.getString("executedReason");
        this.executedSilent = document.getBoolean("executedSilent");

        this.type = Type.valueOf(document.getString("type"));
    }

    @Override
    public Document toDocument() {


        final Document toReturn = new Document();

        toReturn.put("uuid",this.uuid.toString());
        toReturn.put("executor",this.executor.toString());
        toReturn.put("executedAt",this.executedAt.longValue());
        toReturn.put("executedReason",this.executedReason);
        toReturn.put("executedSilent",this.executedSilent.booleanValue());

        toReturn.put("type",this.type.name().toUpperCase());
        toReturn.put("iType",this.getIType().name().toUpperCase());

        return toReturn;
    }

    public void execute(Player player) {
        if (player == null) {
            return;
        }

        if (Nebula.getInstance().isTestServer()) {
            Nebula.getInstance().getServer().broadcastMessage(player.getName() + ChatColor.GRAY + " would have been punished however the network is in developer mode so nothing actually happened.");
            return;
        }

        if (this.type == Type.WARN) {
            player.sendMessage(ChatColor.GRAY + "");
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have been warned" + CC.RED + "\nReason: " + ChatColor.YELLOW + this.executedReason);
            player.sendMessage(ChatColor.GRAY + "");
        } else if (this.type == Type.KICK) {
            final String kickMessage = ChatColor.RED + "You have been kicked by a staff member. \nReason: " + ChatColor.YELLOW + this.executedReason;

            Nebula.getInstance().getServer().getScheduler().runTaskLater(Nebula.getInstance(),() -> player.kickPlayer(kickMessage),5L);
        }
    }

    @AllArgsConstructor
    public enum Type implements IPunishmentType {

        WARN("Warn","warned",null),
        KICK("Kick","kicked",null);

        @Getter private String readable;
        @Getter private String executedContext;
        @Getter private String pardonedContext;

    }

}
