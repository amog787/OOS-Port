package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.Dependency;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.statusbar.policy.BatteryController;
import com.oneplus.util.OpUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class BatteryControllerImpl extends BroadcastReceiver implements BatteryController {
    private static final boolean DEBUG = Log.isLoggable("BatteryController", 3);
    protected boolean mAodPowerSave;
    /* access modifiers changed from: private */
    public int mBatteryStyle;
    private final ArrayList<BatteryController.BatteryStateChangeCallback> mChangeCallbacks;
    protected boolean mCharged;
    protected boolean mCharging;
    /* access modifiers changed from: private */
    public final Context mContext;
    private boolean mDemoMode;
    private Estimate mEstimate;
    private final EnhancedEstimates mEstimates;
    private int mFastchargeType;
    private final ArrayList<BatteryController.EstimateFetchCompletion> mFetchCallbacks;
    private boolean mFetchingEstimate;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mHasReceivedBattery;
    /* access modifiers changed from: private */
    public boolean mIsOptimizatedCharge;
    protected int mLevel;
    protected boolean mPluggedIn;
    private final PowerManager mPowerManager;
    protected boolean mPowerSave;
    private final SettingObserver mSettingObserver;
    /* access modifiers changed from: private */
    public boolean mShowPercent;
    /* access modifiers changed from: private */
    public boolean mTestmode;

    private final class SettingObserver extends ContentObserver {
        private final int BATTERY_BAR_STYLE = 0;
        private final int BATTERY_PERCENT_STYLE = 3;
        private final Uri CHARING_OPTIMIZATED_STATUS_URI = Settings.System.getUriFor("charging_optimized_status");
        private final Uri SHOW_BATTERY_PERCENT = Settings.System.getUriFor("status_bar_show_battery_percent");
        private final Uri STATUS_BAR_BATTERY_STYLE = Settings.System.getUriFor("status_bar_battery_style");

        public SettingObserver() {
            super(new Handler());
        }

        /* access modifiers changed from: package-private */
        public void observe() {
            ContentResolver contentResolver = BatteryControllerImpl.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(this.SHOW_BATTERY_PERCENT, false, this, -1);
            contentResolver.registerContentObserver(this.STATUS_BAR_BATTERY_STYLE, false, this, -1);
            contentResolver.registerContentObserver(this.CHARING_OPTIMIZATED_STATUS_URI, false, this, -1);
            update((Uri) null);
        }

        public void onChange(boolean z, Uri uri) {
            update(uri);
        }

        public void update(Uri uri) {
            boolean z;
            ContentResolver contentResolver = BatteryControllerImpl.this.mContext.getContentResolver();
            boolean z2 = true;
            boolean z3 = false;
            if (uri == null || this.SHOW_BATTERY_PERCENT.equals(uri)) {
                boolean unused = BatteryControllerImpl.this.mShowPercent = Settings.System.getIntForUser(contentResolver, "status_bar_show_battery_percent", 0, KeyguardUpdateMonitor.getCurrentUser()) != 0;
                z = true;
            } else {
                z = false;
            }
            if (uri == null || this.STATUS_BAR_BATTERY_STYLE.equals(uri)) {
                int unused2 = BatteryControllerImpl.this.mBatteryStyle = Settings.System.getIntForUser(contentResolver, "status_bar_battery_style", 0, KeyguardUpdateMonitor.getCurrentUser());
                if (BatteryControllerImpl.this.mBatteryStyle == 3) {
                    int unused3 = BatteryControllerImpl.this.mBatteryStyle = 0;
                    boolean unused4 = BatteryControllerImpl.this.mShowPercent = true;
                    Log.d("BatteryController", "Migrate battery style from percent to bar and show percentage.");
                    Settings.System.putIntForUser(contentResolver, "status_bar_battery_style", BatteryControllerImpl.this.mBatteryStyle, KeyguardUpdateMonitor.getCurrentUser());
                    Settings.System.putIntForUser(contentResolver, "status_bar_show_battery_percent", 1, KeyguardUpdateMonitor.getCurrentUser());
                }
                z = true;
            }
            if (uri == null || this.CHARING_OPTIMIZATED_STATUS_URI.equals(uri)) {
                BatteryControllerImpl batteryControllerImpl = BatteryControllerImpl.this;
                if (Settings.System.getInt(contentResolver, "charging_optimized_status", 0) != 0) {
                    z3 = true;
                }
                boolean unused5 = batteryControllerImpl.mIsOptimizatedCharge = z3;
            } else {
                z2 = false;
            }
            Log.d("BatteryController", "update uri: " + uri + " mShowPercent: " + BatteryControllerImpl.this.mShowPercent + " mBatteryStyle: " + BatteryControllerImpl.this.mBatteryStyle + " mIsOptimizatedCharge:" + BatteryControllerImpl.this.mIsOptimizatedCharge);
            if (z) {
                BatteryControllerImpl.this.fireBatteryStylechange();
            }
            if (z2) {
                BatteryControllerImpl.this.fireOptimizatedStatusChange();
            }
        }
    }

    public boolean isFastCharging(int i) {
        return i == 1;
    }

    public boolean isWarpCharging(int i) {
        return i == 2 || i == 3;
    }

    public BatteryControllerImpl(Context context, EnhancedEstimates enhancedEstimates) {
        this(context, enhancedEstimates, (PowerManager) context.getSystemService(PowerManager.class));
    }

    @VisibleForTesting
    BatteryControllerImpl(Context context, EnhancedEstimates enhancedEstimates, PowerManager powerManager) {
        this.mChangeCallbacks = new ArrayList<>();
        this.mFetchCallbacks = new ArrayList<>();
        this.mTestmode = false;
        this.mHasReceivedBattery = false;
        this.mFetchingEstimate = false;
        this.mFastchargeType = 0;
        this.mShowPercent = false;
        this.mSettingObserver = new SettingObserver();
        this.mBatteryStyle = 0;
        this.mIsOptimizatedCharge = false;
        this.mContext = context;
        this.mHandler = new Handler();
        this.mPowerManager = powerManager;
        this.mEstimates = enhancedEstimates;
        registerReceiver();
        updatePowerSave();
        updateEstimate();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGING");
        intentFilter.addAction("com.android.systemui.BATTERY_LEVEL_TEST");
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        this.mContext.registerReceiver(this, intentFilter);
        this.mSettingObserver.observe();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("BatteryController state:");
        printWriter.print("  mLevel=");
        printWriter.println(this.mLevel);
        printWriter.print("  mPluggedIn=");
        printWriter.println(this.mPluggedIn);
        printWriter.print("  mCharging=");
        printWriter.println(this.mCharging);
        printWriter.print("  mCharged=");
        printWriter.println(this.mCharged);
        printWriter.print("  mPowerSave=");
        printWriter.println(this.mPowerSave);
        printWriter.print("  mShowPercent=");
        printWriter.println(this.mShowPercent);
        printWriter.print("  mBatteryStyle=");
        printWriter.println(this.mBatteryStyle);
    }

    public void setPowerSaveMode(boolean z) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, z, true);
    }

    public void addCallback(BatteryController.BatteryStateChangeCallback batteryStateChangeCallback) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.add(batteryStateChangeCallback);
        }
        batteryStateChangeCallback.onOptimizatedStatusChange(this.mIsOptimizatedCharge);
        if (this.mHasReceivedBattery) {
            batteryStateChangeCallback.onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            batteryStateChangeCallback.onPowerSaveChanged(this.mPowerSave);
            batteryStateChangeCallback.onFastChargeChanged(this.mFastchargeType);
            batteryStateChangeCallback.onBatteryStyleChanged(this.mBatteryStyle);
            batteryStateChangeCallback.onBatteryPercentShowChange(this.mShowPercent);
        }
    }

    public void removeCallback(BatteryController.BatteryStateChangeCallback batteryStateChangeCallback) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.remove(batteryStateChangeCallback);
        }
    }

    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.BATTERY_CHANGED")) {
            if (!this.mTestmode || intent.getBooleanExtra("testmode", false)) {
                boolean z = !this.mHasReceivedBattery;
                this.mHasReceivedBattery = true;
                this.mLevel = (int) ((((float) intent.getIntExtra("level", 0)) * 100.0f) / ((float) intent.getIntExtra("scale", 100)));
                this.mPluggedIn = intent.getIntExtra("plugged", 0) != 0;
                int intExtra = intent.getIntExtra("status", 1);
                this.mCharged = intExtra == 5;
                this.mCharging = this.mCharged || intExtra == 2;
                int intExtra2 = intent.getIntExtra("fastcharge_status", 0);
                if (!OpUtils.SUPPORT_WARP_CHARGING) {
                    intExtra2 = intExtra2 > 0 ? 1 : 0;
                }
                this.mFastchargeType = intExtra2;
                if (z) {
                    fireBatteryStylechange();
                }
                fireBatteryLevelChanged();
            }
        } else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
            updatePowerSave();
        } else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGING")) {
            setPowerSave(intent.getBooleanExtra("mode", false));
        } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            this.mSettingObserver.update((Uri) null);
        } else if (action.equals("com.android.systemui.BATTERY_LEVEL_TEST")) {
            this.mTestmode = true;
            this.mHandler.post(new Runnable() {
                int curLevel = 0;
                Intent dummy;
                int incr = 1;
                int saveLevel;
                boolean savePlugged;

                {
                    BatteryControllerImpl batteryControllerImpl = BatteryControllerImpl.this;
                    this.saveLevel = batteryControllerImpl.mLevel;
                    this.savePlugged = batteryControllerImpl.mPluggedIn;
                    this.dummy = new Intent("android.intent.action.BATTERY_CHANGED");
                }

                public void run() {
                    int i = this.curLevel;
                    int i2 = 0;
                    if (i < 0) {
                        boolean unused = BatteryControllerImpl.this.mTestmode = false;
                        this.dummy.putExtra("level", this.saveLevel);
                        this.dummy.putExtra("plugged", this.savePlugged);
                        this.dummy.putExtra("testmode", false);
                    } else {
                        this.dummy.putExtra("level", i);
                        Intent intent = this.dummy;
                        if (this.incr > 0) {
                            i2 = 1;
                        }
                        intent.putExtra("plugged", i2);
                        this.dummy.putExtra("testmode", true);
                    }
                    context.sendBroadcast(this.dummy);
                    if (BatteryControllerImpl.this.mTestmode) {
                        int i3 = this.curLevel;
                        int i4 = this.incr;
                        this.curLevel = i3 + i4;
                        if (this.curLevel == 100) {
                            this.incr = i4 * -1;
                        }
                        BatteryControllerImpl.this.mHandler.postDelayed(this, 200);
                    }
                }
            });
        }
    }

    public boolean isPowerSave() {
        return this.mPowerSave;
    }

    public boolean isAodPowerSave() {
        return this.mAodPowerSave;
    }

    public void getEstimatedTimeRemainingString(BatteryController.EstimateFetchCompletion estimateFetchCompletion) {
        synchronized (this.mFetchCallbacks) {
            this.mFetchCallbacks.add(estimateFetchCompletion);
        }
        updateEstimateInBackground();
    }

    private String generateTimeRemainingString() {
        synchronized (this.mFetchCallbacks) {
            if (this.mEstimate == null) {
                return null;
            }
            NumberFormat.getPercentInstance().format(((double) this.mLevel) / 100.0d);
            String batteryRemainingShortStringFormatted = PowerUtil.getBatteryRemainingShortStringFormatted(this.mContext, this.mEstimate.getEstimateMillis());
            return batteryRemainingShortStringFormatted;
        }
    }

    private void updateEstimateInBackground() {
        if (!this.mFetchingEstimate) {
            this.mFetchingEstimate = true;
            ((Handler) Dependency.get(Dependency.BG_HANDLER)).post(new Runnable() {
                public final void run() {
                    BatteryControllerImpl.this.lambda$updateEstimateInBackground$0$BatteryControllerImpl();
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateEstimateInBackground$0$BatteryControllerImpl() {
        synchronized (this.mFetchCallbacks) {
            this.mEstimate = null;
            if (this.mEstimates.isHybridNotificationEnabled()) {
                updateEstimate();
            }
        }
        this.mFetchingEstimate = false;
        ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() {
            public final void run() {
                BatteryControllerImpl.this.notifyEstimateFetchCallbacks();
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyEstimateFetchCallbacks() {
        synchronized (this.mFetchCallbacks) {
            String generateTimeRemainingString = generateTimeRemainingString();
            Iterator<BatteryController.EstimateFetchCompletion> it = this.mFetchCallbacks.iterator();
            while (it.hasNext()) {
                it.next().onBatteryRemainingEstimateRetrieved(generateTimeRemainingString);
            }
            this.mFetchCallbacks.clear();
        }
    }

    private void updateEstimate() {
        this.mEstimate = Estimate.getCachedEstimateIfAvailable(this.mContext);
        if (this.mEstimate == null) {
            this.mEstimate = this.mEstimates.getEstimate();
            Estimate estimate = this.mEstimate;
            if (estimate != null) {
                Estimate.storeCachedEstimate(this.mContext, estimate);
            }
        }
    }

    private void updatePowerSave() {
        setPowerSave(this.mPowerManager.isPowerSaveMode());
    }

    private void setPowerSave(boolean z) {
        if (z != this.mPowerSave) {
            this.mPowerSave = z;
            this.mAodPowerSave = this.mPowerManager.getPowerSaveState(14).batterySaverEnabled;
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Power save is ");
                sb.append(this.mPowerSave ? "on" : "off");
                Log.d("BatteryController", sb.toString());
            }
            firePowerSaveChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void fireBatteryLevelChanged() {
        if (Build.DEBUG_ONEPLUS) {
            Log.i("BatteryController", " fireBatteryLevelChanged mLevel:" + this.mLevel + " PluggedIn:" + this.mPluggedIn + " Charging:" + this.mCharging + " mFastchargeType:" + this.mFastchargeType + " show:" + this.mShowPercent + " style:" + this.mBatteryStyle);
        }
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                this.mChangeCallbacks.get(i).onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
                this.mChangeCallbacks.get(i).onFastChargeChanged(this.mFastchargeType);
            }
        }
    }

    private void firePowerSaveChanged() {
        Log.i("BatteryController", " firePowerSaveChanged mPowerSave:" + this.mPowerSave);
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                try {
                    this.mChangeCallbacks.get(i).onPowerSaveChanged(this.mPowerSave);
                } catch (IndexOutOfBoundsException e) {
                    Log.i("BatteryController", "firePowerSaveChanged:" + e.getMessage());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fireBatteryStylechange() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            Log.i("BatteryController", " fireBatteryStylechange mShowPercent:" + this.mShowPercent + " mBatteryStyle:" + this.mBatteryStyle + " mFastchargeType:" + this.mFastchargeType);
            for (int i = 0; i < size; i++) {
                try {
                    this.mChangeCallbacks.get(i).onBatteryPercentShowChange(this.mShowPercent);
                    this.mChangeCallbacks.get(i).onBatteryStyleChanged(this.mBatteryStyle);
                } catch (IndexOutOfBoundsException e) {
                    Log.i("BatteryController", " fireBatteryStylechange:" + e.getMessage());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fireOptimizatedStatusChange() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            Log.i("BatteryController", " fireOptimizatedStatusChange mIsOptimizatedCharge:" + this.mIsOptimizatedCharge);
            for (int i = 0; i < size; i++) {
                try {
                    this.mChangeCallbacks.get(i).onOptimizatedStatusChange(this.mIsOptimizatedCharge);
                } catch (IndexOutOfBoundsException e) {
                    Log.i("BatteryController", " fireOptimizatedStatusChange:" + e.getMessage());
                }
            }
        }
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        if (!this.mDemoMode && str.equals("enter")) {
            this.mDemoMode = true;
            this.mContext.unregisterReceiver(this);
        } else if (this.mDemoMode && str.equals("exit")) {
            this.mDemoMode = false;
            registerReceiver();
            updatePowerSave();
        } else if (this.mDemoMode && str.equals("battery")) {
            String string = bundle.getString("level");
            String string2 = bundle.getString("plugged");
            String string3 = bundle.getString("powersave");
            String string4 = bundle.getString("powerOptimizated");
            if (string != null) {
                this.mLevel = Math.min(Math.max(Integer.parseInt(string), 0), 100);
            }
            if (string2 != null) {
                this.mPluggedIn = Boolean.parseBoolean(string2);
            }
            if (string3 != null) {
                this.mPowerSave = string3.equals("true");
                firePowerSaveChanged();
            }
            if (string4 != null) {
                this.mIsOptimizatedCharge = string4.equals("true");
                fireOptimizatedStatusChange();
            }
            fireBatteryLevelChanged();
        }
    }
}