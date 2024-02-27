package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RequestUtils {
    public static DuelRequest getRequestOfTheDuelPlayerIsIn(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (!Duels.playerToOpponentInGame.containsKey(playerUUID)) {
            return null;
        }
        //bruh playertoopponent replaces
        UUID opponentUUID = Duels.playerToOpponentInGame.get(playerUUID);
        if (!Duels.requests.containsKey(playerUUID) && !Duels.requests.containsKey(opponentUUID)) {
            return null;
        }
        if (Duels.requests.containsKey(playerUUID)) {
            if (Duels.requests.get(playerUUID).getOpponent(playerUUID) == opponentUUID) {
                return Duels.requests.get(playerUUID);
            }
        }
        if (Duels.requests.containsKey(opponentUUID)) {
            if (Duels.requests.get(opponentUUID).getOpponent(opponentUUID) == playerUUID) {
                return Duels.requests.get(opponentUUID);
            }
        }
        return null;
    }
    public static boolean isInGame(DuelRequest request) {
        if (request == null) {
            return false;
        }
        return request.getIsInGame();
    }
}
