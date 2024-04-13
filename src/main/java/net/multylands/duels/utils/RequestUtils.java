package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;

import java.util.UUID;

public class RequestUtils {
    public static DuelRequest getRequestOfTheDuelPlayerIsIn(UUID playerUUID) {
        UUID targetUUID = Duels.playerToOpponentInGame.get(playerUUID);
        if (targetUUID == null) {
            return null;
        }
        if (Duels.requestsReceiverToSenders.get(playerUUID) == null) {
            return null;
        }
        for (DuelRequest request : Duels.requestsReceiverToSenders.get(playerUUID)) {
            if (!((request.getSender() == playerUUID || request.getTarget() == playerUUID) && ((request.getSender() == targetUUID || request.getTarget() == targetUUID)))) {
                continue;
            }
            return request;
        }
        for (DuelRequest request : Duels.requestsSenderToReceivers.get(playerUUID)) {
            if (!((request.getSender() == playerUUID || request.getTarget() == playerUUID) && ((request.getSender() == targetUUID || request.getTarget() == targetUUID)))) {
                continue;
            }
            return request;
        }
        return null;
    }

    public static boolean isInGame(DuelRequest request) {
        if (request == null) {
            return false;
        }
        return request.getIsInGame();
    }

    public static DuelRequest getRequestForCommands(UUID receiverUUID, UUID senderUUID) {
        if (Duels.requestsReceiverToSenders.get(receiverUUID) == null) {
            return null;
        }
        for (DuelRequest request : Duels.requestsReceiverToSenders.get(receiverUUID)) {
            if (!(request.getSender() == senderUUID && request.getTarget() == receiverUUID)) {
                continue;
            }
            return request;
        }
        return null;
    }
}
