package com.github.kanesada2.ExcitingHR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.github.kanesada2.SnowballGame.api.BallThrownEvent;
import com.github.kanesada2.SnowballGame.api.PlayerHitBallEvent;
import com.github.kanesada2.SnowballGame.api.PlayerThrowBallEvent;
import com.github.kanesada2.SnowballGame.api.SnowballGameAPI;

public class ExcitingHRListener implements Listener {
	private ExcitingHR plugin;

	public ExcitingHRListener(ExcitingHR plugin) {
        this.plugin = plugin;
    }
	@EventHandler(priority = EventPriority.LOW)
	public void onHit(PlayerHitBallEvent event){
		Player player = event.getPlayer();
		if(player.hasMetadata("hitLoc") && player.hasMetadata("vx") && player.hasMetadata("vy") && player.hasMetadata("vz") && player.hasMetadata("sx") && player.hasMetadata("sy") && player.hasMetadata("sz")){
			player.removeMetadata("ac", plugin);
			player.removeMetadata("vx", plugin);
			player.removeMetadata("vy", plugin);
			player.removeMetadata("vz", plugin);
			player.removeMetadata("sx", plugin);
			player.removeMetadata("sy", plugin);
			player.removeMetadata("sz", plugin);
			player.removeMetadata("hitLoc", plugin);
		}
		player.setMetadata("hitLoc", new FixedMetadataValue(plugin,event.getBeforeHit().getLocation()));
		player.setMetadata("ac", new FixedMetadataValue(plugin,event.getAcceleration()));
		player.setMetadata("vx", new FixedMetadataValue(plugin,event.getVelocity().getX()));
		player.setMetadata("vy", new FixedMetadataValue(plugin,event.getVelocity().getY()));
		player.setMetadata("vz", new FixedMetadataValue(plugin,event.getVelocity().getZ()));
		player.setMetadata("sx", new FixedMetadataValue(plugin,event.getSpinVector().getX()));
		player.setMetadata("sy", new FixedMetadataValue(plugin,event.getSpinVector().getY()));
		player.setMetadata("sz", new FixedMetadataValue(plugin,event.getSpinVector().getZ()));
		double time = (200 * event.getVelocity().getY() - 3) / 3;
		double initial = event.getVelocity().clone().setY(0).length();
		double estimated = (initial * (1 - Math.pow(0.99, time))) / 0.01;
		Vector criteria = event.getBeforeHit().getVelocity().clone();
		double hAngle = Math.toDegrees(criteria.setY(0).multiply(-1).angle(event.getVelocity().clone().setY(0)));
		double vAngle = event.getVelocity().clone().setY(0).angle(event.getVelocity()) * Math.signum(event.getVelocity().getY()) * 57.2958;
		if(vAngle > plugin.getConfig().getDouble("Min_Vertical_Angle", 22) && vAngle < plugin.getConfig().getDouble("Max_Vertical_Angle", 48) && estimated > plugin.getConfig().getDouble("Min_Flight_Distance", 125) && !Double.isNaN(hAngle) && hAngle < plugin.getConfig().getDouble("Max_Horizontal_Angle", 40)){
			List <String> msgs = plugin.getConfig().getStringList("MsgList");
			String msg = msgs.get((int)Math.floor(Math.random() * msgs.size()));
			int range = plugin.getConfig().getInt("Range");
			if(range > 0){
				 range *= range;
		         Location location = player.getLocation();
		         List<Player> players = player.getWorld().getPlayers();
		         	for (Player toSend : players) {
		         		if (location.distanceSquared(toSend.getLocation()) <= range) {
		         			toSend.sendTitle(msg, "", 10, 80, 20);
		                }
		            }
			}
			player.getWorld().spawnParticle(Particle.TOTEM, event.getBeforeHit().getLocation(), 5, 2, 2, 2);
			FireworkEffect effect = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.RED).withFade(Color.ORANGE).with(FireworkEffect.Type.BALL).build();
			for(int i=0;i < 5;i++){
				Firework firework = (Firework)player.getWorld().spawnEntity(event.getBeforeHit().getLocation().add(Vector.getRandom().subtract(Vector.getRandom()).multiply(10)), EntityType.FIREWORK);
				FireworkMeta meta = firework.getFireworkMeta();
				switch(i){
				case 1:
					meta.addEffect(FireworkEffect.builder().trail(false).flicker(false).withColor(Color.BLUE).withFade(Color.GREEN).with(FireworkEffect.Type.BALL).build());
					break;
				case 2:
					meta.addEffect(FireworkEffect.builder().trail(false).flicker(true).withColor(Color.ORANGE).withFade(Color.WHITE).with(FireworkEffect.Type.BALL).build());
					break;
				case 3:
					meta.addEffect(FireworkEffect.builder().trail(false).flicker(true).withColor(Color.PURPLE).withFade(Color.YELLOW).with(FireworkEffect.Type.BALL).build());
					break;
				case 4:
					FireworkEffect.builder().trail(false).flicker(true).withColor(Color.SILVER).withFade(Color.RED).with(FireworkEffect.Type.BALL).build();
					break;
				}
				meta.addEffect(effect);
				firework.setFireworkMeta(meta);
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onExit(EntityDismountEvent event){
		if(!(event.getEntity() instanceof Player && event.getDismounted().hasMetadata("isReplay"))){
			return;
		}
		Player player = (Player)event.getEntity();
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.setCollidable(true);
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPerform(PlayerDropItemEvent event){
		if(!Util.isBat(event.getItemDrop().getItemStack())){
			return;
		}
		event.getItemDrop().remove();
		Player player = event.getPlayer();
		ArmorStand dummy = (ArmorStand)player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ARMOR_STAND);
		dummy.setVisible(false);
		dummy.setItemInHand(Util.getBat());
		dummy.setMarker(true);
		Location eyeLoc = player.getEyeLocation();
		if(player.getMainHand() == MainHand.RIGHT){
			eyeLoc.setYaw(eyeLoc.getYaw() - 60);
		}else{
			eyeLoc.setYaw(eyeLoc.getYaw() + 60);
		}
		Vector velocity = eyeLoc.getDirection().normalize().add(new Vector(0, 0.2, 0));
		velocity.add(new Vector(0, 0.2, 0));
		dummy.setVelocity(velocity);
		new BatThrowTask(dummy,event.getItemDrop().getItemStack()).runTaskTimer(plugin, 0, 1);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onRecordThrow(PlayerThrowBallEvent e){
		Player player = e.getPlayer();
		if(Util.isRecordedBall(e.getItemBall())){
			player.sendMessage("Recorded ball is only for dispensers. You can't throw it.");
			e.setCancelled(true);
			return;
		}
		if(player.hasMetadata("recordedPitch")){
			player.removeMetadata("recordedPitch", plugin);
		}
		Collection<Entity> entities = e.getPlayer().getNearbyEntities(50, 10, 50);
		List<Entity> umpires = new ArrayList<Entity>();
		for (Entity entity : entities) {
			if(entity instanceof ArmorStand && com.github.kanesada2.SnowballGame.Util.isUmpire(((ArmorStand)entity).getBoots())){
				umpires.add(entity);
			}
		}
		if(umpires.isEmpty()){
			return;
		}
		double closest = 80;
		Entity target = null;
		for(Entity umpire : umpires){
			double dist = umpire.getLocation().distance(player.getLocation());
			if (closest == 80 || dist < closest){
				closest = dist;
				target = umpire;
			}
		}
		Location playerfloor = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		Location targetLoc = target.getLocation();
		Vector refVector = targetLoc.clone().subtract(playerfloor.add(0.5,0,0.5)).toVector().setY(0);
		Vector u2rp = targetLoc.clone().subtract(e.getRPoint()).toVector();

		String ballName = e.getBallName().replaceAll("&", "<and>");

		String jsonStr = ballName + "&"
					+ Util.calcRotatableValues(u2rp, refVector) + "&"
					+ Util.calcRotatableValues(e.getVelocity(), refVector) + "&"
					+ Util.calcRotatableValues(e.getSpinVector(), refVector) + "&"
					+ Util.calcRotatableValues(e.getVModifier(), refVector) + "&"
					+ e.getAcceleration() + "&"
					+ e.getRandom() + "&"
					+ e.getTracker() + "&"
					+ plugin.getConfig().getString("Server_Identifier");
		player.setMetadata("recordedPitch", new FixedMetadataValue(plugin, jsonStr));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDispense(BlockDispenseEvent event){
		if(!(event.getBlock().getType() == Material.DISPENSER && com.github.kanesada2.SnowballGame.Util.isBall(event.getItem()) && Util.isRecordedBall(event.getItem()))){
			return;
		}
		String[] prop = Util.getPropsFromBall(event.getItem());
		if(prop == null){
			return;
		}
		Dispenser from = (Dispenser)event.getBlock().getState();
		from.setMetadata("ballProp", new FixedMetadataValue(plugin, prop));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDispenserThrow(BallThrownEvent event){
		if(event.getEntity().hasMetadata("isRecorded")){
			return;
		}
		Projectile projectile = (Projectile)event.getEntity();
		if(!(projectile.getShooter() instanceof BlockProjectileSource)){
			return;
		}
		BlockProjectileSource source = (BlockProjectileSource)projectile.getShooter();
		Block from = source.getBlock();

		if(!from.hasMetadata("ballProp")){
			return;
		}

		Collection<Entity> entities = projectile.getNearbyEntities(50, 10, 50);
		List<Entity> umpires = new ArrayList<Entity>();
		for (Entity entity : entities) {
			if(entity instanceof ArmorStand && com.github.kanesada2.SnowballGame.Util.isUmpire(((ArmorStand)entity).getBoots())){
				umpires.add(entity);
			}
		}
		if(umpires.isEmpty()){
			return;
		}
		double closest = 80;
		Entity target = null;
		for(Entity umpire : umpires){
			double dist = umpire.getLocation().distance(projectile.getLocation());
			if (closest == 80 || dist < closest){
				closest = dist;
				target = umpire;
			}
		}
		Location targetLoc = target.getLocation();
		Location dispenserfloor = new Location(from.getWorld(), from.getLocation().getBlockX(), from.getLocation().getBlockY(), from.getLocation().getBlockZ());
		projectile.remove();
		Vector u2p = targetLoc.clone().subtract(dispenserfloor.add(0.5,0,0.5)).toVector().multiply(-1);
		double yawSignum = Math.signum(u2p.getCrossProduct(targetLoc.getDirection()).getY());
		if(yawSignum == 0){
			yawSignum = 1;
		}
		targetLoc.setYaw((float) (targetLoc.getDirection().angle(u2p.clone().setY(0)) * 180 / Math.PI * yawSignum));

		String[] prop = (String[])from.getMetadata("ballProp").get(0).value();
		from.removeMetadata("ballProp", plugin);

		String ballName = prop[0].replaceAll("<and>", "&");
		if(!prop[8].equalsIgnoreCase(plugin.getConfig().getString("Server_Identifier"))){
			ballName += "(Other Server)";
		}
		String[] rpValues = prop[1].split(",");
		Vector t2rp = Util.calcVectorFromRotatableVal(targetLoc.clone(), Double.valueOf(rpValues[0]), Float.valueOf(rpValues[1]), Float.valueOf(rpValues[2]));
		Location rp = targetLoc.clone().add(t2rp);
		String[] velValues = prop[2].split(",");
		Vector velocity = Util.calcVectorFromRotatableVal(targetLoc.clone(), Double.valueOf(velValues[0]), Float.valueOf(velValues[1]), Float.valueOf(velValues[2])).multiply(-1);
		String[] spinValues = prop[3].split(",");
		Vector spin = Util.calcVectorFromRotatableVal(targetLoc.clone(), Double.valueOf(spinValues[0]), Float.valueOf(spinValues[1]), Float.valueOf(spinValues[2])).multiply(-1);
		String[] vModValues = prop[4].split(",");
		Vector vModifier = Util.calcVectorFromRotatableVal(targetLoc.clone(), Double.valueOf(vModValues[0]), Float.valueOf(vModValues[1]), Float.valueOf(vModValues[2])).multiply(-1);
		double acceleration = Double.valueOf(prop[5]);
		double random = Double.valueOf(prop[6]);
		Particle tracker = null;
		if(!prop[7].equalsIgnoreCase("null")){
			tracker = Particle.valueOf(prop[7]);
		}

		Projectile ball = SnowballGameAPI.launch(source, null, true, from.getMetadata("ballType").get(0).asString(), ballName, velocity, spin, acceleration, random, tracker, rp, vModifier);
		ball.setMetadata("isRecorded", new FixedMetadataValue(plugin, true));
	}
}
