package events.nation.claim;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import managers.CommandManager;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class ClaimChunkEvent extends NationEvent{
	private Player player;
	private Chunk chunk;
	
	public ClaimChunkEvent(NationMapping nation, CommandSender sender, Chunk chunk) {
		super(nation);
		this.player = (Player) sender;
		this.chunk = chunk;
		
		if(super.isCancelled) return;
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		CommandManager manager = main.getCommandManager();
		
		if(manager.getUnclaimingSet().contains(player.getUniqueId())) {
			manager.getUnclaimingSet().remove(player.getUniqueId());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), player.getName()));
		}
		
		if(nation.getClaimedChunks().size() < nation.getMaxChunks()-1) {
			nation.addClaimedChunk(chunk);
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.ClaimedChunk.Message"), String.format("%d", nation.getMaxChunks() - nation.getClaimedChunks().size())));
		}
		else {
			if(nation.getClaimedChunks().size() == nation.getMaxChunks()-1) nation.addClaimedChunk(chunk);
			MappingRepository mappingRepo = main.getMappingRepo();
			
			if(!manager.getClaimingSet().contains(this.player.getUniqueId())) return;
			manager.getClaimingSet().remove(this.player.getUniqueId());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.MaxChunks.Message"), nation.getName()));
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), nation.getName()));
			
			PlayerMapping playerMap = mappingRepo.getPlayerByUUID(this.player.getUniqueId());
			mappingRepo.getNationByPlayer(playerMap).saveChunks();
			return;
		}
	}

	public Player getPlayer() {
		return player;
	}

	public Chunk getChunk() {
		return chunk;
	}

}
