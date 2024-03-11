package net.multylands.duels.object;

public class DuelRestrictions {
    boolean isBowAllowed;
    boolean isNotchAllowed;
    boolean isPotionsAllowed;
    boolean isComplete;
    boolean isGoldenAppleAllowed;
    boolean isShieldsAllowed;
    boolean isTotemsAllowed;
    boolean isElytraAllowed;
    boolean isEnderPearlAllowed;
    boolean isKeepInventoryEnabled;

    public DuelRestrictions(boolean bowAllowed, boolean notchAllowed,
                            boolean potionsAllowed, boolean goldenAppleAllowed,
                            boolean shieldsAllowed, boolean totemsAllowed, boolean isElytraAllowed,
                            boolean isEnderPearlAllowed, boolean isComplete, boolean isKeepInventoryEnabled) {
        this.isBowAllowed = bowAllowed;
        this.isComplete = isComplete;
        this.isNotchAllowed = notchAllowed;
        this.isGoldenAppleAllowed = goldenAppleAllowed;
        this.isPotionsAllowed = potionsAllowed;
        this.isShieldsAllowed = shieldsAllowed;
        this.isTotemsAllowed = totemsAllowed;
        this.isElytraAllowed = isElytraAllowed;
        this.isEnderPearlAllowed = isEnderPearlAllowed;
        this.isKeepInventoryEnabled = isKeepInventoryEnabled;
    }

    public boolean isBowAllowed() {
        return isBowAllowed;
    }

    public boolean isElytraAllowed() {
        return isElytraAllowed;
    }

    public boolean isEnderPearlAllowed() {
        return isEnderPearlAllowed;
    }

    public boolean isKeepInventoryAllowed() {
        return isKeepInventoryEnabled;
    }

    public boolean isNotchAllowed() {
        return isNotchAllowed;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isPotionsAllowed() {
        return isPotionsAllowed;
    }

    public boolean isGoldenAppleAllowed() {
        return isGoldenAppleAllowed;
    }

    public boolean isShieldsAllowed() {
        return isShieldsAllowed;
    }

    public boolean isTotemsAllowed() {
        return isTotemsAllowed;
    }

    public void setBowAllowed(boolean yesOrNot) {
        isBowAllowed = yesOrNot;
    }

    public void setNotchAllowed(boolean yesOrNot) {
        isNotchAllowed = yesOrNot;
    }

    public void setPotionsAllowed(boolean yesOrNot) {
        isPotionsAllowed = yesOrNot;
    }

    public void setComplete(boolean yesOrNot) {
        isComplete = yesOrNot;
    }

    public void setGoldenAppleAllowed(boolean yesOrNot) {
        isGoldenAppleAllowed = yesOrNot;
    }

    public void setShieldsAllowed(boolean yesOrNot) {
        isShieldsAllowed = yesOrNot;
    }

    public void setTotemsAllowed(boolean yesOrNot) {
        isTotemsAllowed = yesOrNot;
    }

    public void setElytraAllowed(boolean yesOrNot) {
        isElytraAllowed = yesOrNot;
    }
    public void setKeepInventoryAllowed(boolean yesOrNot) {
        isKeepInventoryEnabled = yesOrNot;
    }

    public void setEnderPearlAllowed(boolean yesOrNot) {
        isEnderPearlAllowed = yesOrNot;
    }
}
