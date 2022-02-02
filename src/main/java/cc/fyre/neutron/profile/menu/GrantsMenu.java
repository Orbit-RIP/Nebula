package cc.fyre.neutron.profile.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.grant.comparator.GrantDateComparator;
import cc.fyre.neutron.profile.attributes.grant.comparator.GrantWeightComparator;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantRemovePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantsMenu extends PaginatedMenu {

    @Getter private Profile profile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.profile.getFancyName();
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public Map<Integer,Button> getAllPagesButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Grant grant : this.profile.getGrants().stream().sorted(new GrantWeightComparator().reversed().thenComparing(new GrantDateComparator().reversed())).collect(Collectors.toList())) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(grant.getExecutedAt()));
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "By: " + grant.getExecutedByFancyName());
                    toReturn.add(ChatColor.YELLOW + "Rank: " + grant.getRank().getFancyName());
                    toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + grant.getExecutedReason());
                    if (grant.isPermanent()) {
                        toReturn.add(ChatColor.YELLOW + "Time: " + ChatColor.RED + "Forever");
                    } else {
                        toReturn.add(ChatColor.YELLOW + "Time: " + ChatColor.RED + TimeUtils.formatIntoDetailedString((int) (grant.getDuration() / 1000)));
                    }
                    toReturn.add(ChatColor.YELLOW + "Scopes: " + ChatColor.RED + StringUtils.join(grant.getScopes(), ", "));

                    if (grant.isActive() && !grant.isPermanent()) {
                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                        toReturn.add(ChatColor.YELLOW + "Time Left: " + ChatColor.RED + grant.getRemainingString());
                    }
                    if (!grant.isActive() && grant.hasExpired()) {
                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                        toReturn.add(ChatColor.RED + "This grant has expired!");
                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                        return toReturn;
                    }

                    if (grant.isPardoned()) {
                        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                        toReturn.add(ChatColor.YELLOW + "Pardoned By: " + grant.getPardonedByFancyName());
                        toReturn.add(ChatColor.YELLOW + "Pardoned At: " + ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(grant.getPardonedAt())));
                        toReturn.add(ChatColor.YELLOW + "Pardoned Reason: " + ChatColor.RED + grant.getPardonedReason());
                    } else {

                        if (player.hasPermission(NeutronConstants.MANAGER_PERMISSION) && !grant.getRankUuid().equals(Neutron.getInstance().getRankHandler().getDefaultRank().getUuid())) {
                            toReturn.add("");
                            toReturn.add(ChatColor.WHITE + "Click to remove grant.");
                        }

                    }

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.WOOL;
                }

                @Override
                public byte getDamageValue(Player player) {

                    if (grant.isActive()) {
                        return grant.isPermanent() ? DyeColor.GREEN.getWoolData():DyeColor.YELLOW.getWoolData();
                    }

                    return DyeColor.RED.getWoolData();
                }

                @Override
                public void clicked(Player player,int slot,ClickType clickType) {

                    if (!grant.isActive()) {
                        return;
                    }

                    if (!player.hasPermission(NeutronConstants.MANAGER_PERMISSION)) {
                        return;
                    }

                    if (grant.getRankUuid().equals(Neutron.getInstance().getRankHandler().getDefaultRank().getUuid())) {
                        return;
                    }

                    player.closeInventory();

                    final ConversationFactory factory = new ConversationFactory(Proton.getInstance()).withFirstPrompt(new RemovePrompt(profile,grant)).withLocalEcho(false);

                    player.beginConversation(factory.buildConversation(player));
                }
            });

        }

        return toReturn;
    }

    @AllArgsConstructor
    public class RemovePrompt extends StringPrompt {

        @Getter private Profile profile;
        @Getter private Grant grant;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return ChatColor.RED + "Please provide a reason to remove this grant.";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext,String input) {

            final Player sender = (Player)conversationContext.getForWhom();

            System.out.println(sender.getName());

            if (input.equalsIgnoreCase("cancel")) {
                sender.sendRawMessage(ChatColor.RED + "Cancelled removing " + this.grant.getRank().getFancyName() + ChatColor.GOLD + " grant from " + this.profile.getFancyName() + ChatColor.GOLD + ".");
                return END_OF_CONVERSATION;
            }

            this.grant.setPardoner(sender.getUniqueId());
            this.grant.setPardonedAt(System.currentTimeMillis());
            this.grant.setPardonedReason(input);

            this.profile.recalculateGrants();
            this.profile.save();

            if (this.profile.getPlayer() == null) {
                Proton.getInstance().getPidginHandler().sendPacket(new GrantRemovePacket(this.profile.getUuid(),this.grant.toDocument()));
            }

            sender.sendRawMessage(ChatColor.GREEN + "Removed " + this.grant.getRank().getFancyName() + ChatColor.GOLD + " grant from " + this.profile.getFancyName() + ChatColor.GOLD + ".");
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
