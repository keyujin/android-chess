package com.example.group55.androidchess55.models;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SavedGameInfo {
    private String title;
    private Calendar dt;
    private String file_name;

    public SavedGameInfo(String file_name) {
        this.file_name = file_name;
        String[] data = file_name.split("~");
        title = data[0];
        dt = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss", Locale.US);
        try {
            dt.setTime(sdf.parse(data[1].substring(0, data[1].indexOf(".ser")-1)));
        } catch (ParseException e) {}
    }

    public String toString() {
        return title + "\n" + new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(dt.getTime());
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return file_name;
    }

    public Calendar getDT() {
        return dt;
    }
}
