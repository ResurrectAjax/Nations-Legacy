package listeners;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.loot.Lootable;

import enumeration.Flag;
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
	
	private boolean hasFlag(Chunk chunk, Flag flag) {
		FileConfiguration config = main.getConfig();
		boolean adjustable = config.getBoolean(String.format("Nations.Flags.%s.Adjustable", flag.toString()));
		if(!adjustable) {
			String allow = Flag.getDefault(flag);
			if(allow.equalsIgnoreCase("ALLOW")) return true;
			return false;
		}
		
		MappingRepository mappingRepo = main.getMappingRepo();
		NationMapping chunkNation = mappingRepo.getNationByChunk(chunk);
		if(chunkNation == null || !chunkNation.getFlags().get(flag)) return false;
		return true;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if(hasFlag(block.getChunk(), Flag.TerrainEdit)) return;
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if(hasFlag(block.getChunk(), Flag.TerrainEdit)) return;
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if(hasFlag(block.getChunk(), Flag.TerrainEdit)) return;
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onBucketFillEvent(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if(hasFlag(block.getChunk(), Flag.TerrainEdit)) return;
		cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onStorageAccess(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(block == null) return;
		
		if(block.getState() instanceof Container && !hasFlag(block.getChunk(), Flag.StorageAccess)) cancelIfInNationClaim(player, block.getChunk(), event);
	}
	@EventHandler
	public void onStorageAccess(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		Entity ent = event.getRightClicked();
		if(ent == null) return;
		
		if(hasFlag(ent.getLocation().getChunk(), Flag.StorageAccess)) return;
		if(!(ent instanceof InventoryHolder && ent instanceof Lootable)) return;
		cancelIfInNationClaim(player, ent.getLocation().getChunk(), event);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(block == null) return;
		
		if(hasFlag(block.getChunk(), Flag.Interact)) return;
		
		if(!(block.getState() instanceof Container)) cancelIfInNationClaim(player, block.getChunk(), event);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		Entity ent = event.getRightClicked();
		if(ent == null) return;
		
		if(hasFlag(ent.getLocation().getChunk(), Flag.Interact)) return;
		if(ent instanceof InventoryHolder && ent instanceof Lootable) return;
		cancelIfInNationClaim(player, ent.getLocation().getChunk(), event);
	}
	
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
		MappingRepository mappingRepo = main.getMappingRepo();
		
		Player player = (Player) event.getEntity(), damager = (Player) event.getDamager();
		PlayerMapping playerMap = mappingRepo.getPlayerByUUID(player.getUniqueId()), damagerMap = mappingRepo.getPlayerByUUID(damager.getUniqueId());
		
		NationMapping nation = mappingRepo.getNationByID(playerMap.getNationID());
		
		
		if(nation != null && nation.getFlags().get(Flag.FriendlyFire)) return;
		
		if(playerMap.getNationID() == damagerMap.getNationID()) event.setCancelled(true);
	}
}
