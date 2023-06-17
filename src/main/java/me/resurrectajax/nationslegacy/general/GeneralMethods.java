package me.resurrectajax.nationslegacy.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;
import net.md_5.bungee.api.ChatColor;

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
	 * replaces text with special strings (%Player%, %Date%) with correct values
	 * @param input String to convert
	 * @param special special charactered String
	 * @param value String to replace value with
	 * @return formatted String
	 * */
 	public static String format(CommandSender sender, String input, String... values) {
 		String newStr = input;
 		if(Nations.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof OfflinePlayer) return GeneralMethods.format(PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, input));
 		return getStringWithoutPlaceholderApi(newStr, values);
	}
 	
 	public static String format(OfflinePlayer player, String input, String... values) {
 		String newStr = input;
 		if(Nations.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) return GeneralMethods.format(PlaceholderAPI.setPlaceholders(player, input));
 		return getStringWithoutPlaceholderApi(newStr, values);
	}
 	
 	public static String relFormat(CommandSender sender1, CommandSender sender2, String input, String... values) {
 		String newStr = input;
 		if(Nations.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
 			newStr = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender1, input);
 			return GeneralMethods.format(PlaceholderAPI.setRelationalPlaceholders((Player) sender1, (Player) sender2, newStr));
 		}
 		return getStringWithoutPlaceholderApi(newStr, values);
 	}
 	
 	private static String getStringWithoutPlaceholderApi(String input, String... values) {
 		List<String> formatStrings = getFormatStrings(input);
 		List<String> valueList = Arrays.asList(values);
 		String newStr = input;
 		int counter = 0;
 		for(String format : formatStrings) {
 			if(Nations.getInstance().getFormats().contains(format)) newStr = newStr.replace(String.format("%%%s%%", format), valueList.get(counter));
 			counter++;
 		}
 		
 		return ChatColor.translateAlternateColorCodes('&', newStr);
 	}
 	
 	private static List<String> getFormatStrings(String value) {
 		List<String> matches = new ArrayList<>();
 	    Pattern pattern = Pattern.compile("%(.*?)%"); // Use a regular expression pattern to match text between '%'
 	    Matcher matcher = pattern.matcher(value);
 	    
 	    while (matcher.find()) {
 	        matches.add(matcher.group(1));
 	    }
 	    
 	    return matches;
 	}
 	
 	public static void updatePlayerTab(Player player) {
 		Nations main = Nations.getInstance();
 		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		FileConfiguration config = main.getConfig();
		
		boolean hasPrefix = config.getBoolean("Nations.Prefix.Enabled");
		if(!hasPrefix) return;	
		
		String playerName = player.getDisplayName();
		String total = String.format(config.getString("Nations.Prefix.Format"), nation != null ? nation.getName() : config.getString("Wilderness.Name"), playerName);
		player.setPlayerListName(GeneralMethods.format(total));
 	}
}
