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
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import com.f2prateek.ln.Ln;
import com.f2prateek.shush.wifi.base.BaseBroadcastReceiver;
import javax.inject.Inject;

import static android.net.wifi.WifiManager.EXTRA_WIFI_STATE;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;

/**
 * Shows a dialog if the wifi is turned off, else turns it off.
 */
public class OnWifiStateChanged extends BaseBroadcastReceiver {
  @Inject NotificationManager notificationManager;
  @Inject AlarmManager alarmManager;

  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    int newWifiState = intent.getIntExtra(EXTRA_WIFI_STATE, -1);

    switch (newWifiState) {
      case WIFI_STATE_DISABLED:
      case WIFI_STATE_DISABLING:
        // Wifi has been disabled, show the user the dialog
        context.startActivity(WifiSchedulerDialog.getIntent(context));
        break;
      case WIFI_STATE_ENABLED:
      case WIFI_STATE_ENABLING:
        // Wifi has been enabled, dismiss any existing dialogs and scheduled tasks
        WifiTurnedOffNotification.dismiss(notificationManager);
        TurnWifiOn.cancelScheduled(alarmManager, context);
        break;
      default:
        Ln.d("Ignoring unknown wifi state change.");
    }
  }
}
