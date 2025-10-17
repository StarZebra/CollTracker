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
}
