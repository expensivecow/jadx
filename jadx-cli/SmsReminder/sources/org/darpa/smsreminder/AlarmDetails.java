package org.darpa.smsreminder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import org.darpa.smsreminder.AlarmContent.AlarmItem;

public class AlarmDetails extends Activity {
    private int position;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);
        this.position = getIntent().getExtras().getInt(getString(R.string.position));
        TextView phone_number_view = (TextView) findViewById(R.id.phone_number_view);
        TextView date_time_view = (TextView) findViewById(R.id.date_time_view);
        TextView message_view = (TextView) findViewById(R.id.message_view);
        AlarmItem alarm = AlarmContent.getInstance().getAlarmFromPosition(this.position);
        phone_number_view.setText(alarm.phone_number);
        date_time_view.setText(alarm.time_string());
        message_view.setText(alarm.message);
        ((Button) findViewById(R.id.cancel_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AlarmContent.getInstance().removeAlarmByPosition(AlarmDetails.this.position);
                AlarmDetails.this.finish();
            }
        });
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_alarm_details, menu);
        return true;
    }
}
