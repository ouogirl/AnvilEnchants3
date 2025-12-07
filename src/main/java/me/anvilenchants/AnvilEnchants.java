package me.anvilenchants;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilEnchants extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("AnvilEnchants enabled");
        Bukkit.getPluginManager().registerEvents(new AnvilListener(this), this);
        this.getCommand("giveenchanted").setExecutor(new Commands(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("AnvilEnchants disabled");
    }
}
