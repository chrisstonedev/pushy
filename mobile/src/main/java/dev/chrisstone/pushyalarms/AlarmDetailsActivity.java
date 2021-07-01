package dev.chrisstone.pushyalarms;

import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmDetailsActivity extends AppCompatActivity {

    private final DatabaseHelper dbHelper = new DatabaseHelper(this);

    private AlarmModel alarmDetails;

    private TimePicker timePicker;
    private EditText edtName;
    private CustomSwitch chkWeekly;
    private CustomSwitch chkSunday;
    private CustomSwitch chkMonday;
    private CustomSwitch chkTuesday;
    private CustomSwitch chkWednesday;
    private CustomSwitch chkThursday;
    private CustomSwitch chkFriday;
    private CustomSwitch chkSaturday;
    private TextView txtToneSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        if (getActionBar() != null) {
            getActionBar().setTitle("Create New Alarm");
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        timePicker = findViewById(R.id.alarm_details_time_picker);
        edtName = findViewById(R.id.alarm_details_name);
        chkWeekly = findViewById(R.id.alarm_details_repeat_weekly);
        chkSunday = findViewById(R.id.alarm_details_repeat_sunday);
        chkMonday = findViewById(R.id.alarm_details_repeat_monday);
        chkTuesday = findViewById(R.id.alarm_details_repeat_tuesday);
        chkWednesday = findViewById(R.id.alarm_details_repeat_wednesday);
        chkThursday = findViewById(R.id.alarm_details_repeat_thursday);
        chkFriday = findViewById(R.id.alarm_details_repeat_friday);
        chkSaturday = findViewById(R.id.alarm_details_repeat_saturday);
        txtToneSelection = findViewById(R.id.alarm_label_tone_selection);

        long id = getIntent().getExtras().getLong("id");

        if (id == -1) {
            alarmDetails = new AlarmModel();
        } else {
            alarmDetails = dbHelper.getAlarm(id);

            timePicker.setCurrentMinute(alarmDetails.timeMinute);
            timePicker.setCurrentHour(alarmDetails.timeHour);

            edtName.setText(alarmDetails.name);

            chkWeekly.setChecked(alarmDetails.repeatWeekly);
            chkSunday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
            chkMonday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
            chkTuesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
            chkWednesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
            chkThursday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
            chkFriday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRIDAY));
            chkSaturday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));

            txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
        }

        final LinearLayout ringToneContainer = findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_save_alarm_details) {
            updateModelFromLayout();

            AlarmManagerHelper.cancelAlarms(this);

            if (alarmDetails.id < 0) {
                dbHelper.createAlarm(alarmDetails);
            } else {
                dbHelper.updateAlarm(alarmDetails);
            }

            AlarmManagerHelper.setAlarms(this);

            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateModelFromLayout() {
        alarmDetails.timeMinute = timePicker.getCurrentMinute();
        alarmDetails.timeHour = timePicker.getCurrentHour();
        alarmDetails.name = edtName.getText().toString();
        alarmDetails.repeatWeekly = chkWeekly.isChecked();
        alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, chkSunday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.MONDAY, chkMonday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, chkTuesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, chkWednesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, chkThursday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.FRIDAY, chkFriday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, chkSaturday.isChecked());
        alarmDetails.isEnabled = true;
    }

}