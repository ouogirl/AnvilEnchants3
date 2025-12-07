package me.anvilenchants;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnvilListener implements Listener {

    private final AnvilEnchants plugin;

    private static final int MAX_SHARPNESS = 6;
    private static final int MAX_UNBREAKING = 4;
    private static final int MAX_PROTECTION = 5;

    public AnvilListener(AnvilEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory)) return;
        AnvilInventory inv = (AnvilInventory) event.getInventory();

        ItemStack left = inv.getItem(0);
        ItemStack right = inv.getItem(1);
        ItemStack result = event.getResult();

        if (left == null || right == null || result == null) return;

        Map<Enchantment, Integer> merged = new HashMap<>();
        collectEnchants(left, merged);
        collectEnchants(right, merged);

        // Enforce caps
        Map<Enchantment, Integer> capped = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> e : merged.entrySet()) {
            Enchantment ench = e.getKey();
            int lvl = e.getValue();
            if (ench.equals(Enchantment.DAMAGE_ALL)) {
                if (lvl > MAX_SHARPNESS) lvl = MAX_SHARPNESS;
            } else if (ench.equals(Enchantment.DURABILITY)) {
                if (lvl > MAX_UNBREAKING) lvl = MAX_UNBREAKING;
            } else if (ench.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                if (lvl > MAX_PROTECTION) lvl = MAX_PROTECTION;
            }
            capped.put(ench, lvl);
        }

        // Apply to result
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        // clear existing enchants (we'll set final ones)
        for (Enchantment e : meta.getEnchants().keySet()) {
            meta.removeEnchant(e);
        }

        for (Map.Entry<Enchantment, Integer> e : capped.entrySet()) {
            Enchantment ench = e.getKey();
            int lvl = e.getValue();
            if (result.getType().name().toLowerCase().contains("book") && meta instanceof EnchantmentStorageMeta) {
                ((EnchantmentStorageMeta) meta).addStoredEnchant(ench, lvl, true);
            } else {
                meta.addEnchant(ench, lvl, true);
            }
        }

        // Efficiency 20 -> Efficiency XX in lore
        Integer eff = capped.get(Enchantment.DIG_SPEED);
        if (eff != null && eff >= 20) {
            List<String> lore = meta.hasLore() ? meta.getLore() : new java.util.ArrayList<>();
            String line = ChatColor.GRAY + "Efficiency XX";
            if (!lore.contains(line)) lore.add(line);
            meta.setLore(lore);
        }

        result.setItemMeta(meta);
        event.setResult(result);

        // Remove Too Expensive by setting very high max repair cost (Paper feature)
        try {
            inv.setMaximumRepairCost(Integer.MAX_VALUE / 2);
        } catch (Throwable t) {
            // ignored if not supported
        }
    }

    private void collectEnchants(ItemStack item, Map<Enchantment, Integer> out) {
        if (item == null) return;
        // direct enchants
        for (Map.Entry<Enchantment, Integer> e : item.getEnchantments().entrySet()) {
            Enchantment ench = e.getKey();
            int lvl = e.getValue();
            out.put(ench, Math.max(out.getOrDefault(ench, 0), lvl));
        }
        // stored enchants on book
        if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta m = (EnchantmentStorageMeta) item.getItemMeta();
            for (Map.Entry<Enchantment, Integer> e : m.getStoredEnchants().entrySet()) {
                Enchantment ench = e.getKey();
                int lvl = e.getValue();
                out.put(ench, Math.max(out.getOrDefault(ench, 0), lvl));
            }
        }
    }

    @EventHandler
    public void onAnvilDamaged(AnvilDamagedEvent event) {
        // Prevent anvils from taking damage / chipping
        event.setCancelled(true);
    }
}
