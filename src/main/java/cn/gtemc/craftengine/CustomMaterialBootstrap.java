package cn.gtemc.craftengine;

import cn.gtemc.craftengine.util.MaterialHelper;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBuiltInRegistries;
import net.momirealms.craftengine.core.util.ReflectionUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CustomMaterialBootstrap implements PluginBootstrap {
    private static boolean isInitialized = false;

    @SuppressWarnings({"unchecked", "deprecation", "all"})
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        if (!isDatapackDiscoveryAvailable()) return;
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, e -> {
            if (isInitialized) return;
            isInitialized = true;
            context.getLogger().info("The Material enumeration is being dynamically added...");
            try {
                List<Material> added = new ArrayList<>();
                for (Object block : (Iterable<Object>) MBuiltInRegistries.BLOCK) {
                    Material material = MaterialHelper.createBlockMaterial(block);
                    if (material != null) added.add(material);
                }
                List<Material> materials = new ArrayList<>(Arrays.stream((Material[]) MaterialHelper.field$Material$VALUES.get(null)).toList());
                materials.addAll(added);
                ReflectionUtils.UNSAFE.putObjectVolatile(Material.class, ReflectionUtils.UNSAFE.staticFieldOffset(MaterialHelper.field$Material$VALUES), materials.toArray(new Material[0]));
                MaterialHelper.field$Class$enumConstantDirectory.set(Material.class, null);
                MaterialHelper.field$Class$enumConstants.set(Material.class, null);
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
