package com.aurapvp.bunkers.utils.tab;

import org.bukkit.Bukkit;

final class TabThread extends Thread {

	TabThread() {
		super("Bunkers - Tab Thread");

		setDaemon(false);
	}

	@Override
	public void run() {
		while(true) {
			// TODO When add to Cobalt setup this
		    Bukkit.getOnlinePlayers().forEach(player -> {
                try {
                    TabManager.updatePlayer(player);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });

		    try {
				Thread.sleep(TabManager.getUpdateInterval() * 50L);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
