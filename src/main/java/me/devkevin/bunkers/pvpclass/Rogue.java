package me.devkevin.bunkers.pvpclass;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.pvpclass.utils.ArmorClass;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Rogue extends ArmorClass implements Listener
{
    private Bunkers plugin;
    public static TObjectLongMap<UUID> rogueSpeedCooldowns;
    public static PotionEffect ROGUE_SPEED_EFFECT;
    public static PotionEffect ROGUE_JUMP_EFFECT;
    public static long ROGUE_SPEED_COOLDOWN_DELAY;
    public static long ROGUE_JUMP_COOLDOWN_DELAY;
    public static TObjectLongMap<UUID> rogueJumpCooldowns;
    public static TObjectLongMap<UUID> rogueBackstabCooldowns;
    
    static {
        Rogue.rogueBackstabCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        Rogue.rogueSpeedCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        Rogue.ROGUE_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 4);
        Rogue.ROGUE_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 7);
        Rogue.ROGUE_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45);
        Rogue.ROGUE_JUMP_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45);
        Rogue.rogueJumpCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
    }
    
    public Rogue(Bunkers plugin) {
        super("Rogue", TimeUnit.MILLISECONDS.toMillis(1L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player damaged = (Player)event.getEntity();
            final Player damager = (Player)event.getDamager();
            if (damaged != damager && this.plugin.getArmorClassManager().getEquippedClass(damager) == this) {
                final ItemStack itemInHand = damager.getItemInHand();
                if (itemInHand != null && itemInHand.getType() == Material.GOLD_SWORD && itemInHand.getEnchantments().isEmpty()) {
                    boolean cancelled = false;
                    for (final PotionEffect activePotionEffects : damager.getActivePotionEffects()) {
                        if (activePotionEffects.getType().equals(PotionEffectType.SLOW)) {
                            cancelled = true;
                        }
                    }
                    if (!cancelled) {
                        final Vector damagerDirection = damager.getLocation().getDirection();
                        final Vector damagedDirection = damaged.getLocation().getDirection();
                        if (damagerDirection.dot(damagedDirection) > 0.0) {
                            damaged.setHealth((damaged).getHealth() - 7.0);
                            damager.setItemInHand(new ItemStack(Material.AIR));
                            damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                            damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
                            damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + damager.getName() + " &ehas backstabbed you."));
                            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have backstabbed &c" + damaged.getName() + "&e."));
                        }
                    }
                }
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
            final long timestamp = Rogue.rogueSpeedCooldowns.get(uuid);
            final long millis = System.currentTimeMillis();
            final long remaining = (timestamp == Rogue.rogueSpeedCooldowns.getNoEntryValue()) ? -1L : (timestamp - millis);
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
                this.plugin.getEffectRestorer().setRestoreEffect(player, Rogue.ROGUE_SPEED_EFFECT);
                Rogue.rogueSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + Rogue.ROGUE_SPEED_COOLDOWN_DELAY);
            }
        }
        else if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.FEATHER) {
            if (this.plugin.getArmorClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            final long timestamp2 = Rogue.rogueJumpCooldowns.get(uuid);
            final long millis2 = System.currentTimeMillis();
            final long remaining2 = (timestamp2 == Rogue.rogueJumpCooldowns.getNoEntryValue()) ? -1L : (timestamp2 - millis2);
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
                this.plugin.getEffectRestorer().setRestoreEffect(player, Rogue.ROGUE_JUMP_EFFECT);
                Rogue.rogueJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + Rogue.ROGUE_JUMP_COOLDOWN_DELAY);
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
    }
    
    public Byte direction(final Player player) {
        double rotation = (player.getLocation().getYaw() - 90.0f) % 360.0f;
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if (0.0 <= rotation && rotation < 22.5) {
            return 12;
        }
        if (22.5 <= rotation && rotation < 67.5) {
            return 14;
        }
        if (67.5 <= rotation && rotation < 112.5) {
            return 0;
        }
        if (112.5 <= rotation && rotation < 157.5) {
            return 2;
        }
        if (157.5 <= rotation && rotation < 202.5) {
            return 4;
        }
        if (202.5 <= rotation && rotation < 247.5) {
            return 6;
        }
        if (247.5 <= rotation && rotation < 292.5) {
            return 8;
        }
        if (292.5 <= rotation && rotation < 337.5) {
            return 10;
        }
        if (337.5 <= rotation && rotation < 360.0) {
            return 12;
        }
        return null;
    }
}
