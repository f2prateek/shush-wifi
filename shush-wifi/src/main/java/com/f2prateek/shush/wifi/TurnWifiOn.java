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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import com.f2prateek.shush.wifi.base.BaseBroadcastReceiver;
import javax.inject.Inject;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

/**
 * Turns the wifi on when received.
 */
public final class TurnWifiOn extends BaseBroadcastReceiver {
  public static PendingIntent createPendingIntent(Context context) {
    Intent intent = new Intent(context, TurnWifiOn.class);
    return PendingIntent.getBroadcast(context, 0, intent, FLAG_CANCEL_CURRENT);
  }

  @Inject WifiManager wifiManager;

  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    if (wifiManager.isWifiEnabled()) {
      return;
    } else {
      wifiManager.setWifiEnabled(true);
    }
  }

  public static void schedule(AlarmManager alarmManager, PendingIntent ringerOnIntent,
      long onTime) {
    alarmManager.set(AlarmManager.RTC_WAKEUP, onTime, ringerOnIntent);
  }

  public static void cancelScheduled(AlarmManager alarmManager, Context context) {
    alarmManager.cancel(createPendingIntent(context));
  }
}
