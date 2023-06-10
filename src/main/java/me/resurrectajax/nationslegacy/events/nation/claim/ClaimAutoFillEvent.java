package me.resurrectajax.nationslegacy.events.nation.claim;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.algorithms.FillAlgorithm;
import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class ClaimAutoFillEvent extends NationEvent{

	private List<Chunk> claimedChunks;
	
	public ClaimAutoFillEvent(NationMapping nation, CommandSender sender, Chunk startChunk) {
		super(nation, sender);
		
		Player player = (Player) sender;
		
		Nations main = Nations.getInstance();
		
		FillAlgorithm algorithm = new FillAlgorithm(main, nation, startChunk);
		this.claimedChunks = new ArrayList<>(algorithm.fillSquareOutline());
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				if(claimedChunks.size() > nation.getMaxChunks()-nation.getClaimedChunks().size()) {
					//error message
					return;
				}
				for(Chunk claim : claimedChunks) {
					Bukkit.getPluginManager().callEvent(new ClaimChunkEvent(nation, player, claim));
				}
			}
		}, 1L);
	}
	
	public void setClaimedChunks(List<Chunk> claimedChunks) {
		this.claimedChunks = claimedChunks;
	}
	
	public List<Chunk> getClaimedChunks() {
		return claimedChunks;
	}

}
