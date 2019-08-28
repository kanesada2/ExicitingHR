package com.github.kanesada2.ExcitingHR;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BatThrowTask extends BukkitRunnable {
	private ArmorStand dummy;
	private ItemStack bat;
	double count = 0;
	public BatThrowTask(ArmorStand dummy, ItemStack bat){
		this.dummy = dummy;
		this.bat = bat;
	}
	@Override
	public void run() {
		Location loc = dummy.getLocation();
    	loc.setPitch(loc.getPitch() + 45);
    	loc.setYaw(loc.getYaw() + 45);
    	dummy.teleport(loc);
    	if(loc.add(0, -0.1, 0).getBlock().getType() != Material.AIR){
    		dummy.remove();
    		loc.getWorld().dropItem(loc, bat);
    		this.cancel();
    	}
	}

}
