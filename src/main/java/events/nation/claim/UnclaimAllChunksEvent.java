package events.nation.claim;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
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
		
		chunks.addAll(nation.getClaimedChunks());
		
		Main main = Main.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				FileConfiguration language = main.getLanguage();
				
				if(isCancelled) return;
				
				MappingRepository mappingRepo = main.getMappingRepo();
				if(mappingRepo.getUnclaimingSet().contains(player.getUniqueId())) {
					mappingRepo.getUnclaimingSet().remove(player.getUniqueId());
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), player.getName()));
				}
				
				if(nation.getClaimedChunks().size() == 0) {
					sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
					return;
				}
				
				sender.sendMessage(GeneralMethods.format((OfflinePlayer)sender, language.getString("Command.Nations.Unclaim.UnclaimedAll.Message"), nation.getName()));
				nation.unclaimAll();
			}
		}, 0L);
		
	}

	public List<Chunk> getChunk() {
		return chunks;
	}
}
