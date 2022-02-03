package rip.orbit.nebula.profile.attributes.rollback;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.NebulaConstants;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.api.Executable;
import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Rollback implements Executable {

    private UUID executor;
    private Long executedAt;
    private String executedReason;

    private boolean log;
    private int amount;

    private UUID executorTarget;
    private Long duration;
    private RollbackType type;

    public void execute(CommandSender sender) {

        final List<Profile> toSave = new ArrayList<>();

        for (Player loopPlayer : Nebula.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.hasPermission(NebulaConstants.MANAGER_PERMISSION)) {
                continue;
            }

            loopPlayer.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
            loopPlayer.sendMessage(Nebula.getInstance().getProfileHandler().fromUuid(this.executor).getFancyName() + ChatColor.RED + " is executing a rollback for " + this.type.getReadable() + ".");
            loopPlayer.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
        }

        Nebula.getInstance().getServer().getScheduler().runTaskAsynchronously(Nebula.getInstance(),() -> {

            for (Document document : Nebula.getInstance().getMongoHandler().getMongoDatabase().getCollection(NebulaConstants.PROFILE_COLLECTION).find()) {

                final Profile profile = new Profile(document);

                if (this.type == RollbackType.PUNISHMENT) {

                    if (profile.getActivePunishments().isEmpty()) {
                        continue;
                    }

                    if (!toSave.contains(profile)) {
                        toSave.add(profile);
                    }

                    for (RemoveAblePunishment activePunishment : profile.getActivePunishments()) {

                        if (!activePunishment.getExecutor().equals(this.executorTarget)) {
                            continue;
                        }

                        if (System.currentTimeMillis() - activePunishment.getExecutedAt() <= this.duration) {
                            activePunishment.setPardoner(this.executor);
                            activePunishment.setPardonedAt(this.executedAt);
                            activePunishment.setPardonedSilent(true);
                            activePunishment.setPardonedReason(this.executedReason + " (Rollback)");

                            if (this.log) {
                                if (sender != null) {
                                    sender.sendMessage(ChatColor.GOLD + "Pardoned " + ChatColor.WHITE + activePunishment.getType().getReadable() + ChatColor.GRAY + " -> " + profile.getFancyName() + ChatColor.GOLD + ".");
                                }
                            }

                            this.amount++;
                        }
                    }

                } else if (this.type == RollbackType.GRANT) {

                    if (profile.getActiveGrants().isEmpty()) {
                        continue;
                    }

                    if (!toSave.contains(profile)) {
                        toSave.add(profile);
                    }

                    for (Grant grant : profile.getActiveGrants()) {

                        if (!grant.getExecutor().equals(this.executorTarget)) {
                            continue;
                        }

                        if (System.currentTimeMillis() - grant.getExecutedAt() <= this.duration) {
                            grant.setPardoner(this.executor);
                            grant.setPardonedAt(this.executedAt);
                            grant.setPardonedReason(this.executedReason + " (Rollback)");

                            if (this.log && sender != null) {
                                sender.sendMessage(ChatColor.GOLD + "Removed grant " + grant.getRank().getFancyName() + ChatColor.GRAY + " -> " + profile.getFancyName() + ChatColor.GOLD + ".");
                            }

                            this.amount++;
                        }
                    }

                }
            }

            toSave.forEach(Profile::save);

            final Document document = new Document();

            document.put("type",this.type.name());
            document.put("amount",this.amount);
            document.put("executedReason",this.executedReason);
            document.put("executor",this.executor.toString());
            document.put("executorTarget",this.executorTarget.toString());
            document.put("duration",this.duration);

            Nebula.getInstance().getMongoHandler().getMongoDatabase().getCollection(NebulaConstants.ROLLBACK_LOG_COLLECTION).insertOne(document);

            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
                sender.sendMessage(ChatColor.RED + "Removed a total of " + ChatColor.WHITE + amount + ChatColor.RED + " " + this.type.getReadable() + ".");
                sender.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
            }

        });

    }

    public Document toDocument() {

        final Document toReturn = new Document();

        toReturn.put("type", this.type.toString());
        toReturn.put("amount", this.amount);
        toReturn.put("executedReason", this.executedReason);
        toReturn.put("executor", this.executor.toString());
        toReturn.put("executorTarget", this.executorTarget.toString());
        toReturn.put("duration", this.duration);

        return toReturn;
    }

    public Rollback(Document document) {
        this.type = RollbackType.valueOf(document.getString("type").toUpperCase());
        this.amount = document.getInteger("amount");
        this.executedReason = document.getString("executedReason");
        this.executor = UUID.fromString(document.getString("executor"));
        this.executorTarget = UUID.fromString(document.getString("executorTarget"));
        this.duration = document.getLong("duration");
    }
}
