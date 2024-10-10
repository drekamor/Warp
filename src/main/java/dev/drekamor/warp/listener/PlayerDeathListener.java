package dev.drekamor.warp.listener;

import dev.drekamor.warp.WarpPlugin;
import dev.drekamor.warp.util.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final WarpPlugin plugin;

    public PlayerDeathListener(WarpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        if(plugin.getCache().getPlayerLocation(event.getPlayer()) == null) {
            return;
        }

        Warp warp = plugin.getCache().getPlayerLocation(event.getPlayer());
        Location location = new Location(
                Bukkit.getWorld(warp.world()),
                warp.x(),
                warp.y(),
                warp.z(),
                warp.yaw(),
                warp.pitch()
        );

        event.getPlayer().setRespawnLocation(location);
    }
}
