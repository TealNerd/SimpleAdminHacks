package com.programmerdan.minecraft.simpleadminhacks.hacks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Hopper;

import com.programmerdan.minecraft.simpleadminhacks.SimpleAdminHacks;
import com.programmerdan.minecraft.simpleadminhacks.SimpleHack;
import com.programmerdan.minecraft.simpleadminhacks.configs.GameFixesConfig;

public class GameFixes extends SimpleHack<GameFixesConfig> implements Listener {
	public static final String NAME = "GameFixes";
	
	public GameFixes(SimpleAdminHacks plugin, GameFixesConfig config) {
		super(plugin, config);
	}

	@Override
	public void registerListeners() {
		if(config != null && config.isEnabled()) {
			plugin().log("Regsitering GameFixes listeners");
			plugin().registerListener(this);
		}
	}

	@Override
	public void registerCommands() {
	}

	@Override
	public void dataBootstrap() {
	}

	@Override
	public void unregisterListeners() {
		//Bukkit does this for us (why is this a method then?)
	}

	@Override
	public void unregisterCommands() {
	}

	@Override
	public void dataCleanup() {
	}

	@Override
	public String status() {
		StringBuilder genStatus = new StringBuilder();
		genStatus.append("GameFixes is ");
		if(config != null && config.isEnabled()) {
			genStatus.append("active\n");
			if(config.isBlockElytraBreakBug()) {
				genStatus.append("   Block eltra break bug is enabled\n");
				genStatus.append("   Will deal " + config.getDamageOnElytraBreakBug() + " damage to players\n");
			} else {
				genStatus.append("   Block elytra break bug is disabled\n");
			}
		} else {
			genStatus.append("inactive");
		}
		return genStatus.toString();
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if(!player.getLocation().equals(block.getLocation())
				&& player.getEyeLocation().getBlock().getType() != Material.AIR
				&& config.isBlockElytraBreakBug()) {
			event.setCancelled(true);
			player.damage(config.getDamageOnElytraBreakBug());
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onEntityTeleport(EntityTeleportEvent event) {
		if(!config.canStorageTeleport() && event.getEntity() instanceof InventoryHolder) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		if(!config.isStopHopperDupe() || !(event.getDestination().getType() == InventoryType.HOPPER)
				|| !(event.getSource().getType() == InventoryType.HOPPER)) return;
		Hopper source = (Hopper) event.getSource().getLocation().getBlock().getState().getData();
		Hopper dest = (Hopper) event.getDestination().getLocation().getBlock().getState().getData();
		if(source.getFacing().getOppositeFace() == dest.getFacing()) {
			//They're pointing into each other and will eventually dupe
			event.setCancelled(true);
		}
		
	}
	
	public static GameFixesConfig generate(SimpleAdminHacks plugin, ConfigurationSection config) {
		return new GameFixesConfig(plugin, config);
	}
}
