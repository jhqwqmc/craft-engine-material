package cn.gtemc.craftengine;

import cn.gtemc.craftengine.util.MaterialHelper;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBuiltInRegistries;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class CustomMaterial extends JavaPlugin {
    private static CustomMaterial instance;

    @SuppressWarnings("unchecked")
    @Override
    public void onLoad() {
        instance = this;
        getLogger().info("The Material enumeration is being dynamically added...");
        try {
            for (Object block : (Iterable<Object>) MBuiltInRegistries.BLOCK) {
                if (MaterialHelper.addBlockToMaterial(block)) {
                    MaterialHelper.createBlockMaterial(block);
                }
            }
        } catch (Throwable e) {
            getLogger().log(Level.WARNING, """
                    Failed to add the Material enumeration dynamically.
                    You may be able to fix this problem by adding the following command-line argument \
                    directly after the 'java' command in your start script:\s
                    '--add-opens java.base/java.lang=ALL-UNNAMED'
                    """, e);
            return;
        }
        getLogger().info("The dynamic addition of the Material enumeration is complete.");
    }

    public static CustomMaterial instance() {
        return instance;
    }
}
