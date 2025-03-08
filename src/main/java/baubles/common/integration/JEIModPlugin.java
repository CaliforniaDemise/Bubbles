package baubles.common.integration;

import baubles.client.gui.GuiPlayerExpanded;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@JEIPlugin
public class JEIModPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new BubblesGuiHandler());
    }

    public static class BubblesGuiHandler implements IAdvancedGuiHandler<GuiPlayerExpanded> {

        @NotNull
        @Override
        public Class<GuiPlayerExpanded> getGuiContainerClass() {
            return GuiPlayerExpanded.class;
        }

        @Nullable
        @Override
        public List<Rectangle> getGuiExtraAreas(@NotNull GuiPlayerExpanded gui) {
            int add = gui.getBaubleSlots() > gui.getActualMaxBaubleSlots() ? -9 : 0;
            int add2 = gui.getBaubleSlots() > gui.getActualMaxBaubleSlots() ? 18 : 0;
            return Collections.singletonList(new Rectangle(gui.getGuiLeft() - 28, gui.getGuiTop() + add, 28, gui.getMaxY() + 9 + add2));
        }
    }
}
