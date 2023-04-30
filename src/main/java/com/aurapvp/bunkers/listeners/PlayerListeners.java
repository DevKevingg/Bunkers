package com.aurapvp.bunkers.listeners;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.BunkersAPI;
import com.aurapvp.bunkers.events.GameStatusChangeEvent;
import com.aurapvp.bunkers.game.GameManager;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.profiles.status.PlayerStatus;
import com.aurapvp.bunkers.team.Team;
import com.aurapvp.bunkers.team.chat.ChatMode;
import com.aurapvp.bunkers.timer.PvPCooldown;
import com.aurapvp.bunkers.utils.Animation;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.ItemBuilder;
import com.aurapvp.bunkers.utils.LocationUtils;
import com.aurapvp.bunkers.utils.menu.Button;
import com.aurapvp.bunkers.utils.menu.Menu;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PlayerListeners implements Listener {

	public static Map<Block, Block> oldBlockNewBlock = new HashMap<>();

	private Bunkers bunkers = Bunkers.getPlugin();

	ItemStack red = new ItemBuilder(Material.WOOL).setDurability(14).setDisplayName("&c&lRed Team").create();
	ItemStack blue = new ItemBuilder(Material.WOOL).setDurability(3).setDisplayName("&9&lBlue Team").create();
	ItemStack green = new ItemBuilder(Material.WOOL).setDurability(5).setDisplayName("&a&lGreen Team").create();
	ItemStack yellow = new ItemBuilder(Material.WOOL).setDurability(4).setDisplayName("&e&lYellow Team").create();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.getMessage().startsWith("@")) {
			Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(event.getPlayer());
			if (team != null) {
				team.getMembers().forEach(e -> {
					Player p = Bukkit.getPlayer(e);
					p.sendMessage(event.getMessage().replace("@", ""));

				});
				event.setCancelled(true);
				return;
			}
		}
		Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(event.getPlayer());
		Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(event.getPlayer());
		if (profile.getChatMode() == ChatMode.TEAM) {
			if (team != null) {
				team.getMembers().forEach(e -> {
					Player p = Bukkit.getPlayer(e);
					p.sendMessage(event.getMessage().replace("@", ""));

				});
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.getInventory().clear();

		//NOTE TO SELF, ADD CHEATBREAKER CHECK. Update 4/14/2020: LMAOOOOOO

		event.setJoinMessage(null);

		if (bunkers.getInformationManager().getInformation().getLobbyLocation() != null) {
			player.teleport(LocationUtils.getLocation(bunkers.getInformationManager().getInformation().getLobbyLocation()));
		} else {
			player.sendMessage(ChatColor.RED + "You must set the spawn location.");
		}

		if (bunkers.getGameManager().getStatus() != GameStatus.WAITING) {
			bunkers.getSpectatorManager().setSpectator(player);
			return;
		}

		if (bunkers.getGameManager().canBePlayed()) {
			if (bunkers.getInformationManager().getInformation().getMinPlayers() <= Bukkit.getOnlinePlayers().size() && bunkers.getGameManager().getStatus() == GameStatus.WAITING) {
				bunkers.getGameManager().startCooldown();
			}

			BunkersAPI.reset(player);

			player.getInventory().setItem(1, red);
			player.getInventory().setItem(3, blue);
			player.getInventory().setItem(5, green);
			player.getInventory().setItem(7, yellow);
		}

		Animation animation = new Animation("footer", player.getUniqueId(), 30L);

		animation.getLines().add(xyz.refinedev.spigot.utils.CC.YELLOW + "udrop.club");
		animation.getLines().add(xyz.refinedev.spigot.utils.CC.YELLOW + "store.udrop.club");
		animation.getLines().add(xyz.refinedev.spigot.utils.CC.YELLOW + "udrop.club/discord");
		animation.getLines().add(xyz.refinedev.spigot.utils.CC.YELLOW + "udrop.club/twitter");
	}

	@EventHandler
	public void onDmg(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(p.getUniqueId());
			if (profile.getStatus() == PlayerStatus.SPECTATOR) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItem();
		Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);


		if (profile.getStatus().equals(PlayerStatus.SPECTATOR)) {
			if (event.getItem() == null) {
				event.setCancelled(true);
				return;
			}
			if (event.getItem().getType() == Material.REDSTONE) {
				event.setCancelled(true);
				player.kickPlayer("Left Match");
				return;
			}
			if (event.getItem().getType() == Material.COMPASS) {
				event.setCancelled(true);
				Menu m = new Menu() {
					@Override
					public String getTitle(Player player) {
						return "Online Playing";
					}

					@Override
					public int size() {
						return 27;
					}

					@Override
					public Map<Integer, Button> getButtons(Player player) {
						Map<Integer, Button> buttons = new HashMap<>();

						int i = 0;
						for (Player on : Bunkers.getPlugin().getServer().getOnlinePlayers()) {
							Profile p = Bunkers.getPlugin().getProfileManager().getProfile(on);
							if (p.getStatus() == PlayerStatus.SPECTATOR) {
								continue;
							}
							buttons.put(i, new Button() {
								@Override
								public ItemStack getItem(Player player) {
									return new ItemBuilder(Material.SKULL_ITEM).setDisplayName(CC.chat("" + on.getName())).addLore(CC.chat("&7Click to teleport to " + on.getName())).create();
								}

								@Override
								public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
									player.teleport(on);
								}
							});
							++i;
						}

						return buttons;
					}
				};
				m.openMenu(player);
				return;
			}
			event.setCancelled(true);
			return;
		}

		if (itemStack != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
			if (itemStack.isSimilar(red)) {
				Team team = Bunkers.getPlugin().getTeamManager().getByName("Red");

				if (team.getMembers().size() >= 5) {
					player.sendMessage(ChatColor.RED + "The red team is full.");
					return;
				}

				if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
					Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
				}

				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &cred &eteam."));
				team.getMembers().add(player.getUniqueId());
			} else if (itemStack.isSimilar(blue)) {
				Team team = Bunkers.getPlugin().getTeamManager().getByName("Blue");

				if (team.getMembers().size() >= 5) {
					player.sendMessage(ChatColor.RED + "The blue team is full.");
					return;
				}

				if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
					Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
				}

				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &9blue &eteam."));
				team.getMembers().add(player.getUniqueId());

			} else if (itemStack.isSimilar(green)) {
				Team team = Bunkers.getPlugin().getTeamManager().getByName("Green");

				if (team.getMembers().size() >= 5) {
					player.sendMessage(ChatColor.RED + "The green team is full.");
					return;
				}

				if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
					Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
				}

				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &agreen &eteam."));
				team.getMembers().add(player.getUniqueId());
			} else if (itemStack.isSimilar(yellow)) {
				Team team = Bunkers.getPlugin().getTeamManager().getByName("Yellow");

				if (team.getMembers().size() >= 5) {
					player.sendMessage(ChatColor.RED + "The yellow team is full.");
					return;
				}

				if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
					Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
				}

				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &4yellow &eteam."));
				team.getMembers().add(player.getUniqueId());
			}
		}
	}

//	@EventHandler
//	public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
//		if (Bukkit.getServer().getOnlinePlayers().size() >= 20) {
//			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
//			event.setKickMessage(ChatColor.RED + "The match is full.");
//			return;
//		}
//
//		if (bunkers.getGameManager().getStatus() != GameStatus.WAITING) {
//			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
//			event.setKickMessage(ChatColor.RED + "You can't join a started match.");
//		}
//	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (bunkers.getGameManager().getStatus() != GameStatus.PLAYING) {
			Player player = event.getPlayer();

			if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (bunkers.getGameManager().getStatus() != GameStatus.PLAYING) {
			Player player = event.getPlayer();

			if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) return;

		if (bunkers.getGameManager().getStatus() != GameStatus.PLAYING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onChange(GameStatusChangeEvent event) {
		if (event.getNewStatus() == GameStatus.ENDING) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
				profile.save();
			});
		}
	}

	@EventHandler
	public void onDmgByEntity(EntityDamageByEntityEvent event) {
		try {
			if (event.getDamager() instanceof Projectile) {
				if (event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					Player shooter = (Player) ((Projectile) event.getDamager()).getShooter();
					if (Bunkers.getPlugin().getTeamManager().getByPlayer(shooter).getColor() == Bunkers.getPlugin().getTeamManager().getByPlayer(player).getColor()) {
						event.setCancelled(true);
						return;
					}

				}
			}
		} catch (NullPointerException ignored) {

		}
	}

	@EventHandler
	public void onDmg(EntityDamageEvent event) {
		if (PvPCooldown.isGrace) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
		Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

		Bunkers.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Bunkers.getPlugin(), () -> {
			if (player.isDead()) {
				((CraftPlayer) player).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
			}
		});

		PacketContainer lightningPacket = BunkersAPI.createLightningPacket(player.getLocation());

		Random random = new Random();

		float thunderSoundPitch = 0.8F + random.nextFloat() * 0.2F;
		float explodeSoundPitch = 0.5F + random.nextFloat() * 0.2F;

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 10000F, thunderSoundPitch);
			online.playSound(player.getLocation(), Sound.EXPLODE, 2.0F, explodeSoundPitch);

			BunkersAPI.sendLightningPacket(online, lightningPacket);
		}

		if (Information.i == 1) {
			team.setDtr1(team.getDtr1() - 1);
		} else if (Information.i == 2) {
			team.setDtr2(team.getDtr2() - 1);
		}
		profile.setDeaths(profile.getDeaths() + 1);

		if (event.getEntity().getKiller() != null) {
			Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).setKills(Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).getKills() + 1);
			Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).setMatchKills(Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).getMatchKills() + 1);
		}

		if (Information.i == 1) {
			if (team.getDtr1() <= 0) {
				Bunkers.getPlugin().getSpectatorManager().setSpectator(player);
			}
		} else if (Information.i == 2) {
			if (team.getDtr2() <= 0) {
				Bunkers.getPlugin().getSpectatorManager().setSpectator(player);
			}
		}

		for (UUID dead : Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers()) {

			if (Bunkers.getPlugin().getProfileManager().getProfile(dead).getStatus() == PlayerStatus.SPECTATOR) {
				Bunkers.getPlugin().getGameManager().getTeamsLeft().remove(team);
				Bukkit.broadcastMessage(ChatColor.RED + Bunkers.getPlugin().getTeamManager().getByPlayer(player).getName() + " team has been eliminated!");
			}
		}

		if (Bunkers.getPlugin().getGameManager().getTeamsLeft().size() == 1) {
			new GameManager().setWon(Bunkers.getPlugin().getGameManager().getTeamsLeft().listIterator().next());
		}
	}

	@EventHandler
	public void onDmg2(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Villager) {
			if (event.getDamager() instanceof Player) {
				Team team = Bunkers.getPlugin().getTeamManager().getByPlayer((Player) event.getDamager());
				if (team.getCuboid().contains(event.getEntity().getLocation())) {
					event.setCancelled(true);
				}
			} else if (event.getDamager() instanceof Projectile) {
				if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
					Team team = Bunkers.getPlugin().getTeamManager().getByPlayer((Player) ((Projectile) event.getDamager()).getShooter());
					if (team.getCuboid().contains(event.getEntity().getLocation())) {
						event.setCancelled(true);
					}
				}
			}
		} else if (event.getEntity() instanceof Player) {

			if (event.getDamager() instanceof Projectile) {
				if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
					Team team = Bunkers.getPlugin().getTeamManager().getByPlayer((Player) ((Projectile) event.getDamager()).getShooter());
					if (team.getMembers().contains(event.getEntity().getName())) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
			Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);

			if (bunkers.getGameManager().getStatus() == GameStatus.WAITING) {
				team.getMembers().remove(player.getUniqueId());
			} else if (bunkers.getGameManager().getStatus() == GameStatus.PLAYING) {
				if (Information.i == 1) {
					team.setDtr1(team.getDtr1() - 1);
				} else if (Information.i == 2) {
					team.setDtr2(team.getDtr2() - 1);
				}
			} else if (bunkers.getGameManager().getStatus() == GameStatus.STARTING) {
				team.getMembers().remove(player.getUniqueId());
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(LocationUtils.getLocation(Bunkers.getPlugin().getTeamManager().getByPlayer(event.getPlayer()).getSpawnLocation()));
		int noticks = event.getPlayer().getNoDamageTicks();
		event.getPlayer().setNoDamageTicks(20000);
		new BukkitRunnable() {
			@Override
			public void run() {
				event.getPlayer().setNoDamageTicks(noticks);
			}
		}.runTaskLater(Bunkers.getPlugin(), 20 * 5);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
/*
        if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() instanceof Villager){
            Player damager = (Player) ((EntityDamageByEntityEvent) event).getDamager();

            Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(damager);

            if(profile.getStatus().equals(PlayerStatus.SPECTATOR)){
                event.setCancelled(true);
            }
        }*/

		if (Bunkers.getPlugin().getGameManager().getStatus() != GameStatus.PLAYING) {
			event.setCancelled(true);
		} else {
			if (event instanceof EntityDamageByEntityEvent) {
				if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() instanceof Player) {
					Player damaged = (Player) event.getEntity();
					Player damager = (Player) ((EntityDamageByEntityEvent) event).getDamager();

					Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(damager);

					if (profile.getStatus().equals(PlayerStatus.SPECTATOR)) {
						event.setCancelled(true);
						damager.sendMessage(ChatColor.RED + "You cannot hit players while in spectator mode.");
					}

					if (Bunkers.getPlugin().getTeamManager().getByPlayer(damaged) == Bunkers.getPlugin().getTeamManager().getByPlayer(damager)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDrop2(PlayerDropItemEvent event) {
		Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(event.getPlayer());

		if (profile.getStatus().equals(PlayerStatus.SPECTATOR)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot hit players while in spectator mode.");
		}
	}

	@EventHandler
	public void onCLick(InventoryClickEvent event) {
		Profile profile = Bunkers.getPlugin().getProfileManager().getProfile((Player) event.getWhoClicked());

		if (profile.getStatus().equals(PlayerStatus.SPECTATOR)) {
			event.setCancelled(true);
			((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You cannot hit players while in spectator mode.");
		}
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	public void onMotd(ServerListPingEvent event) {
		event.setMotd("Bunkers;" + Bunkers.getPlugin().getGameManager().getStatus().name() + ";" + Bunkers.getPlugin().getKoth().getCapSeconds() + ";" + Bukkit.getOnlinePlayers().size());
		if (Information.i == 1) {
			Bunkers.getPlugin().getInformationManager().getInformation().setMotd1(event.getMotd());
		} else if (Information.i == 2) {
			Bunkers.getPlugin().getInformationManager().getInformation().setMotd2(event.getMotd());
		}
	}
}
