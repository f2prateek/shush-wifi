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

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.CompoundButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.f2prateek.shush.wifi.base.BaseActivity;
import com.f2prateek.shush.wifi.prefs.BooleanPreference;
import com.f2prateek.shush.wifi.prefs.Color;
import com.f2prateek.shush.wifi.prefs.IntPreference;
import com.f2prateek.shush.wifi.prefs.NotificationsEnabled;
import com.larswerkman.holocolorpicker.ColorPicker;
import javax.inject.Inject;

/**
 * A dialog that explains how Shush works and lets users pick limited options.
 */
public final class WelcomeScreen extends BaseActivity
    implements ColorPicker.OnColorSelectedListener {
  @Inject @Color IntPreference colorPreference;
  @Inject @NotificationsEnabled BooleanPreference notificationsPreference;
  @Inject Resources resources;

  @InjectView(R.id.notification_toggle) CompoundButton notificationToggle;
  @InjectView(R.id.color_picker) ColorPicker colorPicker;

  @Override protected void onResume() {
    super.onResume();

    setContentView(R.layout.welcome_screen);
    ButterKnife.inject(this);

    notificationToggle.setChecked(notificationsPreference.get());

    colorPicker.setColor(colorPreference.get());
    colorPicker.setOnColorSelectedListener(this);

    // We don't get notified when the user selects the old center, so we simply disable this
    // colorPicker.setOldCenterColor(colorPreference.get());
    colorPicker.setShowOldCenterColor(false);
  }

  @OnCheckedChanged(R.id.notification_toggle) public void notificationsToggled(boolean checked) {
    notificationsPreference.set(checked);
  }

  @OnClick(R.id.share) public void share() {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_subject));
    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_message));
    startActivity(Intent.createChooser(intent, resources.getString(R.string.share_chooser_title)));
  }

  @OnClick(R.id.rate) public void rate() {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.f2prateek.shush"));
    startActivity(i);
  }

  @Override public void onColorSelected(int color) {
    colorPreference.set(color);
  }
}
