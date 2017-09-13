package code.young.pure.objects;


public class IPBan {

    String banner;
    String address;
    String reason;
    String date;

    public IPBan(String address, String banner, String reason, String date) {
        this.address = address;
        this.banner = banner;
        this.reason = reason;
        this.date = date;
    }

    public String getBannedAddress() {
        return address;
    }

    public String getBanner() {
        return banner;
    }

    public void setBannedAddress(String address) {
        this.address = address;
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
