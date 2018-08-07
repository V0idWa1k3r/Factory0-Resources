package v0id.f0resources.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.SlotItemHandler;
import v0id.f0resources.tile.TileBurnerDrill;

public class ContainerBurnerDrill extends Container
{
    public int fuel;
    public int maxFuel;
    public TileBurnerDrill tile;

    public ContainerBurnerDrill(InventoryPlayer playerInventory, TileBurnerDrill drill)
    {
        this.addSlotToContainer(new SlotItemHandler(drill.inventory, 0, 80, 16));
        this.addSlotToContainer(new SlotItemHandler(drill.inventory, 1, 80, 52));
        this.addPlayerInventory(playerInventory);
        this.tile = drill;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = this.listeners.get(i);
            if (this.fuel != this.tile.fuelHandler.getFuel())
            {
                icontainerlistener.sendWindowProperty(this, 0, this.tile.fuelHandler.getFuel());
            }

            if (maxFuel != this.tile.fuelHandler.getMaxFuel())
            {
                icontainerlistener.sendWindowProperty(this, 1, this.tile.fuelHandler.getMaxFuel());
            }
        }

        this.fuel = this.tile.fuelHandler.getFuel();
        this.maxFuel = this.tile.fuelHandler.getMaxFuel();
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);
        if (id == 0)
        {
            this.tile.fuelHandler.setFuel(data);
        }

        if (id == 1)
        {
            this.tile.fuelHandler.setMaxFuel(data);
        }
    }

    public void addPlayerInventory(InventoryPlayer playerInventory)
    {
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 2)
            {
                if (!this.mergeItemStack(itemstack1, 2, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, true))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
