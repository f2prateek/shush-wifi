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

import android.content.Context;
import com.github.johnpersano.supertoasts.SuperToast;

public class Toaster {
  private Toaster() {
    // no instances
  }

  public static void show(Context context, String message) {
    SuperToast superToast = new SuperToast(context.getApplicationContext());
    superToast.setDuration(SuperToast.Duration.LONG);
    superToast.setAnimations(SuperToast.Animations.SCALE);
    superToast.setText(message);
    superToast.setBackground(SuperToast.Background.BLACK);
    superToast.show();
  }
}
