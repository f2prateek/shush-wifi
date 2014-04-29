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

package com.f2prateek.shush.wifi.base;

import android.app.Activity;
import android.os.Bundle;
import com.f2prateek.shush.wifi.ShushApp;

/**
 * Base Activity for injecting into the application graph.
 * Sub-classes are responsible for injecting views.
 */
public class BaseActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ShushApp.get(this).inject(this);
  }
}
