package accessor.hotel;

import accessor.hotel.data.DatabaseHelper;
import accessor.hotel.model.Room;
import java.sql.SQLException;

public class TestApp {
    
    public static void main(String[] args) throws SQLException {
//        addRooms();
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
