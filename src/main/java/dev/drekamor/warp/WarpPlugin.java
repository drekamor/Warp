package dev.drekamor.warp;

import dev.drekamor.warp.command.WarpCommand;
import dev.drekamor.warp.command.WarpsCommand;
import dev.drekamor.warp.config.ConfigurationManager;
import dev.drekamor.warp.database.DatabaseCredentials;
import dev.drekamor.warp.database.DatabaseManager;
import dev.drekamor.warp.handler.WarpHandler;
import dev.drekamor.warp.handler.WarpsHandler;
import dev.drekamor.warp.listener.PlayerDeathListener;
import dev.drekamor.warp.util.Cache;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpPlugin extends JavaPlugin {
    static {
        registerSerialisations();
    }

    private ConfigurationManager configManager;
    private DatabaseManager databaseManager;
    private Cache cache;

    private WarpsHandler warpsHandler;
    private WarpHandler warpHandler;

    @Override
    public void onEnable() {
        configManager = new ConfigurationManager(this.getConfig());
        databaseManager = new DatabaseManager(this, configManager.getDatabaseCredentials());

        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                cache = new Cache(databaseManager.getWarpNames(), databaseManager.getWarps());
            }
        });

        warpsHandler = new WarpsHandler(this);
        warpHandler = new WarpHandler(this);

        this.getCommand("warps").setExecutor(new WarpsCommand(this.warpsHandler));
        this.getCommand("warp").setExecutor(new WarpCommand(this.warpHandler));

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }

    public Cache getCache() {
        return cache;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    private static void registerSerialisations() {
        ConfigurationSerialization.registerClass(DatabaseCredentials.class);
    }

    public void info(String string) {
        this.getLogger().info(string);
    }

    public void warning(String string) {
        this.getLogger().warning(string);
    }

    public void severe(String string) {
        this.getLogger().severe(string);
    }
}
