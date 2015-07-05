package com.closetoeuphoria.keepmehonest;

import android.media.RingtoneManager;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class DBHelperTest extends AndroidTestCase {

    DatabaseHelper dbHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private AlarmModel getModel() {
        AlarmModel model = new AlarmModel();
        model.name = "Test";
        model.timeHour = 6;
        model.timeMinute = 30;
        model.repeatWeekly = true;
        model.setRepeatingDay(AlarmModel.SUNDAY, false);
        model.setRepeatingDay(AlarmModel.MONDAY, true);
        model.setRepeatingDay(AlarmModel.WEDNESDAY, true);
        model.setRepeatingDay(AlarmModel.FRIDAY, true);
        model.setRepeatingDay(AlarmModel.SATURDAY, false);
        model.alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        return model;
    }

    public void testCreateAlarm() {
        AlarmModel model = getModel();

        long id = dbHelper.createAlarm(model);

        AlarmModel returnModel = dbHelper.getAlarm(id);

        assertEquals(model.name, returnModel.name);
        assertEquals(model.timeHour, returnModel.timeHour);
        assertEquals(model.timeMinute, returnModel.timeMinute);
        assertEquals(model.repeatWeekly, returnModel.repeatWeekly);
        assertEquals(model.alarmTone, returnModel.alarmTone);
        assertEquals(model.getRepeatingDay(AlarmModel.SUNDAY), returnModel.getRepeatingDay(AlarmModel.SUNDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.MONDAY), returnModel.getRepeatingDay(AlarmModel.MONDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.TUESDAY), returnModel.getRepeatingDay(AlarmModel.TUESDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.WEDNESDAY), returnModel.getRepeatingDay(AlarmModel.WEDNESDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.THURSDAY), returnModel.getRepeatingDay(AlarmModel.THURSDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.FRIDAY), returnModel.getRepeatingDay(AlarmModel.FRIDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.SATURDAY), returnModel.getRepeatingDay(AlarmModel.SATURDAY));
    }

    public void testUpdateAlarm() {
        AlarmModel model = getModel();
        model.name = "Update Test";
        model.timeHour = 22;
        model.timeMinute = 0;
        model.repeatWeekly = false;
        model.setRepeatingDay(AlarmModel.WEDNESDAY, false);

        long id = dbHelper.createAlarm(model);

        AlarmModel returnModel = dbHelper.getAlarm(id);

        assertEquals(model.name, returnModel.name);
        assertEquals(model.timeHour, returnModel.timeHour);
        assertEquals(model.timeMinute, returnModel.timeMinute);
        assertEquals(model.repeatWeekly, returnModel.repeatWeekly);
        assertEquals(model.alarmTone, returnModel.alarmTone);
        assertEquals(model.getRepeatingDay(AlarmModel.SUNDAY), returnModel.getRepeatingDay(AlarmModel.SUNDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.MONDAY), returnModel.getRepeatingDay(AlarmModel.MONDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.TUESDAY), returnModel.getRepeatingDay(AlarmModel.TUESDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.WEDNESDAY), returnModel.getRepeatingDay(AlarmModel.WEDNESDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.THURSDAY), returnModel.getRepeatingDay(AlarmModel.THURSDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.FRIDAY), returnModel.getRepeatingDay(AlarmModel.FRIDAY));
        assertEquals(model.getRepeatingDay(AlarmModel.SATURDAY), returnModel.getRepeatingDay(AlarmModel.SATURDAY));
    }

    public void testDeleteAlarm() {
        AlarmModel model = getModel();

        long id = dbHelper.createAlarm(model);

        int rows = dbHelper.deleteAlarm(id);

        assertFalse(rows == 0);

        AlarmModel returnModel = dbHelper.getAlarm(id);

        assertNull(returnModel);
    }
}