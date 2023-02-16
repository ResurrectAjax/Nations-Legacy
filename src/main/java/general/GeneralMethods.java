package general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import main.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

/**
 * Class of general methods that can be used everywhere
 * 
 * @author ResurrectAjax
 * */
public class GeneralMethods extends me.resurrectajax.ajaxplugin.general.GeneralMethods{
	/**
     * pad a string to center with special characters
     * @param str - String to center
     * @param pad - Padding amount
     * @param size - total size
     * @return center padded string
     */
	public static String padCenter(String str, char pad, int size) {
		if (str == null || size <= str.length())
            return str;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - str.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(str);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
	}
	
	/**
	 * turn text into hover text
	 * @param string base text to put hover text on
	 * @param hover the hover text
	 * @param command command that will be activated on click... <i>Nullable</i>
	 * @param color the color of the base text and hover text
	 * @return String message
	 * */
	public static TextComponent createHoverText(String string, String hover, String command, ChatColor color) {
		TextComponent message = new TextComponent(string);
		
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(color + hover)));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		
		message.setBold(true);
		message.setColor(color);
		
		
		return message;
	}
		
	/**
	 * check if a string is a valid date
	 * @param date String to check
	 * @return boolean
	 * */
	public boolean isValidDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(ChatColor.stripColor(date).trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
	}
	
	/**
	 * check if string is an Integer
	 * @param input String to check
	 * @return boolean
	 * */
	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
		}
		catch(Exception ex) {
			return false;
		}
		return true;
	}
	
	/**
	 * gets all numeric values from String
	 * @param input String to retreive values from
	 * @return array of Integer values
	 * */
	public static Integer[] getIntFromString(String input) {
		String nStr = ChatColor.stripColor(input);
		String[] splitStr = nStr.split("[\\D]");
		Integer[] splitNumbers = new Integer[splitStr.length];
		for(int i = 0; i < splitNumbers.length; i++) {
			splitNumbers[i] = Integer.parseInt(splitStr[i]);
		}
		return splitNumbers;
	}
	
	/**
	 * replaces the color coded text with a colored text
	 * @param msg String to format
	 * @return colored text
	 * */
	public static String format(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
 	
	/**
	 * replaces text with special strings (%Player%, %Date%) with correct values
	 * @param input String to convert
	 * @param special special charactered String
	 * @param value String to replace value with
	 * @return formatted String
	 * */
 	public static String format(CommandSender sender, String input, String value) {
 		String newStr = input;
 		if(Main.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof OfflinePlayer) return GeneralMethods.format(PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, input));
 		for(String format : Main.getInstance().getFormats()) {
 			if(input.contains(format)) newStr = input.replaceAll(format, value);
 		}
 		return ChatColor.translateAlternateColorCodes('&', newStr);
	}
 	
 	public static String format(OfflinePlayer player, String input, String value) {
 		String newStr = input;
 		if(Main.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) return GeneralMethods.format(PlaceholderAPI.setPlaceholders(player, input));
 		for(String format : Main.getInstance().getFormats()) {
 			if(input.contains(format)) newStr = input.replaceAll(format, value);
 		}
 		return ChatColor.translateAlternateColorCodes('&', newStr);
	}
 	
 	public static String relFormat(CommandSender sender1, CommandSender sender2, String input, String value) {
 		String newStr = input;
 		if(Main.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && sender1 instanceof Player && sender2 instanceof Player) {
 			newStr = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender1, input);
 			return GeneralMethods.format(PlaceholderAPI.setRelationalPlaceholders((Player) sender1, (Player) sender2, newStr));
 		}
 		for(String format : Main.getInstance().getFormats()) {
 			if(input.contains(format)) newStr = input.replaceAll(format, value);
 		}
 		return ChatColor.translateAlternateColorCodes('&', newStr);
 	}
 	
 	public static String getBadSyntaxMessage(String syntax) {
 		return GeneralMethods.formatCustom(Main.getInstance().getLanguage().getString("Command.Error.BadSyntax.Message"), "%rel_relnations_syntax%", syntax);
 	}
 	
 	private static String formatCustom(String input, String special, String value) {
 		String newStr = input;
 		if(input.contains(special)) newStr = input.replaceAll(special, value);
 		return GeneralMethods.format(newStr);
 	}
 	
 	/**
 	 * checks if a location is within the bounds of 2 other locations
 	 * @param loc location to check
 	 * @param bound1 first bound
 	 * @param bound2 second bound
 	 * @return boolean
 	 * */
 	public boolean isInBounds(Location loc, Location bound1, Location bound2) {
		
		double posXmax = 0, posZmax = 0, posXmin = 0, posZmin = 0;
		if(bound1.getBlockX() > bound2.getBlockX()) {
			posXmax = bound1.getBlockX();
			posXmin = bound2.getBlockX();
		}
		else {
			posXmax = bound2.getBlockX();
			posXmin = bound1.getBlockX();
		}
		if(bound1.getBlockZ() > bound2.getBlockZ()) {
			posZmax = bound1.getBlockZ();
			posZmin = bound2.getBlockZ();
		}
		else {
			posZmax = bound2.getBlockZ();
			posZmin = bound1.getBlockZ();
		}
		
		Vector vector = new org.bukkit.util.Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
				vector1 = new org.bukkit.util.Vector(posXmin, 0, posZmin),
				vector2 = new org.bukkit.util.Vector(posXmax, 255, posZmax);
		
		if(vector.isInAABB(vector1, vector2)) {
       		return true;
		}
		
    	return false;
 	}
 	
 	/**
 	 * gets a list of all the PotionEffects specified in a configuration section
 	 * @param section ConfigurationSection where PotionEffects are listed
 	 * @return list of PotionEffects
 	 * */
 	public List<PotionEffect> getPotionEffects(ConfigurationSection section) {
		List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
		for(String effect : section.getKeys(false)) {
			int amplifier = section.getInt(effect + ".Amplifier"),
				duration = convertHoursMinutesSecondsToSeconds(section.getString(effect + ".Duration"))*20;
			
			PotionType type = PotionType.valueOf(effect);
			potionEffects.add(new PotionEffect(type.getEffectType(), duration, amplifier-1));
		}
		return potionEffects;
	}
 	
 	/**
 	 * converts a string in format(hh:mm:ss) to seconds
 	 * @param input String entered in format to convert
 	 * @return int total in seconds
 	 * */
 	public static int convertHoursMinutesSecondsToSeconds(String input) {
		HashMap<String, Integer> hourminsec = new HashMap<String, Integer>();
		String[] numbers = input.split("\\D");
		String[] letters = input.replaceAll("\\d", "").split("");
		for(int i = 0; i < letters.length; i++) {
			hourminsec.put(letters[i], Integer.parseInt(numbers[i]));
		}
		
		Integer hours = hourminsec.get("h"), 
				minutes = hourminsec.get("m"), 
				seconds = hourminsec.get("s");
		
		if(hours == null && minutes == null && seconds == null) {
			throw new IllegalArgumentException("Please use the right time formats(h|m|s): config.yml");
		}
		else {
			if(hours == null) {
				hours = 0;
			}
			if(minutes == null) {
				minutes = 0;
			}
			if(seconds == null) {
				seconds = 0;
			}	
		}

		int totalseconds = (hours*3600) + (minutes*60) + seconds;
		return totalseconds;
	}
 	
 	/**
 	 * gets the head of a player as an ItemStack
 	 * @param uuid players Unique User ID
 	 * @param displayName String that will be the ItemStacks display name
 	 * @param lore lore list
 	 * @return ItemStack playerhead
 	 * */
 	public static ItemStack getPlayerHead(UUID uuid, String displayName, List<String> lore) {
		boolean isNewVersion = Arrays.stream(Material.values())
				.map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		
		Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
		ItemStack item = new ItemStack(type, 1);
		
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		
		meta.setDisplayName(GeneralMethods.format(displayName));
		
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
 	/**
 	 * gets the head of a player as an ItemStack, can only be used for page monitoring in gui's
 	 * @param name the name of the head you want('MHF_ArrowLeft' and 'MHF_ArrowRight')
 	 * @return ItemStack playerhead
 	 * */
 	public static ItemStack getPlayerHead(String name) {
		boolean isNewVersion = Arrays.stream(Material.values())
				.map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		
		Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
		ItemStack item = new ItemStack(type, 1);
		
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
		
		List<String> lore = new ArrayList<String>();
		switch(name) {
			case "MHF_ArrowLeft":
				meta.setDisplayName(GeneralMethods.format("&6&lBack"));
				lore.add(GeneralMethods.format("&7Go to the previous page"));
				break;
			case "MHF_ArrowRight":
				meta.setDisplayName(GeneralMethods.format("&6&lNext"));
				lore.add(GeneralMethods.format("&7Go to the next page"));
				break;
		}
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
}
