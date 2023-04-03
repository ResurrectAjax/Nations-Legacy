package general;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import main.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

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
 		if(Main.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
 			newStr = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender1, input);
 			return GeneralMethods.format(PlaceholderAPI.setRelationalPlaceholders((Player) sender1, (Player) sender2, newStr));
 		}
 		for(String format : Main.getInstance().getFormats()) {
 			if(input.contains(format)) newStr = input.replaceAll(format, value);
 		}
 		return ChatColor.translateAlternateColorCodes('&', newStr);
 	}
 	
 	public static void updatePlayerTab(Player player) {
 		Main main = Main.getInstance();
 		MappingRepository mappingRepo = main.getMappingRepo();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		FileConfiguration config = main.getConfig();
		
		boolean hasPrefix = config.getBoolean("Nations.Prefix.Enabled");
		if(!hasPrefix) return;
		
		String playerName = player.getDisplayName();
		String total = String.format(config.getString("Nations.Prefix.Format"), nation != null ? nation.getName() : "&2Wilderness", playerName);
		player.setPlayerListName(GeneralMethods.format(total));
 	}
}
