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
import android.app.Application;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import com.f2prateek.shush.wifi.base.BaseBroadcastReceiver;
import com.f2prateek.shush.wifi.prefs.PreferencesModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.WIFI_SERVICE;

@Module(
    includes = PreferencesModule.class,
    injects = {
        ShushApp.class, BaseBroadcastReceiver.class, OnWifiStateChanged.class, TurnWifiOn.class,
        WifiSchedulerDialog.class, WelcomeScreen.class
    })
public final class ShushModule {
  private final ShushApp app;

  public ShushModule(ShushApp app) {
    this.app = app;
  }

  @Provides @Singleton Application provideApp() {
    return app;
  }

  @Provides @Singleton Resources provideResources() {
    return app.getResources();
  }

  @Provides @Singleton WifiManager provideWifiManager(final Application app) {
    return getSystemService(app, WIFI_SERVICE);
  }

  @Provides @Singleton AlarmManager provideAlarmManager(final Application app) {
    return getSystemService(app, ALARM_SERVICE);
  }

  @Provides @Singleton NotificationManager provideNotificationManager(final Application app) {
    return getSystemService(app, NOTIFICATION_SERVICE);
  }

  @Provides @Singleton KeyguardManager provideKeyguardManager(final Application app) {
    return getSystemService(app, KEYGUARD_SERVICE);
  }

  @Provides @Singleton SharedPreferences provideSharedPreferences(final Application app) {
    return app.getSharedPreferences("shush.wifi", MODE_PRIVATE);
  }

  @SuppressWarnings("unchecked")
  public <T> T getSystemService(Context context, String serviceConstant) {
    return (T) context.getSystemService(serviceConstant);
  }
}