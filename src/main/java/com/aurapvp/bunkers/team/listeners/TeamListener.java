package com.aurapvp.bunkers.team.listeners;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.events.PlayerClaimEnterEvent;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamListener implements Listener {
    private Map<Location, Block> brokeBlocks = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        handleMove(event);
    }

    @EventHandler
    public void onClaimEnter(PlayerClaimEnterEvent event) {
        Team from = event.getFromTeam();
        Team to = event.getToTeam();

        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eNow leaving: " + (from == null ? "&4Warzone" : from.getColor() + from.getName())));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eNow entering: " + (to == null ? "&4Warzone" : to.getColor() + to.getName())));
    }

    private void handleMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        boolean cancelled = false;

        Team fromFaction = Bunkers.getPlugin().getTeamManager().getByLocation(from);
        Team toFaction = Bunkers.getPlugin().getTeamManager().getByLocation(to);

        if (fromFaction != toFaction) {
            PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction);
            Bukkit.getPluginManager().callEvent(calledEvent);
            cancelled = calledEvent.isCancelled();
        }

        if (cancelled) {
            from.setX(from.getBlockX() + 0.5);
            from.setZ(from.getBlockZ() + 0.5);
            event.setTo(from);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){

        Player player = event.getPlayer();

        Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
        Team brokeBlock = Bunkers.getPlugin().getTeamManager().getByLocation(event.getBlock().getLocation());

        if (team != brokeBlock) {
            player.sendMessage(ChatColor.RED + "You can't break blocks here.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getClickedBlock().getType() == Material.FENCE_GATE) {
                Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
                Team interactedBlock = Bunkers.getPlugin().getTeamManager().getByLocation(event.getClickedBlock().getLocation());

                if(team != interactedBlock) {
                    player.sendMessage(ChatColor.RED + "You can't do this!");
                    event.setCancelled(true);
                }
            }
        }
    }

    public static List<Location> locs = new ArrayList<>();

    @EventHandler
    public void onPlaceE(BlockPlaceEvent event) {
        if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.PLAYING) {
            locs.add(event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.PLAYING) {
            if (brokeBlocks.get(block.getLocation()) != null) {
                event.setCancelled(true);
                return;
            }


            if (block.getType() == Material.DIAMOND_ORE || block.getType() == Material.GOLD_ORE || block.getType() == Material.IRON_ORE || block.getType() == Material.COAL_ORE) {
                event.setCancelled(true);

                if (block.getType() == Material.DIAMOND_ORE) {
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND));
                    brokeBlocks.put(block.getLocation(), block);
                    block.setType(Material.COBBLESTONE);

                    Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> {
                        brokeBlocks.remove(block.getLocation());
                        block.setType(Material.DIAMOND_ORE);
                    }, 20 * 8L);
                } else if (block.getType() == Material.GOLD_ORE) {
                    player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
                    brokeBlocks.put(block.getLocation(), block);
                    block.setType(Material.COBBLESTONE);

                    Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> {
                        brokeBlocks.remove(block.getLocation());
                        block.setType(Material.GOLD_ORE);
                    }, 20 * 6L);
                } else if (block.getType() == Material.COAL_ORE) {
                    player.getInventory().addItem(new ItemStack(Material.COAL));
                    brokeBlocks.put(block.getLocation(), block);
                    block.setType(Material.COBBLESTONE);

                    Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> {
                        brokeBlocks.remove(block.getLocation());
                        block.setType(Material.COAL_ORE);
                    }, 20 * 3L);
                } else if (block.getType() == Material.IRON_ORE) {
                    player.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
                    brokeBlocks.put(block.getLocation(), block);
                    block.setType(Material.COBBLESTONE);

                    Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> {
                        brokeBlocks.remove(block.getLocation());
                        block.setType(Material.IRON_ORE);
                    }, 20 * 5L);
                }

                return;
            }

            Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
            Team brokeBlock = Bunkers.getPlugin().getTeamManager().getByLocation(event.getBlock().getLocation());

            if (brokeBlock == null) {
                player.sendMessage(ChatColor.RED + "You can't break blocks here.");
                event.setCancelled(true);
                return;
            }

            if (team != brokeBlock) {
                if (!locs.contains(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break blocks that aren't placed by a player!");
                } else {
                    if (!(brokeBlock.getDtr1() < 1))
                        event.setCancelled(true);
                    else {
                        event.setCancelled(false);
                    }
                }
            }
            if (team == brokeBlock) {
                if (!locs.contains(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break blocks that aren't placed by a player!");
                } else {
                    event.setCancelled(false);
                }
            }
        }
    }
}
