package events.nation.war;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class WarEndEvent extends WarEvent{

	/**
	 * Event for ending a war with a winning and losing nation
	 * @param winner - {@link NationMapping} winning nation
	 * @param loser - {@link NationMapping} losing nation
	 * @param sender - {@link CommandSender} last killed player
	 * */
	public WarEndEvent(NationMapping winner, NationMapping loser, @Nullable CommandSender sender) {
		super(winner, loser, sender);
		
		Main main = Main.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			
			@Override
			public void run() {
				if(isCancelled) return;
				
				FileConfiguration language = main.getLanguage();
				MappingRepository mappingRepo = main.getMappingRepo();
				
				loser.unclaimAll();
				int losingChunkAmount = loser.getMaxChunks()-loser.getBaseChunkLimit();
				
				mappingRepo.removeWar(nation.getNationID(), enemy.getNationID());
				
				Player player = Bukkit.getOnlinePlayers().stream().filter(el -> nation.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				Player enemyPlayer = Bukkit.getOnlinePlayers().stream().filter(el -> enemy.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId()))).findFirst().orElse(null);
				OfflinePlayer enemyOffline = enemy.getAllMembers().stream().map(el -> Bukkit.getOfflinePlayer(el.getUUID())).findFirst().orElse(null);
				
				Set<PlayerMapping> players = new HashSet<>();
				players.addAll(winner.getAllMembers());
				players.addAll(loser.getAllMembers());
				
				Bukkit.getOnlinePlayers().stream()
				.filter(el -> players.contains(mappingRepo.getPlayerByUUID(el.getUniqueId())))
				.forEach(el -> {
					if(enemyPlayer == null) el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Nations.War.End.Broadcast.Message"), nation.getName()));
					el.sendMessage(GeneralMethods.relFormat(player, enemyPlayer, language.getString("Command.Nations.War.End.Broadcast.Message"), nation.getName()));
				});
				Bukkit.getOnlinePlayers().stream()
					.filter(el -> enemy.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())))
					.forEach(el -> {
						if(enemyPlayer == null) el.sendMessage(GeneralMethods.format((OfflinePlayer)el, language.getString("Command.Nations.War.End.ChunksLost.Message"), nation.getName()));
						el.sendMessage(GeneralMethods.relFormat(player, enemyPlayer, language.getString("Command.Nations.War.End.ChunksLost.Message"), nation.getName()));
					});
				Bukkit.getOnlinePlayers().stream()
				.filter(el -> nation.getAllMembers().contains(mappingRepo.getPlayerByUUID(el.getUniqueId())))
				.forEach(el -> {
					if(enemyPlayer == null) el.sendMessage(GeneralMethods.format((OfflinePlayer)enemyOffline, language.getString("Command.Nations.War.End.ChunksGained.Message"), enemy.getName()));
					el.sendMessage(GeneralMethods.relFormat(player, enemyPlayer, language.getString("Command.Nations.War.End.ChunksGained.Message"), enemy.getName()));
				});
				
				winner.setMaxChunks(winner.getMaxChunks()+losingChunkAmount);
				loser.setMaxChunks(loser.getBaseChunkLimit());
			}
		}, 1L);
	}
	
	/**
	 * Returns the winning nation
	 * @return {@link NationMapping} nation
	 * */
	@Override
	public NationMapping getNation() {
		return nation;
	}
	
	/**
	 * Returns the losing nation
	 * @return {@link NationMapping} enemy
	 * */
	@Override
	public NationMapping getEnemy() {
		return enemy;
	}

}
