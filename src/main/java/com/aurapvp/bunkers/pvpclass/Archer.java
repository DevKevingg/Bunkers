package com.aurapvp.bunkers.pvpclass;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.pvpclass.utils.ArmorClass;
import com.aurapvp.bunkers.utils.CC;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Archer extends ArmorClass implements Listener
{
    public static PotionEffect ARCHER_SPEED_EFFECT;
    public static PotionEffect ARCHER_JUMP_EFFECT;
    public static TObjectLongMap<UUID> archerSpeedCooldowns;
    public static TObjectLongMap<UUID> archerJumpCooldowns;
    public static HashMap<UUID, UUID> TAGGED;
    public static Random random;
    public static long ARCHER_SPEED_COOLDOWN_DELAY;
    public static long ARCHER_JUMP_COOLDOWN_DELAY;
    public Bunkers plugin;

    static {
        Archer.ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
        Archer.ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 5);
        Archer.archerSpeedCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        Archer.archerJumpCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        Archer.TAGGED = new HashMap<>();
        Archer.random = new Random();
        Archer.ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(20);
        Archer.ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(25);
    }
    
    public Archer(Bunkers plugin) {
        super("Archer", TimeUnit.MILLISECONDS.toMillis(1L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            final Arrow arrow = (Arrow)damager;
            final ProjectileSource source = (ProjectileSource)arrow.getShooter();
            if (source instanceof Player) {
                final Player damaged = (Player)event.getEntity();
                final Player shooter = (Player)source;
                final ArmorClass equipped = this.plugin.getArmorClassManager().getEquippedClass(shooter);
                if (equipped == null || !equipped.equals(this)) {
                    return;
                }
                if (this.plugin.getTimerManager().getArcherHandler().getRemaining((Player)entity) == 0L || this.plugin.getTimerManager().getArcherHandler().getRemaining((Player)entity) < TimeUnit.SECONDS.toMillis(5L)) {
                    if (this.plugin.getArmorClassManager().getEquippedClass(damaged) != null && this.plugin.getArmorClassManager().getEquippedClass(damaged).equals(this)) {
                        return;
                    }
                    this.plugin.getTimerManager().getArcherHandler().setCooldown((Player)entity, entity.getUniqueId());
                    Archer.TAGGED.put(damaged.getUniqueId(), shooter.getUniqueId());
                }
                final double distance = shooter.getLocation().distance(damaged.getLocation());
                shooter.sendMessage(CC.translate("&e[&9Arrow Range &e(&c" + String.format("%.1f", distance) + "&e)] " + "&6Marked " + damaged.getName() + " &6for 10" + " seconds. &9&l(1 heart)"));
                damaged.sendMessage(ChatColor.GOLD + "You were Archer" + " tagged by " + ChatColor.RED + shooter.getName() + ChatColor.GOLD + " from " + ChatColor.RED + String.format("%.1f", distance) + ChatColor.GOLD + " blocks away.");
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.SUGAR) {
            if (this.plugin.getArmorClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            final long timestamp = Archer.archerSpeedCooldowns.get(uuid);
            final long millis = System.currentTimeMillis();
            final long remaining = (timestamp == Archer.archerSpeedCooldowns.getNoEntryValue()) ? -1L : (timestamp - millis);
            if (remaining > 0L) {
                player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
            }
            else {
                final ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }
                else {
                    stack.setAmount(stack.getAmount() - 1);
                }
                this.plugin.getEffectRestorer().setRestoreEffect(player, Archer.ARCHER_SPEED_EFFECT);
                Archer.archerSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + Archer.ARCHER_SPEED_COOLDOWN_DELAY);
            }
        }
        else if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.FEATHER) {
            if (this.plugin.getArmorClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            final long timestamp2 = Archer.archerJumpCooldowns.get(uuid);
            final long millis2 = System.currentTimeMillis();
            final long remaining2 = (timestamp2 == Archer.archerJumpCooldowns.getNoEntryValue()) ? -1L : (timestamp2 - millis2);
            if (remaining2 > 0L) {
                player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining2, true, true) + ".");
            }
            else {
                final ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }
                else {
                    stack.setAmount(stack.getAmount() - 1);
                }
                this.plugin.getEffectRestorer().setRestoreEffect(player, Archer.ARCHER_JUMP_EFFECT);
                Archer.archerJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + Archer.ARCHER_JUMP_COOLDOWN_DELAY);
            }
        }
    }

    @Override
    public boolean isApplicableFor(final Player player) {
        final ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET) {
            return false;
        }
        final ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS) {
            return false;
        }
        final ItemStack boots = player.getInventory().getBoots();
        return boots != null && boots.getType() == Material.LEATHER_BOOTS;
    }
}
