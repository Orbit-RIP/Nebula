package rip.orbit.nebula.command.prefix;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.prefix.Prefix;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PrefixCreateCommand {

    @Command(
            names = {"prefix create", "tag create"},
            permission = "orbit.owner"
    )
    public static void execute(CommandSender sender,@Parameter(name = "name")String name) {

        Prefix prefix = Nebula.getInstance().getPrefixHandler().fromName(name);

        if (prefix != null) {
            sender.sendMessage(ChatColor.RED + "Prefix " + ChatColor.WHITE + prefix.getName() + ChatColor.RED + " already exists.");
            return;
        }

        prefix = new Prefix(UUID.randomUUID(),name,UUIDUtils.uuid(sender.getName()));

        Nebula.getInstance().getPrefixHandler().getCache().put(prefix.getUuid(),prefix);

        prefix.save();

        sender.sendMessage(ChatColor.GOLD + "Created a new prefix: " + ChatColor.WHITE + prefix.getName());
    }


}
