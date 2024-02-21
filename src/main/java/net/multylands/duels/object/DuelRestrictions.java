package net.multylands.duels.object;

public class DuelRestrictions {
    boolean isBowAllowed;
    boolean isNotchAllowed;
    boolean isPotionsAllowed;
    boolean isComplete;
    boolean isGoldenAppleAllowed;
    boolean isShieldsAllowed;
    boolean isTotemsAllowed;

    public DuelRestrictions(boolean bowAllowed, boolean notchAllowed, boolean potionsAllowed, boolean goldenAppleAllowed, boolean shieldsAllowed, boolean totemsAllowed, boolean isComplete) {
        this.isBowAllowed = bowAllowed;
        this.isComplete = isComplete;
        this.isNotchAllowed = notchAllowed;
        this.isGoldenAppleAllowed = goldenAppleAllowed;
        this.isPotionsAllowed = potionsAllowed;
        this.isShieldsAllowed = shieldsAllowed;
        this.isTotemsAllowed = totemsAllowed;
    }

    public boolean isBowAllowed() {
        return isBowAllowed;
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
    public boolean isTotemsAllowed() {return isTotemsAllowed;}

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
    public void setTotemsAllowed(boolean yesOrNot){isTotemsAllowed = yesOrNot;}
}
