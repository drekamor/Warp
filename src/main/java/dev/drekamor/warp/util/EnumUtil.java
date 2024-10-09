package dev.drekamor.warp.util;

import org.bukkit.GameMode;

import javax.annotation.Nullable;

public class EnumUtil {
    public static @Nullable GameMode getGamemode(String s) {
        return switch (s) {
            case "survival" -> GameMode.SURVIVAL;
            case "creative" -> GameMode.CREATIVE;
            case "spectator" -> GameMode.SPECTATOR;
            case "adventure" -> GameMode.ADVENTURE;
            default -> null;
        };
    }
}
