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

package com.f2prateek.shush.wifi.prefs;

import android.content.SharedPreferences;
import android.content.res.Resources;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(complete = false, library = true)
public final class PreferencesModule {
  public static final int DEFAULT_MINUTES = 120; // Two Hours

  @Provides @Singleton @Minutes IntPreference provideMinutesPreference(
      final SharedPreferences sharedPreferences) {
    return new IntPreference(sharedPreferences, "minutes", DEFAULT_MINUTES);
  }

  @Provides @Singleton @Color IntPreference provideColorPreference(
      final SharedPreferences sharedPreferences, final Resources resources) {
    return new IntPreference(sharedPreferences, "color",
        resources.getColor(android.R.color.holo_blue_bright));
  }

  @Provides @Singleton @NotificationsEnabled BooleanPreference provideNotificationsPreference(
      final SharedPreferences sharedPreferences) {
    return new BooleanPreference(sharedPreferences, "notifications_enabled", true);
  }
}
