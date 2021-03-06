package pneumaticCraft.common.ai;

import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;
import pneumaticCraft.common.progwidgets.ICountWidget;
import pneumaticCraft.common.progwidgets.ISidedWidget;
import pneumaticCraft.common.progwidgets.ProgWidgetAreaItemBase;
import pneumaticCraft.common.util.IOHelper;
import pneumaticCraft.common.util.PneumaticCraftUtils;
import pneumaticCraft.lib.PneumaticValues;

public class DroneEntityAIInventoryImport extends DroneAIImExBase{

    public DroneEntityAIInventoryImport(IDroneBase drone, ProgWidgetAreaItemBase widget){
        super(drone, widget);
    }

    @Override
    protected boolean isValidPosition(ChunkPosition pos){
        return importItems(pos, true);
    }

    @Override
    protected boolean doBlockInteraction(ChunkPosition pos, double distToBlock){
        return importItems(pos, false) && super.doBlockInteraction(pos, distToBlock);
    }

    private boolean importItems(ChunkPosition pos, boolean simulate){
        IInventory inv = IOHelper.getInventoryForTE(drone.getWorld().getTileEntity(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ));
        if(inv != null) {
            Set<Integer> accessibleSlots = PneumaticCraftUtils.getAccessibleSlotsForInventoryAndSides(inv, ((ISidedWidget)widget).getSides());
            for(Integer i : accessibleSlots) {
                ItemStack stack = inv.getStackInSlot(i);
                if(stack != null && widget.isItemValidForFilters(stack) && IOHelper.canExtractItemFromInventory(inv, stack, i, ((ISidedWidget)widget).getSides())) {
                    ItemStack importedStack = stack.copy();
                    if(((ICountWidget)widget).useCount()) importedStack.stackSize = Math.min(importedStack.stackSize, getRemainingCount());
                    ItemStack remainder = IOHelper.insert(drone.getInventory(), importedStack.copy(), 0, simulate);
                    int removedItems = importedStack.stackSize - (remainder != null ? remainder.stackSize : 0);
                    if(!simulate) {
                        ItemStack newStack = stack.copy();
                        newStack.stackSize = stack.stackSize - removedItems;
                        inv.setInventorySlotContents(i, newStack.stackSize > 0 ? newStack : null);
                        decreaseCount(removedItems);
                        drone.addAir(null, -PneumaticValues.DRONE_USAGE_INV);
                        if(((ICountWidget)widget).useCount() && getRemainingCount() <= 0) return false;
                    } else if(removedItems > 0) return true;
                }
            }
        }

        return false;
    }

}
