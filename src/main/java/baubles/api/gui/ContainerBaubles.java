package baubles.api.gui;

public interface ContainerBaubles {

    void setOffset(int offset);

    int getSlotByOffset(int slotIndex);

    void resetOffset();
}
