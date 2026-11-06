package com.dexterous.flutterlocalnotifications;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import com.dexterous.flutterlocalnotifications.models.NotificationDetails;
import com.dexterous.flutterlocalnotifications.models.ScheduleMode;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class NotificationRecoveryTest {
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        context.getSharedPreferences("notification_plugin_cache", 0).edit().clear().commit();
    }

    @Test
    public void missingIconStateBeforeDartStartupHasValidFallback() {
        assertEquals(android.R.drawable.ic_popup_reminder,
            FlutterLocalNotificationsPlugin.resolveSmallIcon(context, new NotificationDetails()));
    }

    @Test
    public void legacyIconSurvivesMissingDefault() {
        NotificationDetails details = new NotificationDetails();
        details.iconResourceId = android.R.drawable.ic_dialog_info;
        assertEquals(details.iconResourceId.intValue(), FlutterLocalNotificationsPlugin.resolveSmallIcon(context, details));
    }

    @Test
    public void invalidRestoredIconsFallBackSafely() {
        NotificationDetails details = new NotificationDetails();
        details.icon = "removed_icon";
        details.iconResourceId = 123;
        context.getSharedPreferences("notification_plugin_cache", 0).edit().putString("defaultIcon", "removed_default").commit();
        assertEquals(android.R.drawable.ic_popup_reminder, FlutterLocalNotificationsPlugin.resolveSmallIcon(context, details));
    }

    @Test
    public void packagedMetadataWorksWithoutPluginPreferences() throws Exception {
        Bundle metadata = new Bundle();
        metadata.putInt("com.dexterous.flutterlocalnotifications.default_notification_icon", android.R.drawable.ic_dialog_info);
        android.content.pm.PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 128);
        info.applicationInfo.metaData = metadata;
        org.robolectric.Shadows.shadowOf(context.getPackageManager()).installPackage(info);
        assertEquals(android.R.drawable.ic_dialog_info,
            FlutterLocalNotificationsPlugin.resolveSmallIcon(context, new NotificationDetails()));
    }

    @Test
    public void rejectedAlarmDoesNotAbortRemainingRestoreOrDeleteCachedIntent() {
        Context wrapped = spy(context);
        AlarmManager alarms = mock(AlarmManager.class);
        doReturn(alarms).when(wrapped).getSystemService(Context.ALARM_SERVICE);
        doThrow(new IllegalStateException("alarm limit")).doNothing().when(alarms)
            .setExactAndAllowWhileIdle(anyInt(), anyLong(), any(PendingIntent.class));
        NotificationDetails first = new NotificationDetails();
        first.id = 1;
        first.millisecondsSinceEpoch = System.currentTimeMillis() + 60000;
        first.scheduleMode = ScheduleMode.exactAllowWhileIdle;
        NotificationDetails second = new NotificationDetails();
        second.id = 2;
        second.millisecondsSinceEpoch = first.millisecondsSinceEpoch + 60000;
        second.scheduleMode = ScheduleMode.exactAllowWhileIdle;
        String cached = FlutterLocalNotificationsPlugin.buildGson().toJson(Arrays.asList(first, second));
        context.getSharedPreferences("scheduled_notifications", 0).edit().putString("scheduled_notifications", cached).commit();
        FlutterLocalNotificationsPlugin.rescheduleNotifications(wrapped);
        verify(alarms, times(2)).setExactAndAllowWhileIdle(anyInt(), anyLong(), any(PendingIntent.class));
        assertEquals(cached, context.getSharedPreferences("scheduled_notifications", 0).getString("scheduled_notifications", null));
    }
}
