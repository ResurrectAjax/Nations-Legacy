package events.nation.claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class ClaimChunkEvent extends NationEvent{
	private Chunk chunk;
	
	public ClaimChunkEvent(NationMapping nation, CommandSender sender, Chunk chunkA) {
		super(nation, sender);
		Player player = (Player) sender;
		this.chunk = chunkA;
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		MappingRepository mappingRepo = main.getMappingRepo();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				if(mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) {
					mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), player.getName()));
				}
				
				if(nation.getClaimedChunks().size() < nation.getMaxChunks()-1) {
					nation.addClaimedChunk(chunk);
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.ClaimedChunk.Message"), String.format("%d", nation.getMaxChunks() - nation.getClaimedChunks().size())));
				}
				else {
					if(nation.getClaimedChunks().size() == nation.getMaxChunks()-1) nation.addClaimedChunk(chunk);
					
					if(!mappingRepo.getClaimingSet().contains(player.getUniqueId())) return;
					mappingRepo.getClaimingSet().remove(player.getUniqueId());
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.MaxChunks.Message"), nation.getName()));
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), nation.getName()));
					
					PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
					mappingRepo.getNationByPlayer(playerMap).saveChunks();
					return;
				}
			}
		}, 1L);
		
	}

	public Chunk getChunk() {
		return chunk;
	}

}
