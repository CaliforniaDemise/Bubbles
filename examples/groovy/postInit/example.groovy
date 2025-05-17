// Adds an additional ring slot when player gets the 'Mining Stone' advancement.
event_manager.listen { net.minecraftforge.event.entity.player.AdvancementEvent event -> {
    if (event.advancement.id.path == "story/mine_stone") event.entityPlayer.growBaubleSlot(baubleType("baubles:ring"), 1)
}}