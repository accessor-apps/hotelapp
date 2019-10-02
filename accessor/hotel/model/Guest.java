package accessor.hotel.model;

import accessor.hotel.Util;
import java.time.LocalDateTime;

public class Guest {
    public int id;
    public String name;
    public String phone;
    public LocalDateTime arrivalDate;
    public LocalDateTime departureDate;
    public int roomId;
    public int room;
    public String from;
    public int serviceId;
    public String service;
    public double price;
    public String amount;
    public String note;
    public String status;
    public int statusId;

    public int getId() {
        return id+1;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getArrivalDate() {
        return Util.datetimeToString(arrivalDate);
    }

    public String getDepartureDate() {
        return Util.datetimeToString(departureDate);
    }

    public int getRoomId() {
        return roomId;
    }

    public int getRoom() {
        return room;
    }

    public String getFrom() {
        return from;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getService() {
        return service;
    }

    public double getPrice() {
        return price;
    }

    public String getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }
    
    
}