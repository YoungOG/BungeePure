package code.young.pure.objects;

public class TempIPBan {

    String banner;
    String address;
    String reason;
    long bannedUntil;
    String date;

    public TempIPBan(String address, String banner, String reason, long bannedUntil, String date) {
        this.address = address;
        this.banner = banner;
        this.reason = reason;
        this.bannedUntil = bannedUntil;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public String getBanner() {
        return banner;
    }

    public void setAddress(String address) {
        this.address = address;
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
