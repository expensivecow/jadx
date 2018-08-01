package org.darpa.smsreminder;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;
import org.darpa.smsreminder.AlarmContent.AlarmItem;

public class SmsAlarmReceiver extends Service {
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Binding!", 0).show();
        return null;
    }

    public void onStart(Intent intent, int startId) {
        Bundle bundle = intent.getExtras();
        String phone_number = (String) bundle.getCharSequence("phone_number");
        String message = (String) bundle.getCharSequence("message");
        int alarm_id = bundle.getInt("alarm_id");
        if (SettingsOptions.getInstance().repeatOn()) {
            System.out.println("Repeat is on");
            AlarmItem alarm_item = AlarmContent.getInstance().getAlarmById(alarm_id);
            System.out.println("Got old alarm, id = " + Integer.toString(alarm_id));
            String new_phone_number = alarm_item.phone_number;
            System.out.println("Phone number: " + new_phone_number);
            int delay = SettingsOptions.getInstance().repeatDelay();
            System.out.println("Delay = " + Integer.toString(delay));
            long new_time = alarm_item.time_millis() + ((long) ((delay * 60) * 1000));
            System.out.println("New time = " + Long.toString(new_time));
            int new_alarm_number = alarm_item.alarm_number;
            System.out.println("Alarm number = " + Integer.toString(new_alarm_number));
            AlarmContent instance = AlarmContent.getInstance();
            instance.getClass();
            AlarmItem new_alarm_item = new AlarmItem(new_phone_number, new_time, "Reminder! Text me back to stop receiving reminders!", new_alarm_number);
            new_alarm_item.setReminder(true);
            System.out.println("Created new alarm item");
            AlarmContent.getInstance().addAlarm(new_alarm_item);
            System.out.println("Added alarm.");
        }
        send_sms(phone_number, message);
        System.out.println("Deleting alarm id=" + Integer.toString(alarm_id));
        AlarmContent.getInstance().removeAlarmById(alarm_id);
    }

    private void send_sms(String phone_number, String message) {
        int i = 0;
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, i, new Intent(SENT), i);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, i, new Intent(DELIVERED), i);
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int i = 0;
                switch (getResultCode()) {
                    case 1:
                        Toast.makeText(SmsAlarmReceiver.this.getBaseContext(), "Generic failure", i);
                        return;
                    case 2:
                        Toast.makeText(SmsAlarmReceiver.this.getBaseContext(), "Radio off", i).show();
                        return;
                    case 3:
                        Toast.makeText(SmsAlarmReceiver.this.getBaseContext(), "Null PDU", i).show();
                        return;
                    case 4:
                        Toast.makeText(SmsAlarmReceiver.this.getBaseContext(), "No service", i).show();
                        return;
                    default:
                        return;
                }
            }
        }, new IntentFilter(SENT));
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int i = 0;
                switch (getResultCode()) {
                    case -1:
                        Toast.makeText(SmsAlarmReceiver.this.getBaseContext(), "SMS delivered", i).show();
                        return;
                    case 0:
                        Toast.makeText(SmsAlarmReceiver.this.getBaseContext(), "SMS not delivered", i).show();
                        return;
                    default:
                        return;
                }
            }
        }, new IntentFilter(DELIVERED));
        SmsManager.getDefault().sendTextMessage(phone_number, null, message, sentPI, deliveredPI);
    }
}
