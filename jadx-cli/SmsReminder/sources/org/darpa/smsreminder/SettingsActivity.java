package org.darpa.smsreminder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends Activity {
    private CheckBox repeat_check_box;
    private EditText repeat_delay_text;
    private Button save_button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.repeat_check_box = (CheckBox) findViewById(R.id.repeat_check_box);
        this.repeat_delay_text = (EditText) findViewById(R.id.repeat_delay_text);
        this.save_button = (Button) findViewById(R.id.save_button);
        boolean repeat_on = SettingsOptions.getInstance().repeatOn();
        int repeat_delay = SettingsOptions.getInstance().repeatDelay();
        this.repeat_check_box.setChecked(repeat_on);
        this.repeat_delay_text.setText(Integer.toString(repeat_delay));
        this.save_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsOptions so = SettingsOptions.getInstance();
                so.setRepeatDelay(Integer.decode(SettingsActivity.this.repeat_delay_text.getText().toString()).intValue());
                so.setRepeatOn(SettingsActivity.this.repeat_check_box.isChecked());
                SettingsActivity.this.finish();
            }
        });
    }

    public CheckBox getRepeat_check_box() {
        return this.repeat_check_box;
    }

    public void setRepeat_check_box(CheckBox repeat_check_box) {
        this.repeat_check_box = repeat_check_box;
    }

    public EditText getRepeat_delay_text() {
        return this.repeat_delay_text;
    }

    public void setRepeat_delay_text(EditText repeat_delay_text) {
        this.repeat_delay_text = repeat_delay_text;
    }

    public Button getSave_button() {
        return this.save_button;
    }

    public void setSave_button(Button save_button) {
        this.save_button = save_button;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }
}
