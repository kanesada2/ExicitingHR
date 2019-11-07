package com.github.kanesada2.ExcitingHR;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BatThrowTask extends BukkitRunnable {
	private ArmorStand dummy;
	private ItemStack bat;
	private Vector velocity;
	double count = 0;
	public BatThrowTask(ArmorStand dummy, ItemStack bat){
		this.dummy = dummy;
		this.bat = bat;
		this.velocity = dummy.getVelocity();
	}
	@Override
	public void run() {
		velocity.add(new Vector(0, -0.16, 0));
		Location loc = dummy.getLocation();
    	loc.setPitch(loc.getPitch() + 45);
		loc.setYaw(loc.getYaw() + 45);
		dummy.teleport(loc.add(velocity));
		count++;
    	if(count > 100 || loc.add(0, -0.1, 0).getBlock().getType() != Material.AIR){
    		dummy.remove();
    		loc.getWorld().dropItem(loc, bat);
    		this.cancel();
		}
	}

}
