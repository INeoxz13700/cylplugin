package fr.karmaowner.events;

import fr.karmaowner.utils.MoneyUtils;
import fr.karmaowner.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

public class LoggerEvent implements Listener
{

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.getBlockPlaced() != null)
        {
            System.out.println(event.getPlayer().getName() + " placed " + event.getBlockPlaced().getType().name() + " " + event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.getBlock() != null)
        {
            System.out.println(event.getPlayer().getName() + " destroyed " + event.getBlock().getType().name() + " " + event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event)
    {
        if(event.getPlayer().hasPermission(Permissions.Staff))
        {
            if(event.getItemDrop() != null)
            {
                System.out.println("ALERT " + event.getPlayer().getName() + " dropped " + event.getItemDrop().getItemStack().toString() + " " + event.getPlayer().getLocation());
            }
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event)
    {
        if(event.getCursor().getTypeId() == 0) return;

        Player thePlayer = (Player)event.getWhoClicked();
        InventoryView inventoryView = event.getView();
        Inventory toInventory = inventoryView.getTopInventory();

        if(event.getClickedInventory() == inventoryView.getBottomInventory()) return;

        if(thePlayer.hasPermission(Permissions.Staff))
        {
            if(toInventory instanceof PlayerInventory)
            {
                PlayerInventory toInventoryPlayer = (PlayerInventory) toInventory;

                System.out.println("ALERT " + thePlayer.getName() + " transferred " + event.getCursor().toString() + " to " + toInventoryPlayer.getName() + ":" + toInventoryPlayer.getTitle() + " inventory (Inventory of " + toInventoryPlayer.getHolder().getName() + ")");

            }
            else
            {
                System.out.println("ALERT " + thePlayer.getName() + " transferred " + event.getCursor().toString() + " to " + toInventory.getName() + ":" + toInventory.getTitle() + " at " + thePlayer.getLocation().toString());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        InventoryView inventoryView = event.getView();

        if(inventoryView.getTopInventory() == null) return;

        if(inventoryView.getTopInventory().getName().contains("chest"))
        {
            int value = MoneyUtils.getValueOfMoneyInInventory(inventoryView.getTopInventory());
            if(value >= 500000)
            {
                System.out.println("ALERT POTENTIAL DUPLICATED MONEY CHEST FOUND AT " + event.getPlayer().getLocation().toString()  + " FROM " + event.getPlayer().getName() + " VALUE " + value + " EUR");
            }
        }
    }

}
