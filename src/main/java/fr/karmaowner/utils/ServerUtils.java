package fr.karmaowner.utils;

import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class ServerUtils {

    public static final List<Integer> notMoveableItems = Arrays.asList(6561, 6557, 6297, 6215, 6287, 4541, 6391, 345);

    public static final String newInventoryName = "Chest";

    public static final int CARTEIDENTITE = 6561;

    public static final int TELEPHONE = 6557;

    public static final int PERMIS_V = 6297;

    public static final int PERMIS_B = 6215;
    public static final int PERMIS_C = 6287;


    public static final int PAGRICOLE = 4541;

    public static final int PORTARME = 6391;

    public static boolean isPlayerInventory(Inventory inventory)
    {
        if(inventory == null) return false;

        return inventory.toString().contains("CraftInventoryPlayer") || inventory.toString().contains("CraftInventoryCustom");
    }

}
