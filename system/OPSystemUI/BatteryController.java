package com.android.systemui.statusbar.policy;

import com.android.systemui.DemoMode;
import com.android.systemui.Dumpable;

public interface BatteryController extends DemoMode, Dumpable, CallbackController<BatteryStateChangeCallback> {

    public interface BatteryStateChangeCallback {
        void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        }

        void onBatteryPercentShowChange(boolean z) {
        }

        void onBatteryStyleChanged(int i) {
        }

        void onFastChargeChanged(int i) {
        }

        void onOptimizatedStatusChange(boolean z) {
        }

        void onPowerSaveChanged(boolean z) {
        }
    }

    public interface EstimateFetchCompletion {
        void onBatteryRemainingEstimateRetrieved(String str);
    }

    void getEstimatedTimeRemainingString(EstimateFetchCompletion estimateFetchCompletion) {
    }

    boolean isFastCharging(int i) {
        return false;
    }

    boolean isPowerSave();

    boolean isWarpCharging(int i) {
        return false;
    }

    void setPowerSaveMode(boolean z);

    boolean isAodPowerSave() {
        return isPowerSave();
    }
}