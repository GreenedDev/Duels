package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;

import java.util.UUID;

public class RequestUtils {
    public static DuelRequest getRequestOfTheDuelPlayerIsIn(UUID playerUUID) {
        DuelRequest request = null;
        UUID targetUUID = Duels.playerToOpponentInGame.get(playerUUID);
        if (targetUUID == null) {
            return null;
        }
        if (Duels.requestsReceiverToSenders.get(playerUUID) == null) {
            return null;
        }
        for (DuelRequest requests : Duels.requestsReceiverToSenders.get(playerUUID)) {
            if (!((requests.getSender() == playerUUID || requests.getTarget() == playerUUID) && ((requests.getSender() == targetUUID || requests.getTarget() == targetUUID)))) {
                continue;
            }
            request = requests;
        }
        if (request == null) {
            for (DuelRequest requests : Duels.requestsSenderToReceivers.get(playerUUID)) {
                if (!((requests.getSender() == playerUUID || requests.getTarget() == playerUUID) && ((requests.getSender() == targetUUID || requests.getTarget() == targetUUID)))) {
                    continue;
                }
                request = requests;
            }
        }
        if (request == null) {
            return null;
        }
        return request;
    }
    public static boolean isInGame(DuelRequest request) {
        if (request == null) {
            return false;
        }
        return request.getIsInGame();
    }
    public static DuelRequest getRequestForCommands(UUID receiverUUID, UUID senderUUID) {
        DuelRequest request = null;
        if (Duels.requestsReceiverToSenders.get(receiverUUID) == null) {
            return null;
        }
        for (DuelRequest requests : Duels.requestsReceiverToSenders.get(receiverUUID)) {
            if (!(requests.getSender() == senderUUID && requests.getTarget() == receiverUUID)) {
                continue;
            }
            request = requests;
        }
        return request;
    }
}
