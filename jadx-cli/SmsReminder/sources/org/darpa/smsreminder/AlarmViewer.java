package org.darpa.smsreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.darpa.smsreminder.AlarmContent.AlarmItem;

public class AlarmViewer extends Activity {
    private ListView alarms_list;
    private ArrayAdapter<AlarmItem> listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_viewer);
        this.alarms_list = (ListView) findViewById(R.id.alarms_list_view);
        this.listAdapter = new ArrayAdapter(this, 17367043, AlarmContent.getInstance().getAlarms());
        this.alarms_list.setAdapter(this.listAdapter);
        this.alarms_list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
                Intent intent = new Intent(AlarmViewer.this, AlarmDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt(AlarmViewer.this.getString(R.string.position), position);
                intent.putExtras(bundle);
                AlarmViewer.this.startActivity(intent);
                AlarmViewer.this.finish();
            }
        });
        this.alarms_list.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long id) {
                System.out.println("Item selected!");
                Toast.makeText(AlarmViewer.this, "Position: " + Integer.toString(position), 0).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public ListView getAlarms_list() {
        return this.alarms_list;
    }

    public void setAlarms_list(ListView alarms_list) {
        this.alarms_list = alarms_list;
    }

    public ArrayAdapter<AlarmItem> getListAdapter() {
        return this.listAdapter;
    }

    public void setListAdapter(ArrayAdapter<AlarmItem> listAdapter) {
        this.listAdapter = listAdapter;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_alarm_viewer, menu);
        return true;
    }
}
