package cn.gtemc.craftengine;

import cn.gtemc.craftengine.util.MaterialHelper;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBuiltInRegistries;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class CustomMaterialBootstrap implements PluginBootstrap {

    @SuppressWarnings("unchecked")
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        if (!isDatapackDiscoveryAvailable()) return;
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, e -> {
            context.getLogger().info("The Material enumeration is being dynamically added...");
            try {
                for (Object block : (Iterable<Object>) MBuiltInRegistries.BLOCK) {
                    MaterialHelper.createBlockMaterial(block);
                }
            } catch (Throwable ex) {
                context.getLogger().warn("""
                        Failed to add the Material enumeration dynamically.
                        You may be able to fix this problem by adding the following command-line argument \
                        directly after the 'java' command in your start script:\s
                        '--add-opens java.base/java.lang=ALL-UNNAMED'
                        """, ex);
                return;
            }
            context.getLogger().info("The dynamic addition of the Material enumeration is complete.");
        });
    }

    private static boolean isDatapackDiscoveryAvailable() {
        try {
            Class<?> eventsClass = Class.forName("io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents");
            eventsClass.getField("DATAPACK_DISCOVERY");
            return true;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            return false;
        }
    }
}
