package accessor.hotel;

import accessor.hotel.data.DatabaseHelper;
import accessor.hotel.model.Guest;
import accessor.hotel.model.Room;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TestApp {
    
    public static void main(String[] args) throws SQLException, IOException {
        Paths.enterDatabase();
        addGuests();
    }
    
    public static void addGuests() throws IOException, SQLException {
        String[] names = readList("testnames");
        String[] phones = readList("testphone");
        
        for(int i = 2; i < 40; i++) {
            Guest guest = new Guest();
            guest.arrivalDate = LocalDateTime.now();
            guest.departureDate = LocalDateTime.now();
            guest.name = names[i];
            guest.phone = phones[i];
            guest.price = 45000;
            guest.roomId = i + 1;
            guest.statusId = 0;
            guest.serviceId = 0;
            guest.note = "";
            guest.from = "Русь";
            DatabaseHelper.instance.addGuest(guest);
        }
    }
    
    private static String[] readList(String resname) throws IOException {
        byte[] bs;
        try (InputStream res = TestApp.class.getResourceAsStream("/accessor/hotel/res/" + resname)) {
            bs = new byte[res.available()];
            res.read(bs);
        }
        return new String(bs).split("\n");
    }
    
    public static void addRooms() throws SQLException {
        for(int i = 2; i < 40; i++) {
            Room room = new Room();
            room.number = i;
            room.guestId = -1;
            room.isClosed = 0;
            DatabaseHelper.instance.addRoom(room);
            System.out.println("Room added: " + i);
        }
    }
}
