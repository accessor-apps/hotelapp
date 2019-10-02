package accessor.hotel.data;

public class GuestCount {

    public final int totalCount;
    public final int currentCount;
    public final int comingCount;

    public GuestCount(int totalCount, int currentCount, int comingCount) {
        this.totalCount = totalCount;
        this.currentCount = currentCount;
        this.comingCount = comingCount;
    }
}