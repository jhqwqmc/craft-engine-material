package cn.gtemc.craftengine.util;

import cn.gtemc.craftengine.CustomMaterial;
import com.google.common.base.Suppliers;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBuiltInRegistries;
import net.momirealms.craftengine.bukkit.util.BukkitReflectionUtils;
import net.momirealms.craftengine.bukkit.util.KeyUtils;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.data.BlockData;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

public class MaterialHelper {
    public static final AtomicInteger MATERIAL_LENGTH = new AtomicInteger(Material.values().length);
    public static final AtomicInteger ID = new AtomicInteger(100000);
    public static final Field field$Class$enumConstantDirectory = requireNonNull(ReflectionUtils.getDeclaredField(Class.class, "enumConstantDirectory"));
    public static final Field field$Class$enumConstants = requireNonNull(ReflectionUtils.getDeclaredField(Class.class, "enumConstants"));
    public static final Field field$Enum$name = requireNonNull(ReflectionUtils.getDeclaredField(Enum.class, "name"));
    public static final Field field$Enum$ordinal = requireNonNull(ReflectionUtils.getDeclaredField(Enum.class, "ordinal"));
    public static final Field field$Material$id = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "id"));
    public static final Field field$Material$ctor = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "ctor"));
    public static final Field field$Material$maxStack = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "maxStack"));
    public static final Field field$Material$durability = ReflectionUtils.getDeclaredField(Material.class, "durability");
    public static final Field field$Material$data = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "data"));
    public static final Field field$Material$legacy = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "legacy"));
    public static final Field field$Material$key = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "key"));
    public static final Field field$Material$isBlock = ReflectionUtils.getDeclaredField(Material.class, "isBlock");
    public static final Field field$Material$itemType = ReflectionUtils.getDeclaredField(Material.class, "itemType");
    public static final Field field$Material$blockType = ReflectionUtils.getDeclaredField(Material.class, "blockType");
    public static final Field field$Material$BY_NAME = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "BY_NAME"));
    public static final Field field$Material$VALUES = requireNonNull(ReflectionUtils.getDeclaredField(Material.class, "$VALUES"));
    public static final Class<?> clazz$CraftMagicNumbers = requireNonNull(ReflectionUtils.getClazz(BukkitReflectionUtils.assembleCBClass("util.CraftMagicNumbers")));
    public static final Field field$CraftMagicNumbers$BLOCK_MATERIAL = requireNonNull(ReflectionUtils.getDeclaredField(clazz$CraftMagicNumbers, "BLOCK_MATERIAL"));
    public static final Field field$CraftMagicNumbers$MATERIAL_BLOCK = requireNonNull(ReflectionUtils.getDeclaredField(clazz$CraftMagicNumbers, "MATERIAL_BLOCK"));
    @SuppressWarnings("UnstableApiUsage")
    public static final Field field$SimpleRegistry$map = requireNonNull(ReflectionUtils.getDeclaredField(Registry.SimpleRegistry.class, "map"));

    @SuppressWarnings({"unchecked", "deprecation", "all"})
    public static Material createBlockMaterial(Object block) {
        try {
            Key blockId = KeyUtils.resourceLocationToKey(FastNMS.INSTANCE.method$Registry$getKey(MBuiltInRegistries.BLOCK, block));
            if (blockId.namespace().equals("minecraft")) return null;

            Material enumInstance = (Material) ReflectionUtils.UNSAFE.allocateInstance(Material.class);

            // 初始化 Enum 字段
            field$Enum$name.set(enumInstance, blockId.asString().replace(":", "_").toUpperCase(Locale.ROOT));
            field$Enum$ordinal.set(enumInstance, MATERIAL_LENGTH.getAndIncrement());

            // 初始化 Material 字段
            field$Material$id.setInt(enumInstance, ID.getAndIncrement());
            field$Material$ctor.set(enumInstance, null);
            field$Material$maxStack.setInt(enumInstance, 64);
            if (field$Material$durability != null) {
                field$Material$durability.setShort(enumInstance, (short) 0);
            }
            field$Material$data.set(enumInstance, BlockData.class);
            field$Material$legacy.setBoolean(enumInstance, false);
            field$Material$key.set(enumInstance, KeyUtils.toNamespacedKey(blockId));
            if (field$Material$isBlock != null) {
                field$Material$isBlock.setBoolean(enumInstance, true);
            }
            if (field$Material$itemType != null) {
                field$Material$itemType.set(enumInstance, Suppliers.memoize(() -> null));
            }
            if (field$Material$blockType != null) {
                field$Material$blockType.set(enumInstance, Suppliers.memoize(() -> Registry.BLOCK.get(KeyUtils.toNamespacedKey(blockId))));
            }

            return enumInstance;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "deprecation", "all"})
    public static boolean addBlockToMaterial(Object block) {
        try {
            Key blockId = KeyUtils.resourceLocationToKey(FastNMS.INSTANCE.method$Registry$getKey(MBuiltInRegistries.BLOCK, block));
            if (blockId.namespace().equals("minecraft")) return false;
            String blockName = blockId.asString().replace(":", "_").toUpperCase(Locale.ROOT);
            Material existingMaterial = null;
            for (Material material : Material.values()) {
                if (!material.name().equals(blockName)) continue;
                existingMaterial = material;
            }
            if (existingMaterial == null) return true;
            ((Map<String, Material>) field$Material$BY_NAME.get(null)).put(existingMaterial.name(), existingMaterial);
            ((Map<Object, Material>) field$CraftMagicNumbers$BLOCK_MATERIAL.get(null)).put(block, existingMaterial);
            ((Map<Material, Object>) field$CraftMagicNumbers$MATERIAL_BLOCK.get(null)).put(existingMaterial, block);
            Map<NamespacedKey, Material> map = new HashMap<>((Map<NamespacedKey, Material>) field$SimpleRegistry$map.get(Registry.MATERIAL));
            map.put(KeyUtils.toNamespacedKey(blockId), existingMaterial);
            field$SimpleRegistry$map.set(Registry.MATERIAL, Collections.unmodifiableMap(map));
            return false;
        } catch (Throwable e) {
            CustomMaterial.instance().getLogger().log(Level.WARNING, "Failed to add block to Material", e);
            return false;
        }
    }
}
