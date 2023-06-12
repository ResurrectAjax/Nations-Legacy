package me.resurrectajax.nationslegacy.events.nation.claim;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.resurrectajax.nationslegacy.algorithms.FillAlgorithm;
import me.resurrectajax.nationslegacy.events.nation.NationEvent;
import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;

public class ClaimAutoFillEvent extends NationEvent{

	private List<Chunk> claimedChunks;
	
	public ClaimAutoFillEvent(NationMapping nation, CommandSender sender, Chunk startChunk) {
		super(nation, sender);
		
		Player player = (Player) sender;
		
		Nations main = Nations.getInstance();
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		FillAlgorithm algorithm = new FillAlgorithm(main, nation, startChunk);
		this.claimedChunks = new ArrayList<>(algorithm.fillSquareOutline());
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				if(claimedChunks.size() > nation.getMaxChunks()-nation.getClaimedChunks().size()) {
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.Insufficient.Message"), nation.getName()));
					if(mappingRepo.getClaimingSet().contains(player.getUniqueId())) {
						mappingRepo.getClaimingSet().remove(player.getUniqueId());
						sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), nation.getName()));
					}
					return;
				}
				if(claimedChunks.size() == nation.getMaxChunks()-nation.getClaimedChunks().size()) {
					if(mappingRepo.getClaimingSet().contains(player.getUniqueId())) {
						mappingRepo.getClaimingSet().remove(player.getUniqueId());
						sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.MaxChunks.Message"), nation.getName()));
						sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), nation.getName()));
					}
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
