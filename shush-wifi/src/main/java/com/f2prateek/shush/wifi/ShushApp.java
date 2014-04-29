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

import android.app.Application;
import android.content.Context;
import com.f2prateek.ln.DebugLn;
import com.f2prateek.ln.Ln;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;

public class ShushApp extends Application {
  private ObjectGraph applicationGraph;

  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Ln.set(DebugLn.from(this));
    }

    buildObjectGraphAndInject();
  }

  @DebugLog
  public void buildObjectGraphAndInject() {
    applicationGraph = ObjectGraph.create(Modules.list(this));
    inject(this);
  }

  public void inject(Object o) {
    applicationGraph.inject(o);
  }

  public static ShushApp get(Context context) {
    return (ShushApp) context.getApplicationContext();
  }
}
