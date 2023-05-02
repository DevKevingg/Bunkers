package me.devkevin.bunkers.entity;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.entity.types.*;
import me.devkevin.bunkers.menu.MenuListener;
import me.devkevin.bunkers.menu.menu.*;
import me.devkevin.bunkers.team.Team;
import me.devkevin.bunkers.utils.BukkitUtils;
import me.devkevin.bunkers.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class EntityManager implements Listener {
    public Map<Entity, ShopType> killedVillagers = new HashMap<>();

    public EntityManager() {
        Bukkit.getPluginManager().registerEvents(this, Bunkers.getPlugin());
        Bukkit.getPluginManager().registerEvents(new MenuListener(), Bunkers.getPlugin());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() != null && event.getRightClicked() instanceof Villager && event.getRightClicked().getCustomName() != null) {
            String name = ChatColor.stripColor(event.getRightClicked().getCustomName());

            if (name.toLowerCase().contains("combat")) {
                new CombatMenu(player).open(player);
            } else if (name.toLowerCase().contains("sell")) {
                new SellMenu(player).open(player);
            } else if (name.toLowerCase().contains("build")) {
                new BuildMenu(player).open(player);
            } else if (name.toLowerCase().contains("enchant")) {
                new EnchanterMenu(player).open(player);
            } else if (name.toLowerCase().contains("ability")) {
                new AbilityMenu().openMenu(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager && ((Villager) event.getEntity()).getCustomName() != null && ((Villager) event.getEntity()).getCustomName().toLowerCase().contains("dead"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Villager && event.getEntity().getCustomName() != null) {
            if (event.getEntity().getCustomName().toLowerCase().contains("ability") ||event.getEntity().getCustomName().toLowerCase().contains("combat") || event.getEntity().getCustomName().toLowerCase().contains("enchant") || event.getEntity().getCustomName().toLowerCase().contains("build") || event.getEntity().getCustomName().toLowerCase().contains("sell")) {
                final int[] counter = {60};
                ShopType type = null;

                if (event.getEntity().getCustomName().toLowerCase().contains("combat")) type = ShopType.COMBAT;
                else if (event.getEntity().getCustomName().toLowerCase().contains("enchant")) type = ShopType.ENCHANT;
                else if (event.getEntity().getCustomName().toLowerCase().contains("sell")) type = ShopType.SELL;
                else if (event.getEntity().getCustomName().toLowerCase().contains("build")) type = ShopType.BUILD;
                else if (event.getEntity().getCustomName().toLowerCase().contains("ability")) type = ShopType.ABILITY;

                killedVillagers.put(event.getEntity(), type);

                Villager villager = (Villager) event.getEntity().getLocation().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.VILLAGER);
                villager.setFallDistance(0);
                villager.setRemoveWhenFarAway(false);
                villager.setAdult();
                villager.setProfession(Villager.Profession.BLACKSMITH);
                villager.setCanPickupItems(false);
                villager.setMaxHealth(40);
                villager.setHealth(40);
                villager.setCustomNameVisible(true);
                villager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, -6, true), true);

                new BukkitRunnable() {
                    @Override public void run() {
                        if (counter[0] <= 0) {
                            Team team = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.getByChar(ChatColor.getLastColors(event.getEntity().getCustomName()).replace("§", "").replace("&", "")));
                            if (killedVillagers.get(event.getEntity()) == ShopType.SELL) {
                                new SellEntity(team, LocationUtils.getLocation(team.getSellShop()));
                            } else if (killedVillagers.get(event.getEntity()) == ShopType.BUILD) {
                                new BuildEntity(team, LocationUtils.getLocation(team.getBuildShop()));
                            } else if (killedVillagers.get(event.getEntity()) == ShopType.ENCHANT) {
                                new EnchantEntity(team, LocationUtils.getLocation(team.getEnchantShop()));
                            } else if (killedVillagers.get(event.getEntity()) == ShopType.COMBAT) {
                                new CombatEntity(team, LocationUtils.getLocation(team.getCombatShop()));
                            } else if (killedVillagers.get(event.getEntity()) == ShopType.ABILITY) {
                                new AbilityEntity(team, LocationUtils.getLocation(team.getAbilityShop()));
                            }

                            killedVillagers.remove(event.getEntity());
                            villager.remove();

                            cancel();
                            return;
                        }

                        villager.setCustomName(ChatColor.RED + "Dead for" + ChatColor.GRAY + ": " + ChatColor.WHITE + BukkitUtils.niceTime(counter[0], false));
                        counter[0]--;
                    }
                }.runTaskTimer(Bunkers.getPlugin(), 0L, 20L);
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            event.getEntity().setMetadata("noCollision", new FixedMetadataValue(Bunkers.getPlugin(), true));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        World world = to.getWorld();
        Location location = new Location(world, to.getX(), to.getY(), to.getZ());

        for (Entity entity : world.getNearbyEntities(location, 1.0, 1.0, 1.0)) {
            if (entity instanceof Villager && entity.hasMetadata("noCollision")) {
                event.setTo(event.getFrom());
                break;
            }
        }
    }


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Villager && event.getDamager() instanceof Player) {
            ((LivingEntity) event.getEntity()).setMaximumAir(0);
            ((LivingEntity) event.getEntity()).setVelocity(new Vector(0,0,0));
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getEntity().getType().equals(EntityType.VILLAGER)) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (e.getEntity().getType().equals(EntityType.VILLAGER)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntityType() != EntityType.VILLAGER) {
            if (e.getEntityType() != EntityType.PLAYER) {
                if (e.getEntityType() != EntityType.ARROW) {
                    if (e.getEntityType() != EntityType.SPLASH_POTION) {
                        if (e.getEntityType() != EntityType.DROPPED_ITEM) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
