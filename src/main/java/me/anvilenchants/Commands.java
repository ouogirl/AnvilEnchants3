package me.anvilenchants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class Commands implements CommandExecutor {

    private final AnvilEnchants plugin;

    public Commands(AnvilEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is for players only.");
            return true;
        }
        Player p = (Player) sender;

        if (!p.hasPermission("anvilenchants.give")) {
            p.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(ChatColor.YELLOW + "Usage: /giveenchanted <sharp6|unb4|prot5>");
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("sharp6")) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 6, true);
            book.setItemMeta(meta);
            p.getInventory().addItem(book);
            p.sendMessage(ChatColor.GREEN + "Given Sharpness 6 book.");
            return true;
        } else if (sub.equals("unb4")) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(Enchantment.DURABILITY, 4, true);
            book.setItemMeta(meta);
            p.getInventory().addItem(book);
            p.sendMessage(ChatColor.GREEN + "Given Unbreaking 4 book.");
            return true;
        } else if (sub.equals("prot5")) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
            book.setItemMeta(meta);
            p.getInventory().addItem(book);
            p.sendMessage(ChatColor.GREEN + "Given Protection 5 book.");
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "Unknown subcommand.");
            return true;
        }
    }
}
