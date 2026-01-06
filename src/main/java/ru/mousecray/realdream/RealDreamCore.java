package ru.mousecray.realdream;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("RealDreamCore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1000)
public class RealDreamCore implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        System.out.println("[RealDream] Registering mixin config: mixins.realdream.json");
        List<String> configs = new ArrayList<>();
        configs.add("mixins." + Tags.MOD_ID + ".json");
        return configs;
    }

    @Override public String[] getASMTransformerClass()         { return new String[0]; }
    @Override public String getModContainerClass()             { return null; }
    @Nullable @Override public String getSetupClass()          { return null; }
    @Override public void injectData(Map<String, Object> data) { }
    @Override public String getAccessTransformerClass()        { return null; }
}