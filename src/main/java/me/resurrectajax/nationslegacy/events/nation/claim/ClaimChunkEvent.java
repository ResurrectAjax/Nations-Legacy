package me.resurrectajax.nationslegacy.events.nation.claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class ClaimChunkEvent extends NationEvent{
	private Chunk chunk;
	
	public ClaimChunkEvent(NationMapping nation, CommandSender sender, Chunk chunkA) {
		super(nation, sender);
		Player player = (Player) sender;
		this.chunk = chunkA;
		
		Nations main = Nations.getInstance();
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				if(mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) {
					mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), player.getName()));
				}
				
				if(nation.getClaimedChunks().size() < nation.getMaxChunks()-1) {
					nation.addClaimedChunk(chunk);
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.ClaimedChunk.Message"), String.format("%d", nation.getMaxChunks() - nation.getClaimedChunks().size())));
				}
				else {
					if(nation.getClaimedChunks().size() == nation.getMaxChunks()-1) nation.addClaimedChunk(chunk);
					
					if(!mappingRepo.getClaimingSet().contains(player.getUniqueId())) return;
					mappingRepo.getClaimingSet().remove(player.getUniqueId());
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.MaxChunks.Message"), nation.getName()));
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), nation.getName()));
					
					Bukkit.getPluginManager().callEvent(new SaveChunksEvent(nation, sender));
					return;
				}
			}
		}, 1L);
		
	}

	public Chunk getChunk() {
		return chunk;
	}

}
