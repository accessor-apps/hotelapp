package accessor.hotel.data;

import accessor.hotel.LogJournal;
import accessor.hotel.Util;
import accessor.hotel.model.Guest;
import accessor.hotel.model.Preference;
import accessor.hotel.model.Room;
import accessor.hotel.model.Service;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    
    public static final DatabaseHelper instance = new DatabaseHelper(new DatabaseConnection());
    
    private final List<Guest> guests;
    private final List<Service> services;
    
    private final DatabaseConnection connection;

    private Preference preference;

    public DatabaseHelper(DatabaseConnection connection) {
        this.services = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.preference = new Preference();
        this.connection = connection;
        this.connection.connect();
    }

    public Preference getPreference() throws SQLException {
        final String query = "select * from Preferences";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            preference = new Preference();
            preference.roomPrice = result.getDouble(1);
            preference.roomCount = result.getInt(2);
        }
        return preference;
    }

    public void savePreference(Preference preference) throws SQLException {
        this.preference = preference;
        try (PreparedStatement statement = connection.getConnection().prepareStatement("update Preferences set price = ?, count = ?")) {
            statement.setDouble(1, preference.roomPrice);
            statement.setInt(2, preference.roomCount);
            statement.execute();
        }
    }
    
    public List<Guest> getGuests(int currentPage, int maxItems) throws SQLException {
        final String query = "SELECT id, name, phone, arrival, departure, room_id, room_number, country, service, service_name, current_price, note, status"
                + " FROM Guests JOIN Services ON (service == service_id) JOIN Rooms ON (room == room_id) "
                + "where hidden = 0 ORDER BY id DESC limit ?, ?";
        guests.clear();

        System.out.println(query);
        System.out.println("currentPage = " + currentPage);
        System.out.println("maxItems = " + maxItems);
        
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, currentPage * maxItems);
            statement.setInt(2, maxItems);
            
            int i = 0;
            try (ResultSet result = statement.executeQuery()) {
                while(result.next()) {
                    i++;
                    guests.add(readGuest(result));
                }
            }
            System.out.println("guest count: " + i);
        }
        return guests;
    } 
    
    private Guest readGuest(ResultSet result) throws SQLException {
                Guest guest = new Guest();
                guest.id = result.getInt(1);
                guest.name = result.getString(2);
                guest.phone = result.getString(3);
                guest.arrivalDate = Util.datetimeFromString(result.getString(4));
                guest.departureDate = Util.datetimeFromString(result.getString(5));
                guest.roomId = result.getInt(6);
                guest.room = result.getInt(7);
                guest.from = result.getString(8);
                guest.serviceId = result.getInt(9);
                guest.service = result.getString(10);
                guest.price = result.getDouble(11);
                int days = Util.differenceOfDays(guest.arrivalDate, guest.departureDate);
                if (days == 0) days = 1;
                guest.amount = new DecimalFormat("#,###").format(guest.price * days) + "/" + days;
                guest.note = result.getString(12);
                int status = result.getInt(13);
                guest.statusId = status;
                switch(status) {
                    case 0: guest.status = "В пути"; break;
                    case 1: guest.status = "Прибыл"; break;
                    case 2: guest.status = "Убыл"; break;
                    default: LogJournal.error("Unknown status of guest: " + status);
                }
                return guest;
    }
    
    public Guest getGuestById(int id) throws SQLException {
        Guest guest = new Guest();
        final String query = "select id, name, arrival, departure, country, current_price from Guests where id = ?";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            guest.id = result.getInt(1);
            guest.name = result.getString(2);
            guest.arrivalDate = Util.datetimeFromString(result.getString(3));
            guest.departureDate = Util.datetimeFromString(result.getString(4));
            guest.from = result.getString(5);
            guest.price = result.getDouble(6);
            int days = Util.differenceOfDays(guest.arrivalDate, guest.departureDate);
            if (days == 0) {
                days = 1;
            }
            guest.amount = new DecimalFormat("#,###").format(guest.price * days) + "/" + days;
        }
        return guest;
    }
    
    public void addGuest(Guest guest) throws SQLException {
        final String query = "insert into Guests (name, phone, arrival, departure, room, country, service, current_price, note, status) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setString(1, guest.name);
            statement.setString(2, guest.phone);
            statement.setString(3, Util.datetimeToString(guest.arrivalDate));
            statement.setString(4, Util.datetimeToString(guest.departureDate));
            statement.setInt(5, guest.roomId);
            statement.setString(6, guest.from);
            statement.setInt(7, guest.serviceId);
            statement.setDouble(8, guest.price);
            statement.setString(9, guest.note);
            statement.setInt(10, guest.statusId);
            statement.execute();
            
            //last_insert_rowid()
            PreparedStatement stmt = connection.getConnection().prepareStatement("select id from Guests where id = last_insert_rowid();");
            ResultSet executeQuery = stmt.executeQuery();
            int lastId = executeQuery.getInt(1);
            busRoom(lastId, guest.roomId);
        }
    }
    
    public void updateGuest(Guest oldValue, Guest newValue) throws SQLException {
        final String query = "update Guests set name = ?, phone = ?, arrival = ?, departure = ?, room = ?, country = ?, service = ?, current_price = ?, note = ?, status = ? where id = ?";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setString(1, newValue.name);
            statement.setString(2, newValue.phone);
            statement.setString(3, Util.datetimeToString(newValue.arrivalDate));
            statement.setString(4, Util.datetimeToString(newValue.departureDate));
            statement.setInt(5, newValue.roomId);
            statement.setString(6, newValue.from);
            statement.setInt(7, newValue.serviceId);
            statement.setDouble(8, newValue.price);
            statement.setString(9, newValue.note);
            statement.setInt(10, newValue.statusId);
            statement.setInt(11, newValue.id);
            statement.execute();
            freeRoom(oldValue.roomId);
            busRoom(oldValue.id, newValue.roomId);
        }
    }

    public void removeGuest(Guest guest) throws SQLException {
        final String query = "update Guests set hidden = 1 where id = ?";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, guest.id);
            statement.execute();
        }
        freeRoom(guest.roomId);
    }
    
    public void setGuestStatus(Guest guest, int status) throws SQLException {
        final String query = "update Guests set status = ? where id = ?";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, status);
            statement.setInt(2, guest.id);
            statement.execute();
        }
        if (status == 2) freeRoom(guest.roomId);
    }
    
    public GuestCount getGuestsCount() throws SQLException {
        int totalCount = connection.getConnection().createStatement().executeQuery("select count(*) from Guests").getInt(1);
        int currentCount = connection.getConnection().createStatement().executeQuery("select count(*) from Guests where status = 1").getInt(1);
        int comingCount = connection.getConnection().createStatement().executeQuery("select count(*) from Guests where status = 0").getInt(1);
        
        return new GuestCount(totalCount, currentCount, comingCount);
    }

    public int getGuestTotalCount() throws SQLException {
        int totalCount = connection.getConnection().createStatement().executeQuery("select count(*) from Guests").getInt(1);
        return totalCount;
    }
    
    public List<Room> getRooms(boolean onlyFreeRooms, int additionRoomId) throws SQLException {
        String query = "select * from Rooms ";
        if (onlyFreeRooms) query += "where room_guest = -1 and is_closed = 0 ";
        if (additionRoomId != -1) query += " or room_id = " + additionRoomId;

        List<Room> rooms = new ArrayList<>();

        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            
            while(result.next()) {
                Room room = new Room();
                room.id = result.getInt(1);
                room.number = result.getInt(2);
                
                int guestId = result.getInt(3);
                int isClosed = result.getInt(4);
                
                room.guestId = guestId;
                room.isClosed = isClosed;
                
                if (guestId == -1) {
                    room.arrivalDate = "-";
                    room.departureDate = "-";
                    room.guest = "-";
                    room.from = "-";
                    room.amount = "-";
                }
                else {
                    Guest guest = getGuestById(guestId);
                    room.arrivalDate = Util.datetimeToString(guest.arrivalDate);
                    room.departureDate = Util.datetimeToString(guest.departureDate);
                    room.guest = guest.name;
                    room.from = guest.from;
                    room.amount = guest.amount;
                }
                
                if (isClosed == 1) {
                    room.state = "Закрыта";
                }
                else if (guestId == -1) {
                    room.state = "Свободна";
                }
                else {
                    room.state = "Занята";
                }
                rooms.add(room);
            }
            result.close();
        }
        return rooms;
    }
    
    public void addRoom(Room room) throws SQLException {
        final String query = "insert into Rooms (room_number, room_guest, is_closed) values(?, ?, ?)";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, room.number);
            statement.setInt(2, room.guestId);
            statement.setInt(3, room.isClosed);
            statement.execute();
        }
    }
    
    public void freeRoom(int roomId) throws SQLException {
        final String query = "update Rooms set room_guest = -1 where room_id = ?;";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, roomId);
            statement.execute();
        }
    }

    public void busRoom(int guestId, int roomId) throws SQLException {
        final String query = "update Rooms set room_guest = ? where room_id = ?;";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, guestId);
            statement.setInt(2, roomId);
            statement.execute();
        }
    }

    public void openCloseRoom(int id, boolean close) throws SQLException {
        final String query = "update Rooms set is_closed = ? where room_id = ?;";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, close ? 1 : 0);
            statement.setInt(2, id);
            statement.execute();
        }
    }

    public List<Service> getServices() throws SQLException {
        final String query = "select * from Services where service_enable = 1";
        services.clear();

        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Service service = new Service();
                service.id = result.getInt(1);
                service.name = result.getString(2);
                services.add(service);
            }
            result.close();
        }
        return services;
    }
    
    public void addService(Service service) throws SQLException {
        final String query = "insert into Services (service_name) values(?)";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setString(1, service.name);
            statement.execute();
        }
    }
    
    public void removeService(Service service) throws SQLException {
        final String query = "update Services set service_enable = 0 where service_id = ?;";
        try (PreparedStatement statement = connection.getConnection().prepareStatement(query)) {
            statement.setInt(1, service.id);
            statement.execute();
        }
    }    
}