package accessor.hotel.model;

import accessor.hotel.Util;
import java.io.File;
import java.time.LocalDateTime;

public class Journal {
    
    private File journalFile;
    private String journalText;

    public Journal(File journalFile, String journalText) {
        this.journalFile = journalFile;
        this.journalText = journalText;
    }

    public String getJournalText() {
        return journalText;
    }

    @Override
    public String toString() {
        return journalFile.getName();
    }
}
