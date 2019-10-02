package accessor.hotel.model;

public class Room {
    public int id;
    public int number;
    public int guestId;
    public String guest;
    public String from;
    public String arrivalDate;
    public String departureDate;
    public String amount;
    public String state;
    public int isClosed;

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getGuest() {
        return guest;
    }

    public String getFrom() {
        return from;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getAmount() {
        return amount;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }
}