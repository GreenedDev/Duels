package net.multylands.duels.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class PAPIDuels extends PlaceholderExpansion {

    private final Duels plugin;

    public PAPIDuels(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "GreenedDev";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "duel";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("opponent")) {
            if (!player.isOnline()) {
                return "Error #1";
            }
            UUID playerUUID = player.getUniqueId();
            DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
            if (!RequestUtils.isInGame(request)) {
                return "You aren't in the duel";
            }
            UUID opponentUUID = request.getOpponent(playerUUID);
            Player opponent = Bukkit.getPlayer(opponentUUID);
            if (opponent == null) {
                return "opponent isn't online";
            }
            return opponent.getName();
        }
        if (params.equalsIgnoreCase("opponent_ping")) {
            if (!player.isOnline()) {
                return "Error #1";
            }
            UUID playerUUID = player.getUniqueId();
            DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
            if (!RequestUtils.isInGame(request)) {
                return "You aren't in the duel";
            }
            UUID opponentUUID = request.getOpponent(playerUUID);
            Player opponent = Bukkit.getPlayer(opponentUUID);
            if (opponent == null) {
                return "opponent isn't online";
            }
            return String.valueOf(opponent.getPing());
        }
        if (params.equalsIgnoreCase("time_left")) {
            if (!player.isOnline()) {
                return "Error #1";
            }
            UUID playerUUID = player.getUniqueId();
            DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
            if (!RequestUtils.isInGame(request)) {
                return "You aren't in the duel";
            }
            Instant timeWhenDuelRunsOutOfTime = request.getRunOutOfTimeInstant();
            Instant now = Instant.now();

            long secondsBetween = now.until(timeWhenDuelRunsOutOfTime, ChronoUnit.SECONDS);
            return convertSecondsToHMS(secondsBetween);
        }
        return Chat.Color(plugin.getConfig().getString("glowing-wrong-placeholder"));
    }

    public String convertSecondsToHMS(long seconds) {
        long H = (seconds / 60) / 60;  // covert total seconds to hours
        long M = (seconds / 60) % 60;  // Calculate the remaining minutes
        long S = seconds % 60;         // Calculate the remaining seconds

        String format = plugin.getConfig().getString("placeholders.time.format");
        format = format.replace("%hours%", String.valueOf(H));
        format = format.replace("%minutes%", String.valueOf(M));
        format = format.replace("%seconds%", String.valueOf(S));
        return format;
    }
}