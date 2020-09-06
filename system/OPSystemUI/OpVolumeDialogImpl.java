package com.oneplus.volume;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.mediarouter.media.MediaRouter;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.volume.MediaRouterWrapper;
import com.android.systemui.volume.VolumeDialogImpl;
import com.oneplus.plugin.OpLsState;
import com.oneplus.util.OpReflectionUtils;
import com.oneplus.util.OpUtils;
import com.oneplus.util.ThemeColorUtils;
import com.oneplus.volume.OpOutputChooser;
import java.util.ArrayList;
import java.util.List;

public class OpVolumeDialogImpl {
    protected int mAccentColor = 0;
    protected View.OnClickListener mClickOutputChooser;
    protected TextView mConnectedDevice;
    /* access modifiers changed from: protected */
    public Context mContext;
    protected DeviceInfo mDeviceInfo;
    protected ViewGroup mDialogLower;
    protected ViewGroup mDialogRowContainer;
    protected ViewGroup mDialogUpper;
    protected boolean mFirstTimeInitDialog = true;
    protected OpHandler mHandler = new OpHandler();
    /* access modifiers changed from: protected */
    public boolean mIsExpandAnimDone = true;
    /* access modifiers changed from: protected */
    public ViewGroup mODICaptionsView;
    /* access modifiers changed from: protected */
    public int mOpBeforeExpandWidth;
    /* access modifiers changed from: protected */
    public boolean mOpForceExpandState = false;
    protected boolean mOpLastforceExpandState = false;
    protected OpOutputChooser mOpOutputChooser;
    /* access modifiers changed from: protected */
    public int mOpafterExpandWidth;
    protected ImageButton mOutputChooser;
    protected View mOutputChooserBackgroundView;
    protected OpOutputChooser.OutputChooserCallback mOutputChooserCallback = new OpOutputChooser.OutputChooserCallback() {
        public void onOutputChooserNotifyActiveDeviceChange(int i, int i2, String str, String str2) {
            Log.i("OpVolumeDialogImpl", "recevie OutputChooserCallback, deviceInfoType:" + i + ", iconResId:" + i2 + ", deviceInfoName:" + str + ", deviceInfoAddress:" + str2);
            DeviceInfo deviceInfo = OpVolumeDialogImpl.this.mDeviceInfo;
            deviceInfo.deviceInfoType = i;
            deviceInfo.iconResId = i2;
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" ");
            deviceInfo.deviceInfoName = sb.toString();
            OpVolumeDialogImpl.this.mDeviceInfo.deviceInfoAddress = str2;
        }
    };
    protected OpOutputChooserDialog mOutputChooserDialog;
    protected EditText mOutputChooserExpandEditText;
    protected ImageView mOutputChooserExpandIcon;
    protected TextView mOutputChooserExpandTextView;
    /* access modifiers changed from: private */
    public final Object mOutputChooserLock = new Object();
    /* access modifiers changed from: protected */
    public boolean mPendingInit = true;
    protected List<VolumeRow> mRows = new ArrayList();
    protected int mThemeButtonBg = 0;
    protected int mThemeColorDialogBackground = 0;
    protected int mThemeColorDialogRowContainerBackground = 0;
    protected int mThemeColorIcon = 0;
    protected int mThemeColorMode = 0;
    protected int mThemeColorSeekbarBackgroundDrawable = 0;
    protected int mThemeColorText = 0;

    protected class DeviceInfo {
        public String deviceInfoAddress = "";
        public String deviceInfoName = "";
        public int deviceInfoType = -1;
        public int iconResId = 0;

        public DeviceInfo() {
        }
    }

    protected class OpHandler extends Handler {
        protected OpHandler() {
        }

        public void handleMessage(Message message) {
            if (message.what != 1) {
                Log.w("OpVolumeDialogImpl", "Unknown message: " + message.what);
                return;
            }
            OpVolumeDialogImpl.this.setDialogWidthH(message.arg1);
        }
    }

    public static class VolumeRow {
        public ObjectAnimator anim;
        public int animTargetProgress;
        public ColorStateList cachedTint;
        public boolean defaultStream;
        public FrameLayout dndIcon;
        public TextView header;
        public ImageButton icon;
        public int iconMuteRes;
        public int iconRes;
        public int iconState;
        public boolean important;
        public int lastAudibleLevel = 1;
        public int requestedLevel = -1;
        public SeekBar slider;
        public VolumeDialogController.StreamState ss;
        public int stream;
        public int themeColorMode = 2;
        public boolean tracking;
        public long userAttempt;
        public View view;
    }

    /* access modifiers changed from: protected */
    public void setOpOutputChooserGravityNeedBeforeAnimStart(boolean z) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mOutputChooser.getLayoutParams();
        if (z) {
            layoutParams.gravity = 3;
            this.mOutputChooser.setLayoutParams(layoutParams);
            return;
        }
        layoutParams.gravity = 17;
        this.mOutputChooser.setLayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public void setOpOutputChooserVisible(boolean z) {
        setOpOutputChooserVisible(z, false);
    }

    /* access modifiers changed from: protected */
    public void setOpOutputChooserVisible(boolean z, boolean z2) {
        if (OpUtils.DEBUG_ONEPLUS) {
            Log.i("OpVolumeDialogImpl", "setOpOutputChooserVisible:" + z + ", mOpForceExpandState:" + this.mOpForceExpandState);
        }
        FrameLayout frameLayout = (FrameLayout) getDialogView().findViewById(R$id.output_chooser_background_container);
        LinearLayout linearLayout = (LinearLayout) getDialogView().findViewById(R$id.output_active_device_container);
        if (z) {
            this.mOutputChooserExpandIcon.setImageResource(this.mDeviceInfo.iconResId);
            this.mOutputChooserExpandEditText.setText(this.mDeviceInfo.deviceInfoName);
            this.mOutputChooserExpandTextView.setText(this.mDeviceInfo.deviceInfoName);
            linearLayout.setVisibility(0);
            if (z2) {
                this.mOutputChooserExpandIcon.setVisibility(0);
                this.mOutputChooser.setVisibility(8);
                this.mOutputChooserExpandEditText.setVisibility(0);
            } else {
                setViewVisibleGoneFadeInOutAndScaleAnim(this.mOutputChooserExpandIcon, true);
                setViewVisibleGoneFadeInOutAndScaleAnim(this.mOutputChooser, false);
                setViewVisibleGoneFadeInOutAnim(this.mOutputChooserExpandEditText, true, linearLayout);
            }
            this.mOutputChooserExpandTextView.setSelected(true);
        } else if (z2) {
            this.mOutputChooserExpandIcon.setVisibility(8);
            this.mOutputChooser.setVisibility(0);
            this.mOutputChooserExpandTextView.setVisibility(8);
            linearLayout.setVisibility(8);
        } else {
            setViewVisibleGoneFadeInOutAndScaleAnim(this.mOutputChooserExpandIcon, false);
            setViewVisibleGoneFadeInOutAndScaleAnim(this.mOutputChooser, true);
            setViewVisibleGoneFadeInOutAnim(this.mOutputChooserExpandTextView, false, linearLayout);
        }
    }

    private void setViewVisibleGoneFadeInOutAndScaleAnim(View view, boolean z) {
        float f = 0.5f;
        if (z) {
            view.setAlpha(0.0f);
            view.setScaleX(0.5f);
            view.setScaleY(0.5f);
            view.setVisibility(0);
        }
        ViewPropertyAnimator scaleX = view.animate().alpha(z ? 1.0f : 0.0f).scaleX(z ? 1.0f : 0.5f);
        if (z) {
            f = 1.0f;
        }
        scaleX.scaleY(f).setDuration(275).setInterpolator(new PathInterpolator(0.4f, 0.0f, 0.4f, 1.0f)).withEndAction(new Runnable(z, view) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ View f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                OpVolumeDialogImpl.lambda$setViewVisibleGoneFadeInOutAndScaleAnim$0(this.f$0, this.f$1);
            }
        }).setStartDelay(0);
    }

    static /* synthetic */ void lambda$setViewVisibleGoneFadeInOutAndScaleAnim$0(boolean z, View view) {
        if (!z) {
            view.setVisibility(8);
        }
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setAlpha(1.0f);
    }

    private void setViewVisibleGoneFadeInOutAnim(View view, boolean z, LinearLayout linearLayout) {
        if (z) {
            view.setAlpha(0.0f);
            view.setVisibility(0);
        }
        view.animate().alpha(z ? 1.0f : 0.0f).setDuration(275).setInterpolator(new PathInterpolator(0.4f, 0.0f, 0.4f, 1.0f)).withEndAction(new Runnable(z, view, linearLayout) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ View f$2;
            private final /* synthetic */ LinearLayout f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                OpVolumeDialogImpl.this.lambda$setViewVisibleGoneFadeInOutAnim$1$OpVolumeDialogImpl(this.f$1, this.f$2, this.f$3);
            }
        }).setStartDelay(0);
    }

    public /* synthetic */ void lambda$setViewVisibleGoneFadeInOutAnim$1$OpVolumeDialogImpl(boolean z, View view, LinearLayout linearLayout) {
        if (!z) {
            view.setVisibility(8);
            linearLayout.setVisibility(8);
            changeEditTextAndTextViewForMarquee(false);
        } else {
            changeEditTextAndTextViewForMarquee(true);
        }
        view.setAlpha(1.0f);
    }

    /* access modifiers changed from: protected */
    public void changeEditTextAndTextViewForMarquee(boolean z) {
        if (z) {
            this.mOutputChooserExpandEditText.setVisibility(8);
            this.mOutputChooserExpandTextView.setVisibility(0);
            return;
        }
        this.mOutputChooserExpandEditText.setVisibility(0);
        this.mOutputChooserExpandTextView.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void setDialogWidth(int i) {
        this.mHandler.removeMessages(1);
        OpHandler opHandler = this.mHandler;
        opHandler.sendMessage(opHandler.obtainMessage(1, i, 0));
    }

    /* access modifiers changed from: private */
    public void setDialogWidthH(int i) {
        FrameLayout frameLayout = (FrameLayout) getDialog().findViewById(R$id.volume_dialog_container);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.gravity = isLandscape() ? 21 : 19;
        layoutParams.width = i;
        frameLayout.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) getDialogView().getLayoutParams();
        layoutParams2.gravity = isLandscape() ? 21 : 19;
        getDialogView().setLayoutParams(layoutParams2);
        WindowManager.LayoutParams attributes = getDialog().getWindow().getAttributes();
        attributes.width = i;
        if (isLandscape()) {
            attributes.gravity = 21;
        } else {
            attributes.gravity = 19;
        }
        getDialog().getWindow().setAttributes(attributes);
    }

    private boolean isLandscape() {
        return this.mContext.getResources().getConfiguration().orientation == 2;
    }

    /* access modifiers changed from: protected */
    public void loadOpDimens() {
        this.mOpBeforeExpandWidth = (this.mContext.getResources().getDimensionPixelSize(R$dimen.oneplus_contorl_layout_margin_left1) * 2) + (this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_panel_transparent_padding) * 2) + this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_panel_width);
        int i = 3;
        try {
            if (((TelecomManager) this.mContext.getSystemService("telecom")).isInCall()) {
                i = 4;
            }
        } catch (Exception e) {
            Log.d("OpVolumeDialogImpl", "Get status of inCall status fail " + e.toString());
        }
        this.mOpafterExpandWidth = (this.mContext.getResources().getDimensionPixelSize(R$dimen.oneplus_contorl_layout_margin_left1) * 2) + (this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_panel_transparent_padding) * 2) + (i * this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_panel_width));
    }

    /* access modifiers changed from: protected */
    public void updateODIRelatedLayout() {
        int i;
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_icon_size) + (this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_odi_captions_margin) * 2);
        int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_settings_container_height);
        int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_row_margin_bottom);
        int dimensionPixelSize4 = this.mContext.getResources().getDimensionPixelSize(R$dimen.oneplus_contorl_layout_margin_left1);
        if (isLandscape()) {
            int i2 = this.mContext.getResources().getConfiguration().smallestScreenWidthDp;
            int dimensionPixelSize5 = this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_odi_captions_margin_land);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getDialogView().getLayoutParams();
            if (i2 <= 345) {
                i = (int) (((double) dimensionPixelSize5) * 0.5d);
                double d = (double) dimensionPixelSize4;
                getDialogView().setPadding(dimensionPixelSize4, (int) (0.3d * d), (int) (d * 1.5d), dimensionPixelSize4);
            } else if (i2 <= 411) {
                i = (int) (((double) dimensionPixelSize5) * 0.8d);
                double d2 = (double) dimensionPixelSize4;
                getDialogView().setPadding(dimensionPixelSize4, (int) (0.8d * d2), (int) (d2 * 1.5d), dimensionPixelSize4);
            } else {
                getDialogView().setPadding(dimensionPixelSize4, dimensionPixelSize4, (int) (((double) dimensionPixelSize4) * 1.5d), dimensionPixelSize4);
                dimensionPixelSize3 = dimensionPixelSize5;
                dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_icon_size) + (dimensionPixelSize3 * 2);
                dimensionPixelSize2 = dimensionPixelSize;
            }
            dimensionPixelSize3 = i;
            dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.op_volume_dialog_icon_size) + (dimensionPixelSize3 * 2);
            dimensionPixelSize2 = dimensionPixelSize;
        }
        if (this.mODICaptionsView != null) {
            ViewGroup viewGroup = this.mDialogUpper;
            if (viewGroup != null) {
                viewGroup.measure(0, 0);
                int measuredWidth = this.mDialogUpper.getMeasuredWidth();
                if (dimensionPixelSize > measuredWidth) {
                    Log.d("OpVolumeDialogImpl", "mODICaptionsView odiHeight:" + dimensionPixelSize + " max:" + measuredWidth);
                    dimensionPixelSize = measuredWidth;
                }
                this.mODICaptionsView.getLayoutParams().height = dimensionPixelSize;
                this.mDialogLower.invalidate();
            }
        }
        if (getSettingsView() != null) {
            getSettingsView().getLayoutParams().height = dimensionPixelSize2;
        }
        if (getSettingsBackView() != null) {
            getSettingsBackView().getLayoutParams().height = dimensionPixelSize2;
        }
        if (getSettingsOpSettingsView() != null) {
            getSettingsOpSettingsView().getLayoutParams().height = dimensionPixelSize2;
        }
        int size = this.mRows.size();
        for (int i3 = 0; i3 < size; i3++) {
            ImageButton imageButton = this.mRows.get(i3).icon;
            if (imageButton != null) {
                ((ViewGroup.MarginLayoutParams) imageButton.getLayoutParams()).bottomMargin = dimensionPixelSize3;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void showOutputChooserH() {
        synchronized (this.mOutputChooserLock) {
            if (this.mOutputChooserDialog == null) {
                if (this.mOutputChooserDialog == null) {
                    generateOutputChooserH();
                }
                this.mOutputChooserDialog.show();
                this.mOutputChooserDialog.setTheme(this.mThemeColorMode);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void generateOutputChooserH() {
        Context context = this.mContext;
        this.mOutputChooserDialog = new OpOutputChooserDialog(context, new MediaRouterWrapper(MediaRouter.getInstance(context))) {
            /* access modifiers changed from: protected */
            public void cleanUp() {
                synchronized (OpVolumeDialogImpl.this.mOutputChooserLock) {
                    OpVolumeDialogImpl.this.mOutputChooserDialog = null;
                }
            }
        };
    }

    /* access modifiers changed from: protected */
    public void setExpandFeautureDismissState() {
        this.mOpLastforceExpandState = false;
        this.mOpForceExpandState = false;
        setOpOutputChooserVisible(false, true);
        initSettingsH();
        OpOutputChooser opOutputChooser = this.mOpOutputChooser;
        if (opOutputChooser != null) {
            this.mFirstTimeInitDialog = true;
            opOutputChooser.removeCallback();
            this.mOpOutputChooser.destory();
            this.mOpOutputChooser = null;
        }
    }

    private boolean isAccentColorChanged(int i, boolean z) {
        int color = ThemeColorUtils.getColor(ThemeColorUtils.QS_ACCENT);
        if (this.mAccentColor == color) {
            return false;
        }
        this.mAccentColor = color;
        return true;
    }

    /* access modifiers changed from: protected */
    public void applyColorTheme(boolean z) {
        int themeColor = OpUtils.getThemeColor(this.mContext);
        boolean isAccentColorChanged = isAccentColorChanged(themeColor, z);
        if (this.mThemeColorMode != themeColor || isAccentColorChanged || z) {
            this.mThemeColorMode = themeColor;
            if (themeColor == 0) {
                applyWhiteTheme();
            } else if (themeColor == 1) {
                applyBlackTheme();
            } else if (themeColor != 2) {
                applyWhiteTheme();
            } else {
                applyAndroidTheme();
            }
            applyColors();
        }
    }

    /* access modifiers changed from: protected */
    public void applyWhiteTheme() {
        Resources resources = this.mContext.getResources();
        this.mThemeColorDialogBackground = R$drawable.volume_dialog_bg_light;
        this.mThemeColorDialogRowContainerBackground = R$drawable.volume_dialog_row_container_bg_light;
        this.mThemeColorText = resources.getColor(R$color.oneplus_contorl_text_color_secondary_light);
        this.mThemeColorIcon = resources.getColor(R$color.op_volume_dialog_row_icon_color_light);
        this.mThemeColorSeekbarBackgroundDrawable = R$drawable.volume_dialog_progress_light;
        this.mThemeButtonBg = resources.getColor(R$color.op_btn_volume_media_icon_bg_light);
    }

    /* access modifiers changed from: protected */
    public void applyBlackTheme() {
        Resources resources = this.mContext.getResources();
        this.mThemeColorDialogBackground = R$drawable.volume_dialog_bg_dark;
        this.mThemeColorDialogRowContainerBackground = R$drawable.volume_dialog_row_container_bg_dark;
        this.mThemeColorText = resources.getColor(R$color.oneplus_contorl_text_color_secondary_dark);
        this.mThemeColorIcon = resources.getColor(R$color.op_volume_dialog_row_icon_color_dark);
        this.mThemeColorSeekbarBackgroundDrawable = R$drawable.volume_dialog_progress_dark;
        this.mThemeButtonBg = resources.getColor(R$color.op_btn_volume_media_icon_bg_dark);
    }

    /* access modifiers changed from: protected */
    public void applyAndroidTheme() {
        Resources resources = this.mContext.getResources();
        this.mThemeColorDialogBackground = R$drawable.volume_dialog_bg_light;
        this.mThemeColorDialogRowContainerBackground = R$drawable.volume_dialog_row_container_bg_light;
        this.mThemeColorText = resources.getColor(R$color.oneplus_contorl_text_color_secondary_light);
        this.mThemeColorIcon = resources.getColor(R$color.op_volume_dialog_row_icon_color_light);
        this.mThemeColorSeekbarBackgroundDrawable = R$drawable.volume_dialog_progress_light;
        this.mThemeButtonBg = resources.getColor(R$color.op_btn_volume_media_icon_bg_light);
    }

    private void applyColors() {
        this.mDialogUpper.setBackgroundDrawable(getCornerGradientDrawable(this.mThemeColorDialogBackground));
        this.mDialogLower.setBackgroundDrawable(getCornerGradientDrawable(this.mThemeColorDialogBackground));
        this.mDialogRowContainer.setBackgroundDrawable(getCornerGradientDrawable(this.mThemeColorDialogRowContainerBackground));
        this.mODICaptionsView.setBackgroundDrawable(getCornerGradientDrawable(this.mThemeColorDialogRowContainerBackground));
        getSettingsIcon().setColorFilter(this.mThemeColorIcon);
        getSettingsBackIcon().setColorFilter(this.mThemeColorIcon);
        getSettingsOpSettingsIcon().setColorFilter(this.mThemeColorIcon);
        getODICaptionsIcon().setColorFilter(this.mThemeColorIcon);
        this.mConnectedDevice.setTextColor(this.mThemeColorText);
        this.mOutputChooser.setColorFilter(this.mAccentColor);
        this.mOutputChooser.setBackgroundTintList(ColorStateList.valueOf(this.mThemeButtonBg));
        this.mOutputChooserBackgroundView.setBackgroundTintList(ColorStateList.valueOf(this.mThemeButtonBg));
        this.mOutputChooserExpandIcon.setColorFilter(this.mThemeColorIcon);
        this.mOutputChooserExpandEditText.setTextColor(this.mThemeColorIcon);
        this.mOutputChooserExpandTextView.setTextColor(this.mThemeColorIcon);
        for (VolumeRow updateVolumeRowTintH : this.mRows) {
            updateVolumeRowTintH(updateVolumeRowTintH, true, true);
        }
    }

    private GradientDrawable getCornerGradientDrawable(int i) {
        GradientDrawable gradientDrawable = (GradientDrawable) ((LayerDrawable) this.mContext.getResources().getDrawable(i)).getDrawable(0);
        gradientDrawable.setCornerRadii(getVolCornerRadii(this.mContext));
        return gradientDrawable;
    }

    private float[] getVolCornerRadii(Context context) {
        float dimensionPixelSize = (float) context.getResources().getDimensionPixelSize(R$dimen.shape_corner_radius);
        if (OpUtils.DEBUG_ONEPLUS) {
            Log.i("OpVolumeDialogImpl", "shape_corner_radius:" + dimensionPixelSize);
        }
        return new float[]{dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize};
    }

    private ViewGroup getDialogView() {
        return (ViewGroup) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mDialogView");
    }

    /* access modifiers changed from: protected */
    public boolean isStatusBarShowing() {
        if ((OpLsState.getInstance().getPhoneStatusBar() != null ? OpLsState.getInstance().getPhoneStatusBar().getStatusBarWindowState() : 0) == 0) {
            return true;
        }
        Log.d("OpVolumeDialogImpl", "adjust to 1500");
        return false;
    }

    private VolumeDialogImpl.CustomDialog getDialog() {
        return (VolumeDialogImpl.CustomDialog) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mDialog");
    }

    private void updateVolumeRowTintH(VolumeRow volumeRow, boolean z, boolean z2) {
        Class cls = Boolean.TYPE;
        OpReflectionUtils.methodInvokeWithArgs(this, OpReflectionUtils.getMethodWithParams(VolumeDialogImpl.class, "updateVolumeRowTintH", VolumeRow.class, cls, cls), volumeRow, Boolean.valueOf(z), Boolean.valueOf(z2));
    }

    private ImageButton getSettingsIcon() {
        return (ImageButton) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mSettingsIcon");
    }

    private ImageButton getSettingsBackIcon() {
        return (ImageButton) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mSettingsBackIcon");
    }

    private ImageButton getSettingsOpSettingsIcon() {
        return (ImageButton) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mSettingsOpSettingsIcon");
    }

    private View getSettingsView() {
        return (View) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mSettingsView");
    }

    private View getSettingsBackView() {
        return (View) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mSettingsBackView");
    }

    private View getSettingsOpSettingsView() {
        return (View) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mSettingsOpSettingsView");
    }

    private ImageButton getODICaptionsIcon() {
        return (ImageButton) OpReflectionUtils.getValue(VolumeDialogImpl.class, this, "mODICaptionsIcon");
    }

    private void initSettingsH() {
        OpReflectionUtils.methodInvokeVoid(VolumeDialogImpl.class, this, "initSettingsH", new Object[0]);
    }
}