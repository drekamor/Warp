package dev.drekamor.warp.config;

import dev.drekamor.warp.database.DatabaseCredentials;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationManager {
    private final FileConfiguration config;

    public ConfigurationManager(FileConfiguration config){
        this.config = config;
    }

    public DatabaseCredentials getDatabaseCredentials() {
        return ( DatabaseCredentials ) this.config.get("database");
    }
}
