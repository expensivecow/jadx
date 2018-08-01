package org.darpa.smsreminder;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class SettingsOptions {
    private static SettingsOptions instance = null;
    private Context context;
    private String file_name = "alarm_settings.dat";
    private View myview;
    private int repeat_delay;
    private boolean repeat_on;

    public static SettingsOptions getInstance() {
        if (instance == null) {
            instance = new SettingsOptions();
        }
        return instance;
    }

    private SettingsOptions() {
        loadSettings();
    }

    public void setContext(Context new_context) {
        this.context = new_context;
    }

    private void loadSettings() {
        try {
            Scanner reader = new Scanner(new InputStreamReader(this.context.openFileInput(this.file_name)));
            this.repeat_delay = reader.nextInt();
            this.repeat_on = reader.nextBoolean();
            reader.close();
        } catch (Exception e) {
            this.repeat_on = false;
            this.repeat_delay = 5;
        }
    }

    private void writeSettings() {
        int i = 0;
        try {
            PrintWriter writer = new PrintWriter(this.context.openFileOutput(this.file_name, 0));
            StringBuffer sb = new StringBuffer();
            sb.append(this.repeat_delay);
            sb.append(" ");
            sb.append(this.repeat_on);
            writer.println(sb.toString());
        } catch (Exception e) {
            Toast.makeText(this.context, "Error: Failed to write to file", i).show();
        }
    }

    public boolean repeatOn() {
        return this.repeat_on;
    }

    public void setRepeatOn(boolean on) {
        this.repeat_on = on;
        writeSettings();
    }

    public int repeatDelay() {
        return this.repeat_delay;
    }

    public void setRepeatDelay(int minutes) {
        this.repeat_delay = minutes;
        writeSettings();
    }
}
