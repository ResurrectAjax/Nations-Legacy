package me.resurrectajax.nationslegacy.listeners;

import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.resurrectajax.nationslegacy.nationmaps.NationMap;

public class ItemListener implements Listener{
	
	@EventHandler
	public void onBlockItemDrop(BlockDropItemEvent event) {
		List<Item> items = event.getItems();
		for(Item item : items) {
			if(item == null || !item.getItemStack().hasItemMeta() || !item.getItemStack().getItemMeta().hasCustomModelData()) continue;
			items.removeIf(el -> item.getItemStack().getItemMeta().getCustomModelData() == NationMap.getMapModelData());
		}
	}
	
	@EventHandler
	public void onEntityItemDrop(EntityDropItemEvent event) {
		Item item = event.getItemDrop();
		if(item == null || !item.getItemStack().hasItemMeta() || !item.getItemStack().getItemMeta().hasCustomModelData()) return;
		if(item.getItemStack().getItemMeta().getCustomModelData() == NationMap.getMapModelData()) item.setItemStack(null);
	}
	
	@EventHandler
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		if(item == null || !item.getItemStack().hasItemMeta() || !item.getItemStack().getItemMeta().hasCustomModelData()) return;
		if(item.getItemStack().getItemMeta().getCustomModelData() == NationMap.getMapModelData()) item.setItemStack(null);
	}
	
	@EventHandler
	public void onClickItem(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasCustomModelData()) return;
		if(item.getItemMeta().getCustomModelData() != NationMap.getMapModelData()) return;
		
		if(event.getView().getTopInventory().getType() != InventoryType.CRAFTING) event.setCancelled(true);
	}
}
