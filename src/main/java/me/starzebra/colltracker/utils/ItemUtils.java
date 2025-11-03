package me.starzebra.colltracker.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtils {

    public static String getUUID(ItemStack item) {
        try {
            NBTTagCompound nbt = item.getSubCompound("ExtraAttributes",false);
            return nbt.getString("uuid");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getItemName(ItemStack itemStack){
        try {
            NBTTagCompound nbt = itemStack.getSubCompound("display", false);
            return nbt.getString("Name");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getUnformattedItemName(ItemStack itemStack){
        return getItemName(itemStack).replaceAll("§[a-z0-9]", "");
    }
}
