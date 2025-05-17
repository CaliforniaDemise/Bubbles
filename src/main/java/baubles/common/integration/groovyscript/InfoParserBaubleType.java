package baubles.common.integration.groovyscript;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserTranslationKey;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import org.jetbrains.annotations.NotNull;

public class InfoParserBaubleType extends GenericInfoParser<IBaubleType> {

    public static final InfoParserBaubleType instance = new InfoParserBaubleType();

    @Override
    public String id() {
        return "baubleType";
    }

    @Override
    public String name() {
        return "Bauble Type";
    }

    @Override
    public String text(@NotNull IBaubleType entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.formatGenericHandler("baubleType", entry.getRegistryName().toString(), colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        IBauble bauble = BaublesApi.getBauble(info.getStack());
        if (bauble == null) return;
        IBaubleType type = bauble.getType(info.getStack());
        instance.add(info.getMessages(), type, info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), type.getTranslationKey(), info.isPrettyNbt());
    }
}
