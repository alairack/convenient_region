package alairack.region;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if(event.getItem() == null) {
            return;
        }
        ItemStack itemStack = event.getItem();

        NBTItem nbti = new NBTItem(itemStack);
        if (nbti.hasTag("regionID")) {
            String regionID = nbti.getString("regionID");
            if (RegionController.useTool(event.getPlayer(), regionID)){
                itemStack.setAmount(itemStack.getAmount()-1);
            }
        }

    }


}
