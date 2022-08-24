package net.powerscale.forge;

import net.fabricmc.loader.api.fake.FabricLoader;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public class PlatformImpl {
    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void registerLoottableModifier(BiConsumer<Identifier, LootTable.Builder> event) {
        System.out.println("registerLoottableModifier not implimented");
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            event.accept(id, tableBuilder);
        });
    }
}
