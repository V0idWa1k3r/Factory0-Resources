package v0id.api.f0resources.world;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IOreData
{
    ItemStack createOreItem(int amount);

    Item getOreItem();

    void setOreItem(Item item);

    short getOreMeta();

    void setOreMeta(short s);

    int getOreAmount();

    void setOreAmount(int i);

    int getTierReq();

    void setTierReq(int i);
}
