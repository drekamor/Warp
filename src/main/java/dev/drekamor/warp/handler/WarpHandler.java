package dev.drekamor.warp.handler;

import dev.drekamor.warp.WarpPlugin;
import dev.drekamor.warp.util.Warp;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static dev.drekamor.warp.util.EnumUtil.getGamemode;

public class WarpHandler {
    private final WarpPlugin plugin;

    public WarpHandler (WarpPlugin plugin) {
        this.plugin = plugin;
    }

    public List<String> getWarps() {
        return plugin.getCache().getWarpIndex();
    }

    public boolean warp(CommandSender sender, String name) {
        if(!plugin.getCache().getWarpIndex().contains(name)) {
            sender.sendMessage("Warp %s does not exist".formatted(name));
            return true;
        }

        Warp warp = plugin.getCache().getWarp(name);
        Player player = (Player) sender;

        if(warp == null) {
            plugin.warning("Failed to retrieve a warp assumed to be available");
            sender.sendMessage("Failed to warp to %s".formatted(name));
            return true;
        }

        Location location = new Location(
                Bukkit.getWorld(warp.world()),
                warp.x(),
                warp.y(),
                warp.z(),
                warp.yaw(),
                warp.pitch()
        );
        GameMode gameMode = getGamemode(warp.gamemode());

        player.teleport(location);
        player.setGameMode(gameMode);
        plugin.getCache().setPlayerLocation(player, warp);

        return true;
    }
}
