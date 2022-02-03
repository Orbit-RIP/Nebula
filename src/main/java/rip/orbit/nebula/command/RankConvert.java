package rip.orbit.nebula.command;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.prefix.Prefix;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.rank.Rank;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.uuid.UUIDCache;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RankConvert {
    @Command(
            names = {"convert aquacore"},
            async = true,
            permission = "commands.convert.aquacore"
    )
    public static void ranks(CommandSender sender) {
        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        final Nebula instance = Nebula.getInstance();

        final ServerAddress serverAddress = new ServerAddress(instance.getConfig().getString("mongo.host"),instance.getConfig().getInt("mongo.port"));

        MongoClient mongoClient;

        if (instance.getConfig().getString("mongo.pass") == null || instance.getConfig().getString("mongo.pass").equalsIgnoreCase("")) {
            mongoClient = new MongoClient(serverAddress);
        } else {
            mongoClient = new MongoClient(serverAddress, Collections.singletonList(MongoCredential.createCredential(
                    instance.getConfig().getString("mongo.user"),
                    instance.getConfig().getString("mongo.authDatabase"),
                    instance.getConfig().getString("mongo.pass").toCharArray()))
            );
        }

        final MongoDatabase database = mongoClient.getDatabase("Core5");

        database.getCollection("Ranks").find().iterator().forEachRemaining(it -> {

            final Rank rank = new Rank(UUID.randomUUID(),it.getString("name"),UUIDCache.CONSOLE_UUID);

            rank.setPrefix(it.getString("prefix"));
            rank.setColor(ChatColor.valueOf(it.getString("color")));
            rank.setWeight(new AtomicInteger(it.getInteger("weight")));
            rank.setPermissions(Proton.PLAIN_GSON.<List<String>>fromJson(it.getString("permissions"),ArrayList.class));
        });

        database.getCollection("Documentation").find().iterator().forEachRemaining(it -> {

            final Profile profile = new Profile(UUID.fromString(it.getString("uuid")),it.getString("name"));

            profile.setIpAddress(it.getString("address"));

            profile.getServerProfile().setFirstLogin(it.getLong("lastSeen"));
            profile.getServerProfile().setLastLogin(it.getLong("lastSeen"));
            profile.getServerProfile().setOnline(false);
            profile.getServerProfile().setLastServer(it.getString("lastServer"));

            profile.setPermissions(Proton.PLAIN_GSON.<List<String>>fromJson(it.getString("permissions"),ArrayList.class));

            profile.getGrants().add(new Grant(Nebula.getInstance().getRankHandler().getDefaultRank(), UUIDCache.CONSOLE_UUID,(long)Integer.MAX_VALUE,"Default Grant"));
            profile.recalculateGrants();
        });

        database.getCollection("Tags").find().iterator().forEachRemaining(it -> {

            final Prefix prefix = new Prefix(UUID.randomUUID(),it.getString("name"),UUIDCache.CONSOLE_UUID);

            prefix.setDisplay(ChatColor.valueOf(it.getString("color")) + it.getString("prefix"));
            prefix.setCreatedAt(System.currentTimeMillis());
            prefix.setWeight(new AtomicInteger(it.getInteger("weight")));
        });
    }

    @Command(
            names = {"convert pex"},
            async = true,
            permission = ""
    )
    public static void pex(Player player) {
        if (Nebula.getInstance().isTestServer() && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        player.sendMessage("no");
    }

}
