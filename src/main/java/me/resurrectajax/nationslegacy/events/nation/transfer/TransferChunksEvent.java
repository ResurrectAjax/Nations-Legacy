package me.resurrectajax.nationslegacy.events.nation.transfer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.events.nation.claim.SaveChunksEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class TransferChunksEvent extends NationEvent{

	public TransferChunksEvent(NationMapping nation, NationMapping receiver, CommandSender sender, int neededChunkAmount) {
		super(nation, sender);
		
		Nations main = Nations.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = main.getLanguage(), config = main.getConfig();
				
				Set<Chunk> claimedChunks = nation.getClaimedChunks();
				int totalChunksLeft = nation.getMaxChunks()-claimedChunks.size();
				if(totalChunksLeft-neededChunkAmount > 0) {
					if(config.getBoolean("Nations.Transfer.Chunks.Regain")) nation.setGainedChunks(nation.getGainedChunks()-neededChunkAmount > 0 ? nation.getGainedChunks()-neededChunkAmount : 0);
					nation.setMaxChunks(nation.getMaxChunks()-neededChunkAmount);
				}
				else {
					int neededChunks = Math.abs(totalChunksLeft - neededChunkAmount);
					if(claimedChunks.size() > 0) {
						int[] smallest = getSmallest(claimedChunks);
						int[] largest = getLargest(claimedChunks);
						List<Chunk> sortedList = claimedChunks.stream()
								.sorted(Comparator.comparing(Chunk::getX).thenComparing(Chunk::getZ))
								.toList();
						
						Set<Chunk> chunks = getBorderChunks(smallest[0], smallest[1], largest[0], largest[1], sortedList, new HashSet<>(), neededChunks);
						chunks.forEach(el -> nation.unclaimChunk(el));	
						Bukkit.getPluginManager().callEvent(new SaveChunksEvent(nation, sender));
					}	
					if(config.getBoolean("Nations.Transfer.Chunks.Regain")) nation.setGainedChunks(nation.getGainedChunks()-neededChunkAmount > 0 ? nation.getGainedChunks()-neededChunkAmount : 0);
					nation.setMaxChunks(nation.getMaxChunks()-neededChunkAmount);
				}
				
				receiver.setMaxChunks(receiver.getMaxChunks()+neededChunkAmount);
				nation.update();
				receiver.update();
				
				String message = language.getString("Command.Nations.Transfer.Sent.Message");
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, message, String.format("%d", neededChunkAmount), receiver.getName()));
				
				receiver.getAllMembers().forEach(el -> {
					String msg = language.getString("Command.Nations.Transfer.Received.Message");
					msg = msg.replace("%nations_transfer_amount%", String.format("%d", neededChunkAmount));
					Player play = Bukkit.getPlayer(el.getUUID());
					if(play != null) play.sendMessage(GeneralMethods.format((OfflinePlayer)sender, msg, sender.getName()));
				});
				
				main.getMappingRepo().getChunkGainManager().addChunkGain(nation);
			}
		}, 0L);
	}
	
	
	private Set<Chunk> getBorderChunks(int minX, int minZ, int maxX, int maxZ, List<Chunk> original, Set<Chunk> borders, int neededAmount) {
		for(Chunk chunk : original) {
			if(borders.size() < neededAmount && chunk.getX() >= minX && chunk.getX() <= maxX && (chunk.getZ() == minZ || chunk.getZ() == maxZ)) borders.add(chunk);
			if(borders.size() < neededAmount && chunk.getZ() >= minZ && chunk.getZ() <= maxZ && (chunk.getX() == minX || chunk.getX() == maxX)) borders.add(chunk);
		}
		if(borders.size() >= original.size()) return borders;
		if(borders.size() < neededAmount) getBorderChunks(minX+1, minZ+1, maxX-1, maxZ-1, original, borders, neededAmount);
		return borders;
	}
	
	private int[] getSmallest(Set<Chunk> chunks) {
		Integer x = null, z = null;
		for(Chunk chunk : chunks) {
			if(x == null) x = chunk.getX();
			if(z == null) z = chunk.getZ();
			if(chunk.getX() < x) x = chunk.getX();
			if(chunk.getZ() < z) z = chunk.getZ();
		}
		return new int[] {x, z};
	}
	
	private int[] getLargest(Set<Chunk> chunks) {
		Integer x = null, z = null;
		for(Chunk chunk : chunks) {
			if(x == null) x = chunk.getX();
			if(z == null) z = chunk.getZ();
			if(chunk.getX() > x) x = chunk.getX();
			if(chunk.getZ() > z) z = chunk.getZ();
		}
		return new int[] {x, z};
	}

}
