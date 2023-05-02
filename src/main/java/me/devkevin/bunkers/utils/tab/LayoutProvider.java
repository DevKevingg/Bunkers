package me.devkevin.bunkers.utils.tab;

import org.bukkit.entity.Player;

public interface LayoutProvider {

	/**
	 * This function will return us our tablist.
	 *
	 * @param player
	 * @return
	 */
	TabLayout getLayout(Player player);

	/**
	 * This function will return us our header
	 *
	 * @return
	 */
	String getHeader();

	/**
	 * This function will return us our footer
	 *
	 * @return
	 */
	String getFooter();
}
