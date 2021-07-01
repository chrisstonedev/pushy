package dev.chrisstone.pushyalarms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmListActivity extends AppCompatActivity
        implements AlarmListItemClickListener.OnItemClickListener {
    private final static String TAG = "AlarmListActivity";
    private final DatabaseHelper dbHelper = new DatabaseHelper(this);
    private Context mContext;

    private AlarmListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_alarm_list);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);

        mRecyclerView.addOnItemTouchListener(new AlarmListItemClickListener(this, this));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        mAdapter = new AlarmListAdapter(this, dbHelper.getAlarms());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add_new_alarm) {
            startAlarmDetailsActivity(-1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mAdapter.setAlarms(dbHelper.getAlarms());
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setAlarmEnabled(long id, boolean isEnabled) {
        AlarmManagerHelper.cancelAlarms(this);

        AlarmModel model = dbHelper.getAlarm(id);
        model.isEnabled = isEnabled;
        dbHelper.updateAlarm(model);

        AlarmManagerHelper.setAlarms(this);
    }

    public void startAlarmDetailsActivity(long id) {
        Intent intent = new Intent(this, AlarmDetailsActivity.class);
        intent.putExtra("id", id);

        startActivity(intent);
    }

    public void deleteAlarm(long id) {
        final long alarmId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please confirm")
                .setTitle("Delete set?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    //Cancel Alarms
                    AlarmManagerHelper.cancelAlarms(mContext);
                    //Delete alarm from DB by id
                    dbHelper.deleteAlarm(alarmId);
                    //Refresh the list of the alarms in the adaptor
                    mAdapter.setAlarms(dbHelper.getAlarms());
                    //Notify the adapter the data has changed
                    mAdapter.notifyDataSetChanged();
                    //Set the alarms
                    AlarmManagerHelper.setAlarms(mContext);
                }).show();
    }


    @Override
    public void onItemClick(View childView, int position) {
        Log.d(TAG, childView.toString() + " , " + position);
        startAlarmDetailsActivity(mAdapter.getItemId(position));
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        Log.d(TAG, childView.toString() + " , " + position);
        deleteAlarm(mAdapter.getItemId(position));
    }
}