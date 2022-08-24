package net.powerscale;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public class Platform {
    @ExpectPlatform
    public static boolean isModLoaded(String modid) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean registerLoottableModifier(BiConsumer<Identifier, LootTable.Builder> event) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }
}
