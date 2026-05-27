package baubles.common.integration.botania;

import net.minecraft.item.ItemStack;

public interface InventoryBaubleBoxAccess {

    default ItemStack getBox() {
        return ItemStack.EMPTY;
    }

    static InventoryBaubleBoxAccess get(vazkii.botania.client.gui.box.InventoryBaubleBox box) {
        return (InventoryBaubleBoxAccess) box;
    }
}
