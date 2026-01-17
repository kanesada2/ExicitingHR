package com.github.kanesada2.ExcitingHR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.kanesada2.SnowballGame.SnowballGame;
import com.github.kanesada2.SnowballGame.api.SnowballGameAPI;

public final class Util {
	 private Util() {}

	 public static boolean isRecordedBall(ItemStack item){
		 if(!item.hasItemMeta()){
			 return false;
		 }
		 ItemMeta itemMeta = item.getItemMeta();
		 return itemMeta.hasLore() && itemMeta.getLore().contains("Recorded Ball");
	 }


	 public static boolean Replay(Player player, boolean doesRide){
		 if(!(player.hasMetadata("hitLoc") && player.hasMetadata("vx") && player.hasMetadata("vy") && player.hasMetadata("vz") && player.hasMetadata("sx") && player.hasMetadata("sy") && player.hasMetadata("sz"))){
			 player.sendMessage("Please send this command after you hit a ball.");
			 return false;
		 }
		 if(player.getGameMode() != GameMode.CREATIVE){
			 if(!player.getInventory().containsAtLeast(SnowballGameAPI.getBallItem("Normal"),1)){
				 player.sendMessage("You must have at least one normal-ball to send this command.");
				 return false;
			 }
			 	ItemStack[] inventory = player.getInventory().getContents();
				for(ItemStack item : inventory){
					if(item != null && item.isSimilar(SnowballGameAPI.getBallItem("Normal"))){
						item.setAmount(item.getAmount() - 1);
						break;
					}
				}
		 }
		 ExcitingHR plugin = ExcitingHR.getPlugin(ExcitingHR.class);
		 Vector velocity = new Vector(player.getMetadata("vx").get(0).asDouble(), player.getMetadata("vy").get(0).asDouble(), player.getMetadata("vz").get(0).asDouble());
		 double vAngle = Math.toDegrees(velocity.angle(new Vector(player.getMetadata("vx").get(0).asDouble(), 0, player.getMetadata("vz").get(0).asDouble())) * Math.signum(player.getMetadata("vy").get(0).asDouble()));
		 double speed = velocity.length() * 72;
		 Vector spinVector = new Vector(player.getMetadata("sx").get(0).asDouble(), player.getMetadata("sy").get(0).asDouble(), player.getMetadata("sz").get(0).asDouble());
		 Location hitLoc = (Location)player.getMetadata("hitLoc").get(0).value();
		 double acceleration = player.getMetadata("ac").get(0).asDouble();
		 if(hitLoc.distance(player.getLocation()) > 160){
			 player.sendMessage("You are too far to see replay.");
			 return false;
		 }
		 Projectile replay = SnowballGameAPI.launch(player, null, false, "normal", "batted", velocity, spinVector, acceleration, 0, getParticle(plugin.getConfig().getConfigurationSection("Replay")), hitLoc, new Vector(0,0,0));
		 replay.setMetadata("isReplay", new FixedMetadataValue(plugin, true));
		 player.sendMessage("打ち出し角度: " + String.format("%.1f", vAngle) + "度");
		 player.sendMessage("打球速度: " + String.format("%.1f", speed) + "km/h");
		 if(doesRide){
			 player.setCollidable(false);
			 player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
			 replay.addPassenger(player);
		 }
		 return true;
	 }

	 public static void record(String ownerName, String prop, ItemStack ball){
		 String [] props = prop.split("&", 2);
		 String ballName = props[0];
		 BigInteger biProp = new BigInteger(props[1].getBytes());
		 String encoded = Base2441.encode(biProp);
		 ItemMeta meta = ball.getItemMeta();
		 meta.setDisplayName(ownerName + " " + new SimpleDateFormat().format(new Date()) + " " + ballName);
		 List<String> lore = meta.getLore();
		 if(!meta.hasLore()) {
			lore = new ArrayList<String>();
		 }
		 lore.addAll(format4Lore(encoded, 20, "Recorded Ball", ";"));
		 lore.addAll(format4Lore(ballName, 20, "Name:", "&"));
		 meta.setLore(lore);
		 ball.setItemMeta(meta);
	 }

	 public static List<String> format4Lore(String content, int columnNum,  String start, String end){
		 List<String> toAdd = new ArrayList<String>();
		 toAdd.add(start);
		 Matcher m = Pattern.compile("[\\s\\S]{1," + columnNum + "}").matcher(content);
		 while (m.find()) {
		     toAdd.add(m.group());
		 }
		 String last = toAdd.get(toAdd.size() - 1);
		 last += end;
		 toAdd.set(toAdd.size() - 1, last);
		 return toAdd;
	 }

	 public static String extractBySignifier(List<String> lore, String start, String end){
		 int index = lore.indexOf(start) + 1;
		 String target = "";
		 while(lore.size() > index){
			 target += lore.get(index);
			 if(lore.get(index).endsWith(end)){
				 break;
			 }
			 index++;
		 }
		 return target;
	 }

	 public static String calcRotatableValues(Vector original, Vector ref){
		 Vector y0 = original.clone().setY(0);
		 double pitch = original.angle(y0) * 180 / Math.PI * Math.signum(original.getY());
		 double yawSignum = Math.signum(original.getCrossProduct(ref).getY());
		 double yaw = y0.angle(ref) * 180 / Math.PI * yawSignum;
		 if(Double.isNaN(pitch)){
			 pitch = 0;
		 }
		 if(Double.isNaN(yaw)){
			 yaw = 0;
		 }
		 return  original.length() + "," + pitch + "," + yaw;
	 }

	 public static Vector calcVectorFromRotatableVal(Location loc, double length,float pitch, float yaw){
		 loc.setPitch(loc.getPitch() + pitch);
		 loc.setYaw(loc.getYaw() + yaw);
		 return loc.getDirection().normalize().multiply(length);
	 }
	 public static String[] getPropsFromBall(ItemStack ball){
		ItemMeta meta = ball.getItemMeta();
		if(meta == null) return null;
		if(!meta.hasLore()) return null;
		List<String> lore = meta.getLore();
		 String ballName = extractBySignifier(lore, "Name:", "&");
		 String record = extractBySignifier(lore, "Recorded Ball", ";");
		 record = record.substring(0, record.length() -1);
		 String decoded = new String(Base2441.decode(record).toByteArray());
		 return (ballName + decoded).split("&");
	 }

	 public static String deflate(String original){
         byte[] dataByte = Base2441.decode(original).toByteArray();
         Deflater def = new Deflater();
         def.setLevel(Deflater.BEST_COMPRESSION);
         def.setInput(dataByte);
         def.finish();
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(dataByte.length);
         byte[]buf = new byte[2048];
         while(!def.finished()) {
             int compByte = def.deflate(buf);
             byteArrayOutputStream.write(buf, 0, compByte);
         }
         def.end();
         try {
			byteArrayOutputStream.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			def.end();
		}

         byte[] compData = byteArrayOutputStream.toByteArray();
         BigInteger num = new BigInteger(compData);
         return Base2441.encode(num);
	 }

	 public static String inflate(String base2441){
		 BigInteger decoded = Base2441.decode(base2441);
		 byte[] dataByte = decoded.toByteArray();;
		 byte[] buf = new byte[2048];
		 int count = 0;
		 Inflater decompresser = new Inflater();
		  decompresser.setInput(dataByte);
		  ByteArrayOutputStream decompos = new ByteArrayOutputStream();
		  while (!decompresser.finished()) {
			  try {
					count = decompresser.inflate(buf);
			  }catch (DataFormatException e) {
					// TODO 自動生成された catch ブロック
					//e.printStackTrace();
				decompresser.end();
				return "";
			  }
		   decompos.write(buf, 0, count);
		  }
		  decompresser.end();

		  return decompos.toString();
	 }

	 public static Particle getParticle(ConfigurationSection config){
		 Particle particle = null;
		 if(config.contains("Tracker")){
			 try{
				 particle =  Particle.valueOf(config.getString("Tracker"));
			 }catch(IllegalArgumentException e){
				 Bukkit.broadcastMessage("The value of " + config.getCurrentPath() +".Tracker : "+ config.getString("Particle") + " is invalid!!");
			 }
		 }
		 return particle;
	 }
}
