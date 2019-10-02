package accessor.hotel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Paths {
    public static final String LOG_PATH = new File("C:/ProgramData/Accessor/HotelManager").getAbsolutePath() + "/logs/";
    public static final String DATABASE_PATH = new File("C:/ProgramData/Accessor/HotelManager").getAbsolutePath() + "/data.db3";
    
    public static void enterDatabase() {
        try {
            File db = new File(DATABASE_PATH);
            if (db.exists()) return;
            db.getParentFile().mkdirs();
            db.createNewFile();
            
            FileOutputStream fos;
            try (InputStream input = Paths.class.getResourceAsStream("/accessor/hotel/res/data.db3")) {
                byte[] buff = new byte[2048];
                fos = new FileOutputStream(db);
                int len;
                while((len = input.read(buff)) != -1) {
                    fos.write(buff);
                }
            }
            fos.flush();
            fos.close();
        }   catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}