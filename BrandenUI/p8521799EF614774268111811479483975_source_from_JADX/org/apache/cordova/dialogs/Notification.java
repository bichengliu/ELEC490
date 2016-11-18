package org.apache.cordova.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build.VERSION;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Notification extends CordovaPlugin {
    public int confirmResult;
    public ProgressDialog progressDialog;
    public ProgressDialog spinnerDialog;

    /* renamed from: org.apache.cordova.dialogs.Notification.1 */
    class C00771 implements Runnable {
        final /* synthetic */ long val$count;

        C00771(long j) {
            this.val$count = j;
        }

        public void run() {
            Ringtone notification = RingtoneManager.getRingtone(Notification.this.cordova.getActivity().getBaseContext(), RingtoneManager.getDefaultUri(2));
            if (notification != null) {
                for (long i = 0; i < this.val$count; i++) {
                    notification.play();
                    long timeout = 5000;
                    while (notification.isPlaying() && timeout > 0) {
                        timeout -= 100;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.apache.cordova.dialogs.Notification.6 */
    class C00816 implements Runnable {
        final /* synthetic */ CordovaInterface val$cordova;
        final /* synthetic */ String val$message;
        final /* synthetic */ Notification val$notification;
        final /* synthetic */ String val$title;

        /* renamed from: org.apache.cordova.dialogs.Notification.6.1 */
        class C00731 implements OnCancelListener {
            C00731() {
            }

            public void onCancel(DialogInterface dialog) {
                C00816.this.val$notification.progressDialog = null;
            }
        }

        C00816(Notification notification, CordovaInterface cordovaInterface, String str, String str2) {
            this.val$notification = notification;
            this.val$cordova = cordovaInterface;
            this.val$title = str;
            this.val$message = str2;
        }

        public void run() {
            this.val$notification.progressDialog = Notification.this.createProgressDialog(this.val$cordova);
            this.val$notification.progressDialog.setProgressStyle(1);
            this.val$notification.progressDialog.setTitle(this.val$title);
            this.val$notification.progressDialog.setMessage(this.val$message);
            this.val$notification.progressDialog.setCancelable(true);
            this.val$notification.progressDialog.setMax(100);
            this.val$notification.progressDialog.setProgress(0);
            this.val$notification.progressDialog.setOnCancelListener(new C00731());
            this.val$notification.progressDialog.show();
        }
    }

    /* renamed from: org.apache.cordova.dialogs.Notification.4 */
    class C00854 implements Runnable {
        final /* synthetic */ JSONArray val$buttonLabels;
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ CordovaInterface val$cordova;
        final /* synthetic */ String val$defaultText;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$title;

        /* renamed from: org.apache.cordova.dialogs.Notification.4.1 */
        class C00741 implements OnClickListener {
            final /* synthetic */ EditText val$promptInput;
            final /* synthetic */ JSONObject val$result;

            C00741(JSONObject jSONObject, EditText editText) {
                this.val$result = jSONObject;
                this.val$promptInput = editText;
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    this.val$result.put("buttonIndex", 1);
                    this.val$result.put("input1", this.val$promptInput.getText().toString().trim().length() == 0 ? C00854.this.val$defaultText : this.val$promptInput.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                C00854.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, this.val$result));
            }
        }

        /* renamed from: org.apache.cordova.dialogs.Notification.4.2 */
        class C00752 implements OnClickListener {
            final /* synthetic */ EditText val$promptInput;
            final /* synthetic */ JSONObject val$result;

            C00752(JSONObject jSONObject, EditText editText) {
                this.val$result = jSONObject;
                this.val$promptInput = editText;
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    this.val$result.put("buttonIndex", 2);
                    this.val$result.put("input1", this.val$promptInput.getText().toString().trim().length() == 0 ? C00854.this.val$defaultText : this.val$promptInput.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                C00854.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, this.val$result));
            }
        }

        /* renamed from: org.apache.cordova.dialogs.Notification.4.3 */
        class C00763 implements OnClickListener {
            final /* synthetic */ EditText val$promptInput;
            final /* synthetic */ JSONObject val$result;

            C00763(JSONObject jSONObject, EditText editText) {
                this.val$result = jSONObject;
                this.val$promptInput = editText;
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    this.val$result.put("buttonIndex", 3);
                    this.val$result.put("input1", this.val$promptInput.getText().toString().trim().length() == 0 ? C00854.this.val$defaultText : this.val$promptInput.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                C00854.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, this.val$result));
            }
        }

        /* renamed from: org.apache.cordova.dialogs.Notification.4.4 */
        class C00844 implements OnCancelListener {
            final /* synthetic */ EditText val$promptInput;
            final /* synthetic */ JSONObject val$result;

            C00844(JSONObject jSONObject, EditText editText) {
                this.val$result = jSONObject;
                this.val$promptInput = editText;
            }

            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                try {
                    this.val$result.put("buttonIndex", 0);
                    this.val$result.put("input1", this.val$promptInput.getText().toString().trim().length() == 0 ? C00854.this.val$defaultText : this.val$promptInput.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                C00854.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, this.val$result));
            }
        }

        C00854(CordovaInterface cordovaInterface, String str, String str2, String str3, JSONArray jSONArray, CallbackContext callbackContext) {
            this.val$cordova = cordovaInterface;
            this.val$defaultText = str;
            this.val$message = str2;
            this.val$title = str3;
            this.val$buttonLabels = jSONArray;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            EditText promptInput = new EditText(this.val$cordova.getActivity());
            promptInput.setHint(this.val$defaultText);
            Builder dlg = Notification.this.createDialog(this.val$cordova);
            dlg.setMessage(this.val$message);
            dlg.setTitle(this.val$title);
            dlg.setCancelable(true);
            dlg.setView(promptInput);
            JSONObject result = new JSONObject();
            if (this.val$buttonLabels.length() > 0) {
                try {
                    dlg.setNegativeButton(this.val$buttonLabels.getString(0), new C00741(result, promptInput));
                } catch (JSONException e) {
                }
            }
            if (this.val$buttonLabels.length() > 1) {
                try {
                    dlg.setNeutralButton(this.val$buttonLabels.getString(1), new C00752(result, promptInput));
                } catch (JSONException e2) {
                }
            }
            if (this.val$buttonLabels.length() > 2) {
                try {
                    dlg.setPositiveButton(this.val$buttonLabels.getString(2), new C00763(result, promptInput));
                } catch (JSONException e3) {
                }
            }
            dlg.setOnCancelListener(new C00844(result, promptInput));
            Notification.this.changeTextDirection(dlg);
        }
    }

    /* renamed from: org.apache.cordova.dialogs.Notification.2 */
    class C00872 implements Runnable {
        final /* synthetic */ String val$buttonLabel;
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ CordovaInterface val$cordova;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$title;

        /* renamed from: org.apache.cordova.dialogs.Notification.2.2 */
        class C00792 implements OnCancelListener {
            C00792() {
            }

            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                C00872.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, 0));
            }
        }

        /* renamed from: org.apache.cordova.dialogs.Notification.2.1 */
        class C00821 implements OnClickListener {
            C00821() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                C00872.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, 0));
            }
        }

        C00872(CordovaInterface cordovaInterface, String str, String str2, String str3, CallbackContext callbackContext) {
            this.val$cordova = cordovaInterface;
            this.val$message = str;
            this.val$title = str2;
            this.val$buttonLabel = str3;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            Builder dlg = Notification.this.createDialog(this.val$cordova);
            dlg.setMessage(this.val$message);
            dlg.setTitle(this.val$title);
            dlg.setCancelable(true);
            dlg.setPositiveButton(this.val$buttonLabel, new C00821());
            dlg.setOnCancelListener(new C00792());
            Notification.this.changeTextDirection(dlg);
        }
    }

    /* renamed from: org.apache.cordova.dialogs.Notification.5 */
    class C00885 implements Runnable {
        final /* synthetic */ CordovaInterface val$cordova;
        final /* synthetic */ String val$message;
        final /* synthetic */ Notification val$notification;
        final /* synthetic */ String val$title;

        /* renamed from: org.apache.cordova.dialogs.Notification.5.1 */
        class C00901 implements OnCancelListener {
            C00901() {
            }

            public void onCancel(DialogInterface dialog) {
                C00885.this.val$notification.spinnerDialog = null;
            }
        }

        C00885(Notification notification, CordovaInterface cordovaInterface, String str, String str2) {
            this.val$notification = notification;
            this.val$cordova = cordovaInterface;
            this.val$title = str;
            this.val$message = str2;
        }

        public void run() {
            this.val$notification.spinnerDialog = Notification.this.createProgressDialog(this.val$cordova);
            this.val$notification.spinnerDialog.setTitle(this.val$title);
            this.val$notification.spinnerDialog.setMessage(this.val$message);
            this.val$notification.spinnerDialog.setCancelable(true);
            this.val$notification.spinnerDialog.setIndeterminate(true);
            this.val$notification.spinnerDialog.setOnCancelListener(new C00901());
            this.val$notification.spinnerDialog.show();
        }
    }

    /* renamed from: org.apache.cordova.dialogs.Notification.3 */
    class C00893 implements Runnable {
        final /* synthetic */ JSONArray val$buttonLabels;
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ CordovaInterface val$cordova;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$title;

        /* renamed from: org.apache.cordova.dialogs.Notification.3.4 */
        class C00784 implements OnCancelListener {
            C00784() {
            }

            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                C00893.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, 0));
            }
        }

        /* renamed from: org.apache.cordova.dialogs.Notification.3.3 */
        class C00803 implements OnClickListener {
            C00803() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                C00893.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, 3));
            }
        }

        /* renamed from: org.apache.cordova.dialogs.Notification.3.2 */
        class C00832 implements OnClickListener {
            C00832() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                C00893.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, 2));
            }
        }

        /* renamed from: org.apache.cordova.dialogs.Notification.3.1 */
        class C00861 implements OnClickListener {
            C00861() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                C00893.this.val$callbackContext.sendPluginResult(new PluginResult(Status.OK, 1));
            }
        }

        C00893(CordovaInterface cordovaInterface, String str, String str2, JSONArray jSONArray, CallbackContext callbackContext) {
            this.val$cordova = cordovaInterface;
            this.val$message = str;
            this.val$title = str2;
            this.val$buttonLabels = jSONArray;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            Builder dlg = Notification.this.createDialog(this.val$cordova);
            dlg.setMessage(this.val$message);
            dlg.setTitle(this.val$title);
            dlg.setCancelable(true);
            if (this.val$buttonLabels.length() > 0) {
                try {
                    dlg.setNegativeButton(this.val$buttonLabels.getString(0), new C00861());
                } catch (JSONException e) {
                }
            }
            if (this.val$buttonLabels.length() > 1) {
                try {
                    dlg.setNeutralButton(this.val$buttonLabels.getString(1), new C00832());
                } catch (JSONException e2) {
                }
            }
            if (this.val$buttonLabels.length() > 2) {
                try {
                    dlg.setPositiveButton(this.val$buttonLabels.getString(2), new C00803());
                } catch (JSONException e3) {
                }
            }
            dlg.setOnCancelListener(new C00784());
            Notification.this.changeTextDirection(dlg);
        }
    }

    public Notification() {
        this.confirmResult = -1;
        this.spinnerDialog = null;
        this.progressDialog = null;
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (this.cordova.getActivity().isFinishing()) {
            return true;
        }
        if (action.equals("beep")) {
            beep(args.getLong(0));
        } else if (action.equals("alert")) {
            alert(args.getString(0), args.getString(1), args.getString(2), callbackContext);
            return true;
        } else if (action.equals("confirm")) {
            confirm(args.getString(0), args.getString(1), args.getJSONArray(2), callbackContext);
            return true;
        } else if (action.equals("prompt")) {
            prompt(args.getString(0), args.getString(1), args.getJSONArray(2), args.getString(3), callbackContext);
            return true;
        } else if (action.equals("activityStart")) {
            activityStart(args.getString(0), args.getString(1));
        } else if (action.equals("activityStop")) {
            activityStop();
        } else if (action.equals("progressStart")) {
            progressStart(args.getString(0), args.getString(1));
        } else if (action.equals("progressValue")) {
            progressValue(args.getInt(0));
        } else if (!action.equals("progressStop")) {
            return false;
        } else {
            progressStop();
        }
        callbackContext.success();
        return true;
    }

    public void beep(long count) {
        this.cordova.getThreadPool().execute(new C00771(count));
    }

    public synchronized void alert(String message, String title, String buttonLabel, CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new C00872(this.cordova, message, title, buttonLabel, callbackContext));
    }

    public synchronized void confirm(String message, String title, JSONArray buttonLabels, CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new C00893(this.cordova, message, title, buttonLabels, callbackContext));
    }

    public synchronized void prompt(String message, String title, JSONArray buttonLabels, String defaultText, CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new C00854(this.cordova, defaultText, message, title, buttonLabels, callbackContext));
    }

    public synchronized void activityStart(String title, String message) {
        if (this.spinnerDialog != null) {
            this.spinnerDialog.dismiss();
            this.spinnerDialog = null;
        }
        this.cordova.getActivity().runOnUiThread(new C00885(this, this.cordova, title, message));
    }

    public synchronized void activityStop() {
        if (this.spinnerDialog != null) {
            this.spinnerDialog.dismiss();
            this.spinnerDialog = null;
        }
    }

    public synchronized void progressStart(String title, String message) {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
        this.cordova.getActivity().runOnUiThread(new C00816(this, this.cordova, title, message));
    }

    public synchronized void progressValue(int value) {
        if (this.progressDialog != null) {
            this.progressDialog.setProgress(value);
        }
    }

    public synchronized void progressStop() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }

    @SuppressLint({"NewApi"})
    private Builder createDialog(CordovaInterface cordova) {
        if (VERSION.SDK_INT >= 11) {
            return new Builder(cordova.getActivity(), 5);
        }
        return new Builder(cordova.getActivity());
    }

    @SuppressLint({"InlinedApi"})
    private ProgressDialog createProgressDialog(CordovaInterface cordova) {
        if (VERSION.SDK_INT >= 14) {
            return new ProgressDialog(cordova.getActivity(), 5);
        }
        return new ProgressDialog(cordova.getActivity());
    }

    @SuppressLint({"NewApi"})
    private void changeTextDirection(Builder dlg) {
        int currentapiVersion = VERSION.SDK_INT;
        dlg.create();
        AlertDialog dialog = dlg.show();
        if (currentapiVersion >= 17) {
            ((TextView) dialog.findViewById(16908299)).setTextDirection(5);
        }
    }
}
