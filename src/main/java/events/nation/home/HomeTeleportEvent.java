package events.nation.home;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.NationMapping;

public class HomeTeleportEvent extends NationEvent{

	private String nationHome;
	private Location homeLocation;
	private int teleportDelay;
	private int loops = 0;
	private int task = -1;
	
	public HomeTeleportEvent(NationMapping nation, CommandSender sender, String home, Location homeLoc) {
		super(nation, sender);
		this.nationHome = home;
		setTeleportLocation(homeLoc);
		
		Main main = Main.getInstance();
		FileConfiguration config = main.getConfig(), language = main.getLanguage();
		
		setTeleportDelay(GeneralMethods.convertHoursMinutesSecondsToSeconds(config.getString("Nations.Home.Delay")));
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled || !(sender instanceof Entity)) return;
				Entity entity = (Entity) sender;
				Location startLoc = entity.getLocation().getBlock().getLocation();
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Home.Home.Teleporting.Message"), home));
				
				task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
					
					@Override
					public void run() {
						loops++;
						Location newLoc = entity.getLocation().getBlock().getLocation();
						
						if(!startLoc.equals(newLoc)) {
							Bukkit.getScheduler().cancelTask(task);
							sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Home.Home.Cancelled.Message"), home));
							return;
						}
						if(loops < teleportDelay) return;
						entity.teleport(homeLocation);
						Bukkit.getScheduler().cancelTask(task);
					}
				}, 0L, 20L);
			}
		}, 1L);
		
		// TODO Auto-generated constructor stub
	}
	
	public int getTeleportDelay() {
		return teleportDelay;
	}

	public void setTeleportDelay(int teleportDelay) {
		this.teleportDelay = teleportDelay;
	}

	public Location getTeleportLocation() {
		return homeLocation;
	}

	public void setTeleportLocation(Location homeLoc) {
		this.homeLocation = homeLoc;
	}

	public String getNationHome() {
		return nationHome;
	}

}
