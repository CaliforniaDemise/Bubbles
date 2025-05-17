// Adds a body slot when player crafts planks
events.onPlayerCrafted(function(event as crafttweaker.event.PlayerCraftedEvent){
    if (<ore:plankWood> has event.output) {
        event.player.growBaubleSlot("baubles:body", 1);
    }
});