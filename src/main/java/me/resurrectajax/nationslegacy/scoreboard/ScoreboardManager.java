package me.resurrectajax.nationslegacy.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.resurrectajax.nationslegacy.general.GeneralMethods;
import me.resurrectajax.nationslegacy.main.Nations;
import me.resurrectajax.nationslegacy.persistency.MappingRepository;
import me.resurrectajax.nationslegacy.persistency.NationMapping;
import me.resurrectajax.nationslegacy.persistency.PlayerMapping;

public class ScoreboardManager {
	
	private Nations main;
	public ScoreboardManager(Nations main) {
		this.main = main;
	}
	
	public void updateScoreboard(Player player) {
		MappingRepository mappingRepo = main.getMappingRepo();
		
		if(player == null) return; 
		PlayerMapping playerM = mappingRepo.getPlayerByUUID(player.getUniqueId());
		
		Scoreboard scoreboard = main.getServer().getScoreboardManager().getNewScoreboard();
		for(Player playerA : Bukkit.getOnlinePlayers()) {
			PlayerMapping playerMap = mappingRepo.getPlayerByUUID(playerA.getUniqueId());
			NationMapping nation = playerMap.getNationID() != null ? mappingRepo.getNationByID(playerMap.getNationID()) : null;
			
			boolean isAlly = mappingRepo.getAllianceByNationIDs(playerM.getNationID(), playerMap.getNationID()) == null ? false : true;
			boolean isEnemy = mappingRepo.getWarByNationIDs(playerM.getNationID(), playerMap.getNationID()) == null ? false : true;
			
			
			String format;
			String nationName = nation != null ? nation.getName() : "";
			if(isAlly || (playerM.getNationID() == playerMap.getNationID() && playerM.getNationID() != null)) format = "&a&l" + nationName;
			else if(isEnemy) format = "&c&l" + nationName;
			else format = "&9&l" + nationName;
			
			if(playerA.equals(player)) continue;
			Objective obj = scoreboard.getObjective(playerA.getName() + "_nationname");
            if(obj == null) obj = scoreboard.registerNewObjective(playerA.getName() + "_nationname", "dummy", nation != null ? GeneralMethods.format(format) : GeneralMethods.format("&2&lWilderness"));
            else obj.setDisplayName(nation != null ? GeneralMethods.format(format) : GeneralMethods.format("&2&lWilderness"));
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            Score score = obj.getScore(playerA.getName());
            score.setScore(playerMap.getKillpoints());
        }
		player.setScoreboard(scoreboard);
	}

}
