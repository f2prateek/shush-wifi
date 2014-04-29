/*
 * Copyright (C) 2010 Jesse Wilson
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.f2prateek.shush.wifi;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.f2prateek.shush.wifi.base.BaseActivity;
import com.f2prateek.shush.wifi.prefs.BooleanPreference;
import com.f2prateek.shush.wifi.prefs.IntPreference;
import com.f2prateek.shush.wifi.prefs.Minutes;
import com.f2prateek.shush.wifi.prefs.NotificationsEnabled;
import java.util.Date;
import javax.inject.Inject;

import static android.net.wifi.WifiManager.EXTRA_WIFI_STATE;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;
import static android.view.Gravity.BOTTOM;

/**
 * A dialog to schedule the ringer back on after a specified duration.
 */
public final class WifiSchedulerDialog extends BaseActivity {
  /** For restoring state. */
  private static final String KEY_START = "start";
  private static final String KEY_MINUTES = "minutes";
  /** show full-screen toast messages for two seconds */
  private static final long TOAST_LENGTH_MILLIS = 2 * 1000;
  /** cancel shush after 60 seconds of inactivity */
  private static final long TIMEOUT_MILLIS = 60 * 1000;
  /** observe broadcast wifi state changes */
  private static final IntentFilter WIFI_STATE_CHANGED =
      new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");

  /** the shush alert dialog. */
  private Dialog dialog;
  /** either the regular dialog or the dialog in a full screen window for the lock screen */
  private ShushWindow shushWindow;

  /** Read/write access to this activity's event queue */
  private final Handler handler = new Handler();

  /** If the user turns the ringer back on, dismiss the dialog and exit. */
  private final BroadcastReceiver dismissFromWifiStateChange = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      if (clockSlider == null) {
        return; // race between volume up and onStop
      }

      int newWifiState = intent.getIntExtra(EXTRA_WIFI_STATE, -1);
      switch (newWifiState) {
        case WIFI_STATE_ENABLED:
        case WIFI_STATE_ENABLING:
          cancel(false);
          break;
        case WIFI_STATE_DISABLED:
        case WIFI_STATE_DISABLING:
        default:
          // ignore
          break;
      }
    }
  };

  /** If the user doesn't take action, quietly dismiss Shush. */
  private final Runnable dismissFromTimeout = new Runnable() {
    public void run() {
      if (shushWindow != null) {
        cancel(false);
      }
    }
  };

  /**
   * Returns an intent that triggers this dialog as an activity.
   */
  public static Intent getIntent(Context context) {
    Intent intent = new Intent(context, WifiSchedulerDialog.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return intent;
  }

  @Inject AlarmManager alarmManager;
  @Inject NotificationManager notificationManager;
  @Inject KeyguardManager keyguardManager;
  /** True for notifications; false for toasts. */
  @Inject @NotificationsEnabled BooleanPreference notificationsPreference;
  @Inject @com.f2prateek.shush.wifi.prefs.Color IntPreference colorPreference;
  @Inject @Minutes IntPreference minutesPreference;

  @InjectView(R.id.clock_slider) ClockSlider clockSlider;

  @Override protected void onStart() {
    super.onStart();

    TurnWifiOn.cancelScheduled(alarmManager, this);
    WifiTurnedOffNotification.dismiss(notificationManager);

    shushWindow = keyguardManager.inKeyguardRestrictedInputMode() ? new ShushFullscreen()
        : new ShushDialogOnly();
    createShushDialog();
    clockSlider.setStart(new Date());

    clockSlider.setMinutes(minutesPreference.get());
    clockSlider.setColor(colorPreference.get());

    registerReceiver(dismissFromWifiStateChange, WIFI_STATE_CHANGED);
    registerTimeoutCallback();
  }

  @Override protected void onStop() {
    unregisterReceiver(dismissFromWifiStateChange);
    unregisterTimeoutCallback();
    dialog.dismiss();
    shushWindow = null;
    clockSlider = null;
    super.onStop();
  }

  private void registerTimeoutCallback() {
    handler.postDelayed(dismissFromTimeout, TIMEOUT_MILLIS);
  }

  private void unregisterTimeoutCallback() {
    handler.removeCallbacks(dismissFromTimeout);
  }

  private void commit() {
    if (clockSlider == null) {
      return; // race between volume up and shush button
    }

    unregisterTimeoutCallback();
    PendingIntent ringerOn = TurnWifiOn.createPendingIntent(this);

    long onTime = clockSlider.getEnd().getTime();
    TurnWifiOn.schedule(alarmManager, ringerOn, onTime);

    minutesPreference.set(clockSlider.getMinutes());

    String message = WifiTurnedOffNotification.getMessage(this, onTime);
    if (notificationsPreference.get()) {
      WifiTurnedOffNotification.show(this, notificationManager, message, ringerOn);
      shushWindow.finish(null);
    } else {
      shushWindow.finish(message);
    }
  }

  private void cancel(boolean showMessage) {
    if (clockSlider == null) {
      return; // race between volume up and cancel button
    }

    unregisterTimeoutCallback();
    String message = showMessage ? getString(R.string.wifi_disabled_indefinitely) : null;
    shushWindow.finish(message);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    long start = clockSlider.getStart().getTime();
    int minutes = clockSlider.getMinutes();
    outState.putLong(KEY_START, start);
    outState.putInt(KEY_MINUTES, minutes);
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    int minutes = savedInstanceState.getInt(KEY_MINUTES, minutesPreference.get());
    long start = savedInstanceState.getLong(KEY_START, System.currentTimeMillis());
    clockSlider.setStart(new Date(start));
    clockSlider.setMinutes(minutes);
  }

  private void createShushDialog() {
    View view = getLayoutInflater().inflate(R.layout.scheduler_dialog, null);
    ButterKnife.inject(this, view);

    dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ShushTheme)) //
        .setPositiveButton(R.string.disable, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogInterface, int i) {
                commit();
              }
            }
        ).setNegativeButton(R.string.keep_disabled, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            cancel(true);
          }
        }).setIcon(null).setView(view).setTitle(R.string.turn_wifi_on).setCancelable(true).create();
    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      public void onCancel(DialogInterface dialogInterface) {
        cancel(true);
      }
    });
    dialog.setCanceledOnTouchOutside(true);
    dialog.getWindow().setGravity(BOTTOM);
    dialog.show();
  }

  interface ShushWindow {
    void finish(String message);
  }

  /**
   * Show Shush! in a dialog by default.
   */
  class ShushDialogOnly implements ShushWindow {
    public void finish(String message) {
      if (message != null) {
        Toaster.show(getApplicationContext(), message);
      }
      WifiSchedulerDialog.this.finish();
    }
  }

  /**
   * Show Shush! as a full-screen app when triggered by the lock screen.
   */
  class ShushFullscreen implements ShushWindow {
    private final Window fullScreenWindow;

    ShushFullscreen() {
      fullScreenWindow = getWindow();
      fullScreenWindow.setContentView(R.layout.toast);
      fullScreenWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
      fullScreenWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
          | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void finish(String message) {
      if (message == null) {
        WifiSchedulerDialog.this.finish();
        return;
      }

      dialog.dismiss();
      TextView toast = (TextView) fullScreenWindow.findViewById(R.id.toast);
      toast.setText(message);

      handler.postDelayed(new Runnable() {
        public void run() {
          WifiSchedulerDialog.this.finish();
        }
      }, TOAST_LENGTH_MILLIS);
    }
  }
}
