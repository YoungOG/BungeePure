package code.young.pure.objects;

import java.util.UUID;

public class TempBan {

    UUID bannedUUID;
    String banner;
    String reason;
    long bannedUntil;
    String date;

    public TempBan(UUID bannedUUID, String banner, String reason, long bannedUntil, String date) {
        this.bannedUUID = bannedUUID;
        this.banner = banner;
        this.reason = reason;
        this.bannedUntil = bannedUntil;
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

    public void setBannedUntil(long bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    public Long getBannedUntil() {
        return bannedUntil;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
