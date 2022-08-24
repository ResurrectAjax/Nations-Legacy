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
import managers.CommandManager;
import persistency.NationMapping;

public class UnclaimAllChunksEvent extends NationEvent{

	private Player player;
	private List<Chunk> chunks = new ArrayList<Chunk>();
	
	public UnclaimAllChunksEvent(NationMapping nation, CommandSender sender) {
		super(nation);
		this.player = (Player) sender;
		this.chunks.addAll(nation.getClaimedChunks());
		
		if(super.isCancelled) return;
		
		Main main = Main.getInstance();
		FileConfiguration language = main.getLanguage();
		
		CommandManager manager = main.getCommandManager();
		if(manager.getUnclaimingSet().contains(player.getUniqueId())) {
			manager.getUnclaimingSet().remove(player.getUniqueId());
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.TurnedOff.Message"), player.getName()));
		}
		
		if(nation.getClaimedChunks().size() == 0) {
			sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.NoChunks.Message"), nation.getName()));
			return;
		}
		
		sender.sendMessage(GeneralMethods.format(sender, language.getString("Command.Nations.Unclaim.UnclaimedAll.Message"), nation.getName()));
		nation.unclaimAll();
	}

	public Player getPlayer() {
		return player;
	}

	public List<Chunk> getChunk() {
		return chunks;
	}
}
