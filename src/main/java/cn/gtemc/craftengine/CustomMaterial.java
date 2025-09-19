package cn.gtemc.craftengine;

import cn.gtemc.craftengine.util.MaterialHelper;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBuiltInRegistries;
import net.momirealms.craftengine.core.util.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class CustomMaterial extends JavaPlugin {
    private static CustomMaterial instance;

    @SuppressWarnings({"unchecked", "deprecation", "all"})
    @Override
    public void onLoad() {
        instance = this;
        getLogger().info("The Material enumeration is being dynamically added...");
        try {
            List<Material> added = new ArrayList<>();
            for (Object block : (Iterable<Object>) MBuiltInRegistries.BLOCK) {
                if (MaterialHelper.addBlockToMaterial(block)) {
                    Material material = MaterialHelper.createBlockMaterial(block);
                    if (material != null) added.add(material);
                }
            }
            if (!added.isEmpty()) {
                List<Material> materials = new ArrayList<>(Arrays.stream((Material[]) MaterialHelper.field$Material$VALUES.get(null)).toList());
                materials.addAll(added);
                ReflectionUtils.UNSAFE.putObjectVolatile(Material.class, ReflectionUtils.UNSAFE.staticFieldOffset(MaterialHelper.field$Material$VALUES), materials.toArray(new Material[0]));
                MaterialHelper.field$Class$enumConstantDirectory.set(Material.class, null);
                MaterialHelper.field$Class$enumConstants.set(Material.class, null);
                for (Material material : added) {
                    MaterialHelper.addBlockToMaterial(material);
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
