package events.nation.claim;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import events.nation.NationEvent;
import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;

public class UnclaimAllChunksEvent extends NationEvent{

	private List<Chunk> chunks = new ArrayList<Chunk>();
	
	public UnclaimAllChunksEvent(NationMapping nation, CommandSender sender) {
		super(nation, sender);
		Player player = (Player) sender;
		this.chunks.addAll(nation.getClaimedChunks());
		
		if(super.isCancelled) return;
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		
		MappingRepository mappingRepo = main.getMappingRepo();
		if(mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) {
			mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), player.getName()));
		}
		
		if(nation.getClaimedChunks().size() == 0) {
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
			return;
		}
		
		sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.UnclaimedAll.Message"), nation.getName()));
		nation.unclaimAll();
	}

	public List<Chunk> getChunk() {
		return chunks;
	}
}
