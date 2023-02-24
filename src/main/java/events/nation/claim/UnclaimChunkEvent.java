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

public class UnclaimChunkEvent extends NationEvent{

	private Chunk chunk;
	
	public UnclaimChunkEvent(NationMapping nation, CommandSender sender, Chunk chunkA) {
		super(nation, sender);
		Player player = (Player) sender;
		this.chunk = chunkA;
		
		Main main = Main.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = main.getLanguage();
				MappingRepository mappingRepo = main.getMappingRepo();
				
				if(mappingRepo.getClaimingSet().contains(player.getUniqueId())) {
					mappingRepo.getClaimingSet().remove(player.getUniqueId());
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), player.getName()));
				}
				
				if(nation.getClaimedChunks().size() == 1) {
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.UnclaimedChunk.Message"), nation.getName()));
					nation.unclaimChunk(chunk);
					
					if(!mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) return;
					mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
					sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), nation.getName()));
					
					PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
					mappingRepo.getNationByPlayer(playerMap).saveChunks();
					return;
				}
				
				sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.UnclaimedChunk.Message"), nation.getName()));
				nation.unclaimChunk(chunk);
			}
		}, 1L);
	}

	public Chunk getChunk() {
		return chunk;
	}
}
