package org.darpa.smsreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class SmsReminderActivity extends Activity {
    EditText message_text;
    EditText phone_number_text;
    Button send_button;
    TimePicker time_picker;
    Button view_alarms_button;

    public Button getSend_button() {
        return this.send_button;
    }

    public void setSend_button(Button send_button) {
        this.send_button = send_button;
    }

    public Button getView_alarms_button() {
        return this.view_alarms_button;
    }

    public void setView_alarms_button(Button view_alarms_button) {
        this.view_alarms_button = view_alarms_button;
    }

    public EditText getPhone_number_text() {
        return this.phone_number_text;
    }

    public void setPhone_number_text(EditText phone_number_text) {
        this.phone_number_text = phone_number_text;
    }

    public EditText getMessage_text() {
        return this.message_text;
    }

    public void setMessage_text(EditText message_text) {
        this.message_text = message_text;
    }

    public TimePicker getTime_picker() {
        return this.time_picker;
    }

    public void setTime_picker(TimePicker time_picker) {
        this.time_picker = time_picker;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void launchSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                launchSettingsActivity();
                return true;
            default:
                return false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.send_button = (Button) findViewById(R.id.btnSendSMS);
        this.view_alarms_button = (Button) findViewById(R.id.btnViewAlarms);
        this.phone_number_text = (EditText) findViewById(R.id.txtPhoneNo);
        this.message_text = (EditText) findViewById(R.id.txtMessage);
        this.time_picker = (TimePicker) findViewById(R.id.alarmTimePicker);
        AlarmContent.getInstance().setAlarmContext(this);
        SettingsOptions.getInstance().setContext(this);
        this.send_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int i = 12;
                int i2 = 0;
                String phone_number = SmsReminderActivity.this.phone_number_text.getText().toString();
                String message = SmsReminderActivity.this.message_text.getText().toString();
                if (phone_number.length() == 0 || message.length() == 0) {
                    Toast.makeText(SmsReminderActivity.this.getBaseContext(), "Please Enter a Phone Number and Message.", i2).show();
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int alarm_hour = SmsReminderActivity.this.time_picker.getCurrentHour().intValue();
                int alarm_minute = SmsReminderActivity.this.time_picker.getCurrentMinute().intValue();
                int current_hour = calendar.get(11);
                int current_minute = calendar.get(i);
                int hour_delta = 0;
                if (alarm_hour < current_hour) {
                    hour_delta = (24 - current_hour) + alarm_hour;
                } else if (alarm_hour != current_hour) {
                    hour_delta = alarm_hour - current_hour;
                } else if (alarm_minute < current_minute) {
                    hour_delta = 24;
                } else {
                    hour_delta = 0;
                }
                calendar.roll(10, hour_delta);
                calendar.set(i, alarm_minute);
                calendar.set(13, i2);
                AlarmContent.getInstance().addAlarm(phone_number, calendar, message);
            }
        });
        this.view_alarms_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmsReminderActivity.this.startActivity(new Intent(SmsReminderActivity.this, AlarmViewer.class));
            }
        });
    }
}
