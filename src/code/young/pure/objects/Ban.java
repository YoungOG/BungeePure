package code.young.pure.objects;

import java.util.UUID;

public class Ban {

    UUID bannedUUID;
    String banner;
    String reason;
    String date;

    public Ban(UUID bannedUUID, String banner, String reason, String date) {
        this.bannedUUID = bannedUUID;
        this.banner = banner;
        this.reason = reason;
        this.date = date;
    }

    public UUID getBannedUUID() {
        return bannedUUID;
    }

    public String getBanner() {
        return banner;
    }

    public void setBannedUUID(UUID bannedUUID) {
        this.bannedUUID = bannedUUID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
