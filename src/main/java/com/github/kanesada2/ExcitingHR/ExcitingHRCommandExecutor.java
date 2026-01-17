package com.github.kanesada2.ExcitingHR;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.kanesada2.SnowballGame.api.SnowballGameAPI;

public class ExcitingHRCommandExecutor implements CommandExecutor, TabCompleter {

	private ExcitingHR plugin;

    public ExcitingHRCommandExecutor(ExcitingHR plugin) {
        this.plugin = plugin;
    }
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("ExcitingHR")) {
            return null;
        }
		ArrayList<String> completions = new ArrayList<String>();
		if(args.length == 1){
			if (args[0].length() == 0) {
	            completions.add("replay");
	            completions.add("reroad");
	            //completions.add("record");
	            //completions.add("extract");
	            //completions.add("infuse");
			}else {
	            if ("replay".startsWith(args[0])) {
	                completions.add("replay");
	            }
	            if ("reroad".startsWith(args[0])) {
	                completions.add("reroad");
	            }
	            /*if ("record".startsWith(args[0])) {
	                completions.add("record");
	            }
	            if ("extract".startsWith(args[0])) {
	                completions.add("extract");
	            }
	            if ("infuse".startsWith(args[0])) {
	                completions.add("infuse");
	            }*/
	        }
		}else if(args.length == 2){
			if(false && args[0].equalsIgnoreCase("infuse") && sender instanceof Player){
				Player player = (Player)sender;
				if(player.hasMetadata("extracted-props")){
					completions.add(player.getMetadata("extracted-props").get(0).asString());
				}
			}else{
				if (args[1].length() == 0) {
		            completions.add("true");
		            completions.add("false");
				}else {
		            if ("true".startsWith(args[1])) {
		                completions.add("true");
		            }else if ("false".startsWith(args[1])) {
		                completions.add("false");
		            }
		        }
			}
		}
		return completions;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("ExcitingHR")) {
            return false;
        }
		switch(args.length){
			case 0:
				String [] msgs = new String[2];
				msgs[0] = "/ehr replay <true/false> " + ChatColor.YELLOW + " Replay <player>'s most recent batting.";
				msgs[1] = "/ehr reload " + ChatColor.YELLOW + " reload ExcitingHR's config file. Only from console.";
				//msgs[2] = "/ehr record " + ChatColor.YELLOW + " record your most recent pitching to the ball in your main hand.";
				//msgs[3] = "/ehr extract" + ChatColor.YELLOW + " extract the record from the ball in your hand.";
				//msgs[4] = "/ehr infuse [record]" + " infuse the extracted record to the ball in your hand.";
				sender.sendMessage(msgs);
				return true;
			case 1:
				if(sender instanceof ConsoleCommandSender){
					if(args[0].equalsIgnoreCase("reload")){
						plugin.reloadConfig();
						Bukkit.getLogger().info("Config Reloaded!");
						return true;
					}
				}else if(sender instanceof Player){
					if(args[0].equalsIgnoreCase("replay")){
						Player player = (Player)sender;
						if(!player.hasPermission("ExcitingHR.replay")){
							sender.sendMessage("You don't have permission.");
							return false;
						}
						return Util.Replay(player, false);
					}/*else if(args[0].equalsIgnoreCase("record")){
						Player player = (Player)sender;
						if(!player.hasPermission("ExcitingHR.record")){
							sender.sendMessage("You don't have permission.");
							return false;
						}
						if(!player.hasMetadata("recordedPitch")){
							sender.sendMessage("Please send this command after you throw a ball near a umpire.");
							return false;
						}
						ItemStack hand = player.getInventory().getItemInMainHand();
						if(!SnowballGameAPI.isBallItem(hand)){
							sender.sendMessage("Please send this command while holding a ball in your main hand.");
							return false;
						}
						if(Util.isRecordedBall(hand)){
							sender.sendMessage("This ball aleady contains a record.");
							return false;
						}
						Util.record(player.getDisplayName(), player.getMetadata("recordedPitch").get(0).asString(), hand);
						return true;
					}else if(args[0].equalsIgnoreCase("extract")){
						Player player = (Player)sender;
						if(!player.hasPermission("ExcitingHR.extract")){
							sender.sendMessage("You don't have permission.");
							return false;
						}

						ItemStack hand = player.getInventory().getItemInMainHand();
						if(!SnowballGameAPI.isBallItem(hand)){
							sender.sendMessage("Please send this command while holding a ball in your main hand.");
							return false;
						}
						if(!Util.isRecordedBall(hand)){
							sender.sendMessage("This ball doesn't contains a record.");
							return false;
						}
						if(player.hasMetadata("extracted-props")){
							player.removeMetadata("extracted-props", plugin);
						}
						List<String> lore = hand.getItemMeta().getLore();
						String record = Util.extractBySignifier(lore, "Recorded Ball", ";");
						record = record.substring(0, record.length() -1);
						String deflated = Util.deflate(record);
						player.setMetadata("extracted-props", new FixedMetadataValue(plugin, deflated));
						sender.sendMessage("Successfully extracted! Please type "+ ChatColor.BOLD +  "/ehr infuse " + ChatColor.RESET + "then press TAB to copy it.");
						return true;
					}*/else{
						sender.sendMessage("Unknown command. Please check /ehr");
						return false;
					}
				}
			case 2:
				if(!(sender instanceof Player)){
					Bukkit.getLogger().info("Please send this command in game.");
					return false;
				}else{
					Player player = (Player)sender;
					if(args[0].equalsIgnoreCase("replay")){
						if(!player.hasPermission("ExcitingHR.replay")){
							sender.sendMessage("You don't have permission.");
							return false;
						}
						boolean ride = false;
						if(args[1].equalsIgnoreCase("true")){
							ride = true;
						}
						return Util.Replay(player, ride);
					}/*else if(args[0].equalsIgnoreCase("infuse")){
						if(!player.hasPermission("ExcitingHR.infuse")){
							sender.sendMessage("You don't have permission.");
							return false;
						}

						ItemStack hand = player.getInventory().getItemInMainHand();
						if(!SnowballGameAPI.isBallItem(hand)){
							sender.sendMessage("Please send this command while holding a ball in your main hand.");
							return false;
						}
						if(Util.isRecordedBall(hand)){
							sender.sendMessage("This ball aleady contains a record.");
							return false;
						}
						String prop = Util.inflate(args[1]);
						if(prop.equals("")){
							sender.sendMessage("Invalid record. Please check the format.");
							return false;
						}
						prop = "Infused&" + prop;
						Util.record(player.getDisplayName(), prop, hand);
						sender.sendMessage("Successfully infused! Now this ball is contains the record!");
						return true;
					}*/else{
						sender.sendMessage("Unknown command. Please check /ehr");
						return false;
					}
				}
			default:
				sender.sendMessage("Unknown command. Please check /ehr");
				return false;
		}
	}

}
