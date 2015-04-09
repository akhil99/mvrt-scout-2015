 /* Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.integration.android;

 import android.app.Activity;
 import android.app.AlertDialog;
 import android.app.Fragment;
 import android.content.ActivityNotFoundException;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.pm.PackageManager;
 import android.content.pm.ResolveInfo;
 import android.net.Uri;
 import android.os.Bundle;
 import android.util.Log;

 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

/**
 * <p>A utility class which helps ease integration with Barcode Scanner via {@link Intent}s. This is a simple
 * way to invoke barcode scanning and receive the result, without any need to integrate, modify, or learn the
 * project's source code.</p>
 *
 * <h2>Sharing text via barcode</h2>
 *
 * <p>To share text, encoded as a QR Code on-screen, similarly, see {@link #shareText(CharSequence)}.</p>
 *
 * <p>Some code, particularly download integration, was contributed from the Anobiit application.</p>
 *
 * @author Sean Owen
 * @author Fred Lin
 * @author Isaac Potoczny-Jones
 * @author Brad Drehmer
 * @author gcstang
 */
public class IntentIntegrator {

  private static final String TAG = IntentIntegrator.class.getSimpleName();

  public static final String DEFAULT_TITLE = "Install Barcode Scanner?";
  public static final String DEFAULT_MESSAGE =
      "This application requires Barcode Scanner. Would you like to install it?";
  public static final String DEFAULT_YES = "Yes";
  public static final String DEFAULT_NO = "No";

  private static final String BS_PACKAGE = "com.google.zxing.client.android";
  private static final String BSPLUS_PACKAGE = "com.srowen.bs.android";

  public static final List<String> TARGET_ALL_KNOWN = list(
          BSPLUS_PACKAGE,             // Barcode Scanner+
          BSPLUS_PACKAGE + ".simple", // Barcode Scanner+ Simple
          BS_PACKAGE                  // Barcode Scanner          
          // What else supports this intent?
      );
  
  private final Activity activity;
  private final Fragment fragment;

  private String title;
  private String message;
  private String buttonYes;
  private String buttonNo;
  private List<String> targetApplications;
  private final Map<String,Object> moreExtras = new HashMap<String,Object>(3);

  /**
   * @param activity {@link Activity} invoking the integration
   */
  public IntentIntegrator(Activity activity) {
    this.activity = activity;
    this.fragment = null;
    initializeConfiguration();
  }

  /**
   * @param fragment {@link Fragment} invoking the integration.
   */
  public IntentIntegrator(Fragment fragment) {
    this.activity = fragment.getActivity();
    this.fragment = fragment;
    initializeConfiguration();
  }

  private void initializeConfiguration() {
    title = DEFAULT_TITLE;
    message = DEFAULT_MESSAGE;
    buttonYes = DEFAULT_YES;
    buttonNo = DEFAULT_NO;
    targetApplications = TARGET_ALL_KNOWN;
  }
  
  public String getTitle() {
    return title;
  }

  private String findTargetAppPackage(Intent intent) {
    PackageManager pm = activity.getPackageManager();
    List<ResolveInfo> availableApps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    if (availableApps != null) {
      for (String targetApp : targetApplications) {
        if (contains(availableApps, targetApp)) {
          return targetApp;
        }
      }
    }
    return null;
  }

  private static boolean contains(Iterable<ResolveInfo> availableApps, String targetApp) {
    for (ResolveInfo availableApp : availableApps) {
      String packageName = availableApp.activityInfo.packageName;
      if (targetApp.equals(packageName)) {
        return true;
      }
    }
    return false;
  }

  private AlertDialog showDownloadDialog() {
    AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
    downloadDialog.setTitle(title);
    downloadDialog.setMessage(message);
    downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        String packageName;
        if (targetApplications.contains(BS_PACKAGE)) {
          // Prefer to suggest download of BS if it's anywhere in the list
          packageName = BS_PACKAGE;
        } else {
          // Otherwise, first option:
          packageName = targetApplications.get(0);
        }
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
          if (fragment == null) {
            activity.startActivity(intent);
          } else {
            fragment.startActivity(intent);
          }
        } catch (ActivityNotFoundException anfe) {
          // Hmm, market is not installed
          Log.w(TAG, "Google Play is not installed; cannot install " + packageName);
        }
      }
    });
    downloadDialog.setNegativeButton(buttonNo, null);
    downloadDialog.setCancelable(true);
    return downloadDialog.show();
  }


  /**
   * Defaults to type "TEXT_TYPE".
   *
   * @param text the text string to encode as a barcode
   * @return the {@link AlertDialog} that was shown to the user prompting them to download the app
   *   if a prompt was needed, or null otherwise
   * @see #shareText(CharSequence, CharSequence)
   */
  public final AlertDialog shareText(CharSequence text) {
    return shareText(text, "TEXT_TYPE");
  }

  /**
   * Shares the given text by encoding it as a barcode, such that another user can
   * scan the text off the screen of the device.
   *
   * @param text the text string to encode as a barcode
   * @param type type of data to encode. See {@code com.google.zxing.client.android.Contents.Type} constants.
   * @return the {@link AlertDialog} that was shown to the user prompting them to download the app
   *   if a prompt was needed, or null otherwise
   */
  public final AlertDialog shareText(CharSequence text, CharSequence type) {
    Intent intent = new Intent();
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setAction(BS_PACKAGE + ".ENCODE");
    intent.putExtra("ENCODE_TYPE", type);
    intent.putExtra("ENCODE_DATA", text);
    String targetAppPackage = findTargetAppPackage(intent);
    if (targetAppPackage == null) {
      return showDownloadDialog();
    }
    intent.setPackage(targetAppPackage);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    attachMoreExtras(intent);
    if (fragment == null) {
      activity.startActivity(intent);
    } else {
      fragment.startActivity(intent);
    }
    return null;
  }
  
  private static List<String> list(String... values) {
    return Collections.unmodifiableList(Arrays.asList(values));
  }

  private void attachMoreExtras(Intent intent) {
    for (Map.Entry<String,Object> entry : moreExtras.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      // Kind of hacky
      if (value instanceof Integer) {
        intent.putExtra(key, (Integer) value);
      } else if (value instanceof Long) {
        intent.putExtra(key, (Long) value);
      } else if (value instanceof Boolean) {
        intent.putExtra(key, (Boolean) value);
      } else if (value instanceof Double) {
        intent.putExtra(key, (Double) value);
      } else if (value instanceof Float) {
        intent.putExtra(key, (Float) value);
      } else if (value instanceof Bundle) {
        intent.putExtra(key, (Bundle) value);
      } else {
        intent.putExtra(key, value.toString());
      }
    }
  }

}