package baubles.common.event;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import baubles.common.init.BaubleTypes;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandBaubles extends CommandBase {
    public CommandBaubles() {}

    @NotNull
    @Override
    public String getName() {
        return "baubles";
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public String getUsage(@NotNull ICommandSender icommandsender) {
        return Baubles.MODID + ".command.usage";
    }

    @NotNull
    @Override
    public List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "help", "view", "clear", "slots");
        if (args.length == 2 && !args[0].equals("help") && !args[0].equals("slots")) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        if (args[0].equals("slots")) {
            if (args.length == 2) return getListOfStringsMatchingLastWord(args, "grow", "shrink", "set", "reset");
            if (args.length == 3) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            if (!args[1].equals("reset")) {
                if (args.length == 4) return getListOfStringsMatchingLastWord(args, BaubleTypes.getRegistryMap().keySet().stream().map(ResourceLocation::toString).collect(Collectors.toList()));
            }
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isUsernameIndex(@NotNull String[] astring, int i) {
        return i == 1;
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2 || args[0].equalsIgnoreCase("help")) help(sender);
        else {
            EntityPlayerMP player = getPlayer(server, sender, args[0].equals("slots") ? args[2] : args[1]);
            BaublesContainer container = BaublesApi.getBaublesContainer(player);
            String command = args[0];
            switch (command) {
                case "view": view(sender, args, player, container); break;
                case "clear": clear(sender, args, player, container); break;
                case "slots": slots(sender, args, player, container); break;
                default: sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.invalid"));
            }
        }
    }

    private static void help(@NotNull ICommandSender sender) {
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.help.desc"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.help.usage"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.view.desc"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.view.usage"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.clear.desc"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.clear.usage"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.grow.desc"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.grow.usage"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.shrink.desc"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.shrink.usage"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.set.desc"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.set.usage"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.reset.desc"));
        sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.slots.reset.usage"));
    }

    private static void view(@NotNull ICommandSender sender, String[] args, EntityPlayerMP player, IBaublesItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            IBauble bauble = Objects.requireNonNull(BaublesApi.getBauble(stack));
            IBaubleType bt = bauble.getType(stack);
            sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.view.result", i, I18n.translateToLocal(bt.getTranslationKey()), stack.getDisplayName()));
        }
    }

    private static void clear(@NotNull ICommandSender sender, String[] args, EntityPlayerMP player, IBaublesItemHandler handler) {
        if (args.length >= 3) {
            int slot = -1;
            try { slot = Integer.parseInt(args[2]); } catch (Exception e) { }
            if (slot < 0 || slot >= handler.getSlots()) {
                sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.clear.slot_error", slot));
            } else {
                handler.setStackInSlot(slot, ItemStack.EMPTY);
                sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.clear.result"));
            }
        }
        else {
            for (int i = 0; i < handler.getSlots(); i++) {
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
            sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.clear.result"));
        }
    }

    private static void slots(@NotNull ICommandSender sender, String[] args, EntityPlayerMP player, BaublesContainer container) {
        if (args.length < 3) return;
        if (!args[1].equals("reset") && args.length < 5) {
            sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.not_enough_arguments"));
            return;
        }
        switch (args[1]) {
            case "reset": {
                container.reset();
                sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.applied"));
                break;
            }
            case "set": {
                String location = args[3];
                if (!location.contains(":")) location = "baubles:" + location;
                IBaubleType type = BaubleTypes.get(new ResourceLocation(location));
                int slot;
                slot = Integer.parseInt(args[4]);
                container.set(type, slot);
                sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.applied"));
                break;
            }
            case "grow": {
                String location = args[3];
                if (!location.contains(":")) location = "baubles:" + location;
                IBaubleType type = BaubleTypes.get(new ResourceLocation(location));
                int slot;
                slot = Integer.parseInt(args[4]);
                container.grow(type, slot);
                sender.sendMessage(new TextComponentTranslation(Baubles.MODID + ".command.applied"));
                break;
            }
            case "shrink": {
                String location = args[3];
                if (!location.contains(":")) location = "baubles:" + location;
                IBaubleType type = BaubleTypes.get(new ResourceLocation(location));
                int slot;
                slot = Integer.parseInt(args[4]);
                container.shrink(type, slot);
                break;
            }
        }
    }
}
