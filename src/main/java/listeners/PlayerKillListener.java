package listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import enumeration.Rank;
import events.nation.war.WarEndEvent;
import general.GeneralMethods;
import main.Main;
import persistency.AllianceMapping;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;
import persistency.WarMapping;

public class PlayerKillListener implements Listener{
	
	private Main main;
	private MappingRepository mappingRepo;
	private Set<Combat> combatSet = new HashSet<>();
	
	public PlayerKillListener(Main main) {
		this.main = main;
		this.mappingRepo = main.getMappingRepo();
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getEntity(), damager = (Player) event.getDamager();
		
		Combat playerCombat = getPlayerCombat(player.getUniqueId()), damagerCombat = getPlayerCombat(damager.getUniqueId());
		if(playerCombat != null) Bukkit.getScheduler().cancelTask(playerCombat.getTaskId());
		if(damagerCombat != null) Bukkit.getScheduler().cancelTask(playerCombat.getTaskId());
		combatSet.removeIf(el -> el.getPlayer1().equals(player.getUniqueId()) || el.getPlayer2().equals(damager.getUniqueId()));
		
		FileConfiguration config = main.getConfig();
		int combatDuration = GeneralMethods.convertHoursMinutesSecondsToSeconds(config.getString("Nations.Combat.Duration"));
		
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				combatSet.removeIf(el -> el.getPlayer1().equals(player.getUniqueId()) || el.getPlayer2().equals(damager.getUniqueId()));
			}
		}, combatDuration*20L);
		
		Combat combat = new Combat(player.getUniqueId(), damager.getUniqueId(), taskId);
		combatSet.add(combat);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Entity entity = event.getEntity();
		if(!(entity instanceof Player)) return;
		Player player = (Player) entity;
		UUID uuid = player.getUniqueId();
		
		Combat playerCombat = getPlayerCombat(uuid);
		if(playerCombat == null) return;
		Bukkit.getScheduler().cancelTask(playerCombat.getTaskId());
		combatSet.remove(playerCombat);
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(uuid);
		PlayerMapping damagerMap = mappingRepo.getPlayerByUUID(playerCombat.getOtherPlayer(uuid));
		
		int rankWorth = Rank.getRankWorth(playerMap.getRank());
		if(playerMap.getNationID() == null) {
			int kp = rankWorth + damagerMap.getKillpoints();
			damagerMap.setKillpoints(kp);
			damagerMap.update();
			
			int kp2 = playerMap.getKillpoints()-rankWorth;
			playerMap.setKillpoints(kp2);
			playerMap.update();
		}
		
		if(playerMap.getNationID() == null || damagerMap.getNationID() == null) return;

		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID()), damagerNation = mappingRepo.getNationByID(damagerMap.getNationID());
		WarMapping war = mappingRepo.getWarByNationIDs(playerMap.getNationID(), damagerMap.getNationID());
		AllianceMapping alliance = mappingRepo.getAllianceByNationIDs(playerMap.getNationID(), damagerMap.getNationID());
		if(war != null) {
			int kp = rankWorth + damagerMap.getKillpoints();
			damagerMap.setKillpoints(kp);
			damagerMap.update();
			
			int kp2 = playerMap.getKillpoints()-rankWorth;
			playerMap.setKillpoints(kp2);
			playerMap.update();
			
			int damagerNationKp, nationKp;
			if(war.getNation().equals(damagerNation)) {
				damagerNationKp = war.getNationKillpoints() + rankWorth;
				war.setNationKillpoints(damagerNationKp);
				
				nationKp = war.getEnemyKillpoints() - rankWorth;
				if(nationKp >= 0) war.setEnemyKillpoints(nationKp);
			}
			else {
				damagerNationKp = war.getEnemyKillpoints() + rankWorth;
				war.setEnemyKillpoints(damagerNationKp);
				
				nationKp = war.getNationKillpoints() - rankWorth;
				if(nationKp >= 0) war.setNationKillpoints(nationKp);
			}
			if(damagerNationKp >= war.getKillpointGoal()) Bukkit.getPluginManager().callEvent(new WarEndEvent(damagerNation, nation, player));
		}
		else if(alliance != null) return;
		else {
			int kp = rankWorth + damagerMap.getKillpoints();
			damagerMap.setKillpoints(kp);
			damagerMap.update();
			
			int kp2 = playerMap.getKillpoints()-rankWorth;
			playerMap.setKillpoints(kp2);
			playerMap.update();
		}
		
		mappingRepo.getScoreboardManager().updateScoreboard(player);
		mappingRepo.getScoreboardManager().updateScoreboard(Bukkit.getPlayer(playerCombat.getOtherPlayer(uuid)));
	}
	
	private Combat getPlayerCombat(UUID uuid) {
		return combatSet.stream()
				.filter(el -> el.getPlayer1().equals(uuid) || el.getPlayer2().equals(uuid))
				.findAny()
				.orElse(null);
	}
	
	private class Combat{
		private UUID player1;
		private UUID player2;
		private int taskId;
		
		public Combat(UUID player1, UUID player2, int task) {
			this.player1 = player1;
			this.player2 = player2;
			this.taskId = task;
		}
		
		public UUID getOtherPlayer(UUID uuid) {
			if(uuid.equals(player1)) return player2;
			else if(uuid.equals(player2)) return player1;
			else return null;
		}
		
		public UUID getPlayer1() {
			return player1;
		}
		public UUID getPlayer2() {
			return player2;
		}
		public int getTaskId() {
			return taskId;
		}
	}
}
