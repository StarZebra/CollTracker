package me.starzebra.colltracker.mixin;

import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.utils.ItemUtils;
import me.starzebra.colltracker.utils.LocationUtils;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Unique
    private static List<Item> colltracker$supportedItems = Arrays.asList(
            Item.getItemById(409),
            Item.getItemById(397),
            Item.getItemById(270),
            Item.getItemById(274),
            Item.getItemById(257),
            Item.getItemById(285),
            Item.getItemById(278));

    @Shadow private ItemStack currentItemHittingBlock;

    @Redirect(method = {"isHittingPosition"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemStackTagsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB){
        if(SimpleConfig.drillfix && LocationUtils.isInSkyblock){
            if(colltracker$supportedItems.contains(currentItemHittingBlock.getItem())){
                return Objects.equals(ItemUtils.getUUID(stackA), ItemUtils.getUUID(stackB));
            }
        }
        return ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

}
