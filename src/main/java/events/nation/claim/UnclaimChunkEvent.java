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

public class UnclaimChunkEvent extends NationEvent{

	private Player player;
	private Chunk chunk;
	
	public UnclaimChunkEvent(NationMapping nation, CommandSender sender, Chunk chunk) {
		super(nation);
		this.player = (Player) sender;
		this.chunk = chunk;
		
		if(super.isCancelled) return;
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		CommandManager manager = main.getCommandManager();
		
		if(manager.getClaimingSet().contains(player.getUniqueId())) {
			manager.getClaimingSet().remove(player.getUniqueId());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Claim.TurnedOff.Message"), player.getName()));
		}
		
		if(nation.getClaimedChunks().size() == 1) {
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.UnclaimedChunk.Message"), nation.getName()));
			nation.unclaimChunk(chunk);
			
			MappingRepository mappingRepo = main.getMappingRepo();
			
			if(!manager.getUnclaimingSet().contains(this.player.getUniqueId())) return;
			manager.getUnclaimingSet().remove(this.player.getUniqueId());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), nation.getName()));
			
			PlayerMapping playerMap = mappingRepo.getPlayerByUUID(this.player.getUniqueId());
			mappingRepo.getNationByPlayer(playerMap).saveChunks();
			return;
		}
		
		sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.UnclaimedChunk.Message"), nation.getName()));
		nation.unclaimChunk(chunk);
	}

	public Player getPlayer() {
		return player;
	}

	public Chunk getChunk() {
		return chunk;
	}
}
