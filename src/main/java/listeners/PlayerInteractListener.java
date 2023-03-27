package listeners;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import main.Main;
import persistency.MappingRepository;
import persistency.NationMapping;
import persistency.PlayerMapping;

public class PlayerInteractListener implements Listener{

	private Main main;
	public PlayerInteractListener(Main main) {
		this.main = main;
	}
	
	private void cancelIfInNationClaim(Player player, Chunk chunk, Cancellable event) {
		MappingRepository mappingRepo = main.getMappingRepo();
		
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId());
		
		NationMapping chunkNation = mappingRepo.getNationByChunk(chunk);
		if(chunkNation == null) return;
		
		if(playerMap.getNationID() != chunkNation.getNationID()) event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onBucketFillEvent(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onStorageAccess(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(block == null) return;
		
		if(block.getState() instanceof Container) cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
		MappingRepository mappingRepo = main.getMappingRepo();
		
		Player player = (Player) event.getEntity(), damager = (Player) event.getDamager();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId()), damagerMap = mappingRepo.getPlayerByUUID(damager.getUniqueId());
		
		if(playerMap.getNationID() == damagerMap.getNationID()) event.setCancelled(true);
	}
}
