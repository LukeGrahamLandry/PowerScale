package net.powerscale.forge;

import net.minecraftforge.fml.common.Mod;
import net.powerscale.PowerScale;

@Mod(PowerScale.MODID)
public class PowerScaleForge {
    public PowerScaleForge() {
        new PowerScale().onInitialize();
    }
}
