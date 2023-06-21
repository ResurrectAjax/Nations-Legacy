package me.resurrectajax.nationslegacy.events.nation.claim;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class SaveChunksEvent extends NationEvent{

	public SaveChunksEvent(NationMapping nation, CommandSender sender) {
		super(nation, sender);
		
		Nations main = Nations.getInstance();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				nation.saveChunks();
			}
		}, 1L);
	}

}
