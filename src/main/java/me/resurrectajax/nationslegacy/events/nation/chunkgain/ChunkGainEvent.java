package me.resurrectajax.nationslegacy.events.nation.chunkgain;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class ChunkGainEvent extends NationEvent{

	private int chunkAmount;
	
	public ChunkGainEvent(NationMapping nation, CommandSender sender, int addedChunks) {
		super(nation, sender);
		
		this.chunkAmount = addedChunks;
		
		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				int gainedChunks = nation.getGainedChunks()+chunkAmount;
				int maxChunks = nation.getMaxChunks()+chunkAmount;
				nation.setGainedChunks(gainedChunks);
				nation.setMaxChunks(maxChunks);
				nation.update();
			}
		}, 1L);
	}

	public int getChunkAmount() {
		return chunkAmount;
	}

	public void setChunkAmount(int chunkAmount) {
		this.chunkAmount = chunkAmount;
	}

}
