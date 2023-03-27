package scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import general.GeneralMethods;
import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class ScoreboardManager {
	
	private Main main;
	public ScoreboardManager(Main main) {
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
            Objective obj = scoreboard.registerNewObjective("nationname", "dummy", nation != null ? GeneralMethods.format(format) : GeneralMethods.format("&2&lWilderness"));
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            Score score = obj.getScore(playerA.getName());
            score.setScore(playerMap.getKillpoints());
        }
		player.setScoreboard(scoreboard);
	}

}
