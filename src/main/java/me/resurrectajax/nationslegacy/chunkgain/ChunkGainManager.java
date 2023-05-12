package me.resurrectajax.nationslegacy.chunkgain;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import me.resurrectajax.nationslegacy.events.nation.chunkgain.ChunkGainEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class ChunkGainManager {
	
	private Nations main;
	private int interval;
	private HashMap<Integer, Integer> chunkGainTask = new HashMap<>();
	
	public ChunkGainManager(Nations main) {
		this.main = main;
	}
	
	public void addChunkGain(NationMapping nation) {
		FileConfiguration config = main.getConfig();
		
		interval = GeneralMethods.convertHoursMinutesSecondsToSeconds(config.getString("Nations.Claiming.ChunkGain.Interval"));
		int nationID = nation.getNationID();
		int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
			@Override
			public void run() {
				if(nation.getNationID() == null) {
					removeChunkGain(nationID);
					return;
				}
				
				int chunks = config.getInt("Nations.Claiming.ChunkGain.Chunks");
				int limit = config.getInt("Nations.Claiming.ChunkGain.Limit");
				interval = GeneralMethods.convertHoursMinutesSecondsToSeconds(config.getString("Nations.Claiming.ChunkGain.Interval"));
				
				if(nation.getGainedChunks() < limit || limit == -1) Bukkit.getPluginManager().callEvent(new ChunkGainEvent(nation, null, chunks));
				else chunkGainTask.remove(nation.getNationID());
			}
		}, interval*20L, interval*20L);
		
		chunkGainTask.put(nation.getNationID(), task);
	}
	
	public void removeChunkGain(int nationID) {
		if(!chunkGainTask.containsKey(nationID)) return;
		
		Bukkit.getScheduler().cancelTask(chunkGainTask.get(nationID));
		chunkGainTask.remove(nationID);
	}
	
	
}
