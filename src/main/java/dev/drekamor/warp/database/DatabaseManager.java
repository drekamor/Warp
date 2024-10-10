package dev.drekamor.warp.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.drekamor.warp.WarpPlugin;
import dev.drekamor.warp.util.Warp;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseManager {
    private final WarpPlugin plugin;
    private final HikariDataSource dataSource;

    public DatabaseManager (WarpPlugin plugin, DatabaseCredentials credentials) {
        this.plugin = plugin;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://%s:%s/%s".formatted(credentials.getHost(), credentials.getPort(), credentials.getDatabase()));
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
        config.setMaximumPoolSize(credentials.getPoolSize());
        config.setConnectionTimeout(credentials.getConnectionTimeout());
        config.setIdleTimeout(credentials.getIdleTimeout());
        config.setMaxLifetime(credentials.getMaxLifetime());

        this.dataSource = new HikariDataSource(config);
        this.plugin.info("Initialised a database connection");

        initialiseTables();
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            plugin.severe("Failed to obtain a data source");
            plugin.severe(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private void initialiseTables() {
        final Connection connection = this.getConnection();
        if(connection == null) {
            plugin.severe("Failed to obtain a connection. Aborting table initialisation");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `warps` (name VARCHAR(64) UNIQUE PRIMARY KEY, world VARCHAR(64), x REAL, y REAL, z REAL, pitch REAL, yaw REAL, gamemode VARCHAR(16));").execute();
                    connection.close();
                } catch (SQLException e) {
                    plugin.severe("Failed to initialise tables");
                    plugin.severe(Arrays.toString(e.getStackTrace()));
                }
            }
        });
    }

    public HashMap<String, Warp> getWarps() {
        Connection connection = this.getConnection();
        HashMap<String, Warp> warps = new HashMap<>();
        if(connection == null) {
            plugin.severe("Failed to obtain a connection. Aborting retrieving warps");
            return warps;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM warps;");
            ResultSet results = statement.executeQuery();
            while(results.next()) {
                warps.put(
                        results.getString("name"),
                        new Warp(
                                results.getString("name"),
                                results.getString("world"),
                                results.getDouble("x"),
                                results.getDouble("y"),
                                results.getDouble("z"),
                                results.getFloat("pitch"),
                                results.getFloat("yaw"),
                                results.getString("gamemode")
                        )
                );
            }
            connection.close();
        } catch (SQLException e) {
            plugin.severe("Failed to retrieve warps");
            plugin.severe(Arrays.toString(e.getStackTrace()));
        }
        return warps;
    }

    public List<String> getWarpNames() {
        Connection connection = this.getConnection();
        List<String> warps = new ArrayList<>();
        if(connection == null) {
            plugin.severe("Failed to obtain a connection. Aborting retrieving warp names");
            return warps;
        }
        try {
            ResultSet results = connection.prepareStatement("SELECT name FROM warps;").executeQuery();
            while (results.next()) {
                warps.add(results.getString("name"));
            }
            connection.close();
        } catch (SQLException e) {
            plugin.severe("Failed to retrieve warp names");
            plugin.severe(Arrays.toString(e.getStackTrace()));
        }
        return warps;
    }

    public boolean addWarp(Warp warp) {
        Connection connection = this.getConnection();
        if(connection == null) {
            plugin.severe("Failed to obtain a connection. Aborting saving warp %s".formatted(warp.name()));
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO warps(name, world, x, y, z, pitch, yaw, gamemode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, warp.name());
            statement.setString(2, warp.world());
            statement.setDouble(3, warp.x());
            statement.setDouble(4, warp.y());
            statement.setDouble(5, warp.z());
            statement.setFloat(6, warp.pitch());
            statement.setFloat(7, warp.yaw());
            statement.setString(8, warp.gamemode());
            statement.execute();
            connection.close();
            return true;
        } catch (SQLException e) {
            plugin.severe("Failed to save warp %s".formatted(warp.name()));
            plugin.severe(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    public boolean deleteWarp(String name) {
        Connection connection = this.getConnection();
        if(connection == null) {
            plugin.severe("Failed to obtain a connection. Aborting deleting warp %s".formatted(name));
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM warps WHERE name=?;");
            statement.setString(1, name);
            statement.execute();
            connection.close();
            return true;
        } catch (SQLException e) {
            plugin.severe("Failed to delete warp %s".formatted(name));
            plugin.severe(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }
}
