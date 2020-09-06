.class public Lcom/android/server/qeg;
.super Ljava/lang/Object;
.source ""

# interfaces
.implements Lcom/oneplus/android/server/power/IOpFastCharge;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/server/qeg$you;,
        Lcom/android/server/qeg$zta;
    }
.end annotation


# static fields
.field private static final BATTERY_PLUGGED_NONE:I = 0x0

.field private static final DEBUG:Z

.field private static final Qg:Z = false

.field private static final Rg:Ljava/lang/String; = "BatteryLed"

.field private static final Sg:Ljava/lang/String; = "/sys/class/power_supply/battery/fastchg_status"

.field private static final TAG:Ljava/lang/String; = "FastCharge"

.field private static final Tg:Ljava/lang/String; = "/proc/enhance_dash"

.field private static final Ug:Ljava/lang/String; = "sys/class/power_supply/usb/real_type"

.field private static Vg:Lcom/android/server/qeg$zta;

.field private static mContext:Landroid/content/Context;


# instance fields
.field private Kg:Z

.field private Lg:Z

.field private Mg:Z

.field private Ng:Z

.field private Og:Z

.field private Pg:Z

.field private mBatteryLevel:I

.field private mBatteryStatus:I

.field private mDefLowBatteryWarningLevel:I

.field private final mLock:Ljava/lang/Object;

.field private mPlugType:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    sget-boolean v0, Landroid/os/Build;->DEBUG_ONEPLUS:Z

    sput-boolean v0, Lcom/android/server/qeg;->DEBUG:Z

    return-void
.end method

.method public constructor <init>()V
    .locals 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    iput-object v0, p0, Lcom/android/server/qeg;->mLock:Ljava/lang/Object;

    return-void
.end method

.method static synthetic access$100()Lcom/android/server/qeg$zta;
    .locals 1

    sget-object v0, Lcom/android/server/qeg;->Vg:Lcom/android/server/qeg$zta;

    return-object v0
.end method

.method static synthetic access$500()Landroid/content/Context;
    .locals 1

    sget-object v0, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    return-object v0
.end method

.method static synthetic bio(Lcom/android/server/qeg;)I
    .locals 0

    iget p0, p0, Lcom/android/server/qeg;->mBatteryLevel:I

    return p0
.end method

.method static synthetic cno(Lcom/android/server/qeg;)V
    .locals 0

    invoke-direct {p0}, Lcom/android/server/qeg;->om()V

    return-void
.end method

.method static synthetic kth(Lcom/android/server/qeg;)I
    .locals 0

    iget p0, p0, Lcom/android/server/qeg;->mPlugType:I

    return p0
.end method

.method private mm()I
    .locals 6

    const-string p0, "getFastChargeType io close exception :"

    const-string v0, "FastCharge"

    new-instance v1, Ljava/io/File;

    const-string v2, "/proc/enhance_dash"

    invoke-direct {v1, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    const/4 v2, 0x0

    :try_start_0
    new-instance v3, Ljava/io/BufferedReader;

    new-instance v4, Ljava/io/FileReader;

    invoke-direct {v4, v1}, Ljava/io/FileReader;-><init>(Ljava/io/File;)V

    invoke-direct {v3, v4}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_2
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    :try_start_1
    invoke-virtual {v3}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v2
    :try_end_1
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    :try_start_2
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V
    :try_end_2
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_2

    :catch_0
    move-exception v1

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    :goto_0
    invoke-virtual {v3, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/io/IOException;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v3, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_2

    :catch_1
    move-exception v1

    goto :goto_1

    :catchall_0
    move-exception v1

    move-object v3, v2

    goto :goto_4

    :catch_2
    move-exception v1

    move-object v3, v2

    :goto_1
    :try_start_3
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "getFastChargeType io exception:"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/io/IOException;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    if-eqz v3, :cond_0

    :try_start_4
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V
    :try_end_4
    .catch Ljava/io/IOException; {:try_start_4 .. :try_end_4} :catch_3

    goto :goto_2

    :catch_3
    move-exception v1

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    goto :goto_0

    :cond_0
    :goto_2
    if-eqz v2, :cond_1

    const-string p0, ""

    invoke-virtual {p0, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p0

    if-nez p0, :cond_1

    :try_start_5
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object p0

    invoke-virtual {p0}, Ljava/lang/Integer;->intValue()I

    move-result p0
    :try_end_5
    .catch Ljava/lang/NumberFormatException; {:try_start_5 .. :try_end_5} :catch_4

    goto :goto_3

    :catch_4
    move-exception p0

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "getFastChargeType NumberFormatException:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p0}, Ljava/lang/NumberFormatException;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_1
    const/4 p0, 0x1

    :goto_3
    sget-boolean v1, Lcom/android/server/qeg;->DEBUG:Z

    if-eqz v1, :cond_2

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "FastChargeType: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Slog;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_2
    return p0

    :catchall_1
    move-exception v1

    :goto_4
    if-eqz v3, :cond_3

    :try_start_6
    invoke-virtual {v3}, Ljava/io/BufferedReader;->close()V
    :try_end_6
    .catch Ljava/io/IOException; {:try_start_6 .. :try_end_6} :catch_5

    goto :goto_5

    :catch_5
    move-exception v2

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/io/IOException;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v3, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_3
    :goto_5
    throw v1
.end method

.method private nm()Z
    .locals 6

    const-string p0, "Failure in reading charger type"

    const-string v0, "FastCharge"

    new-instance v1, Ljava/io/File;

    const-string v2, "/sys/class/power_supply/battery/fastchg_status"

    invoke-direct {v1, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    const/4 v2, 0x0

    :try_start_0
    new-instance v3, Ljava/io/FileReader;

    invoke-direct {v3, v1}, Ljava/io/FileReader;-><init>(Ljava/io/File;)V

    new-instance v1, Ljava/io/BufferedReader;

    invoke-direct {v1, v3}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V

    invoke-virtual {v1}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v4

    const-string v5, "1"

    invoke-virtual {v4, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_0

    const/4 v4, 0x1

    goto :goto_0

    :cond_0
    move v4, v2

    :goto_0
    invoke-virtual {v1}, Ljava/io/BufferedReader;->close()V

    invoke-virtual {v3}, Ljava/io/FileReader;->close()V
    :try_end_0
    .catch Ljava/io/FileNotFoundException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    move v2, v4

    goto :goto_1

    :catch_0
    move-exception v1

    invoke-static {v0, p0, v1}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    :goto_1
    return v2
.end method

.method private om()V
    .locals 4

    sget-object p0, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object p0

    sget-object v0, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v1, 0x10e0096

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v0

    const-string v1, "battery_light_low_color"

    invoke-static {p0, v1, v0}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v0

    sget-object v1, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    const v2, 0x10e0097

    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v1

    const-string v2, "battery_light_medium_color"

    invoke-static {p0, v2, v1}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v1

    sget-object v2, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    const v3, 0x10e0093

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v2

    const-string v3, "battery_light_full_color"

    invoke-static {p0, v3, v2}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result p0

    sget-object v2, Lcom/android/server/qeg;->Vg:Lcom/android/server/qeg$zta;

    invoke-virtual {v2, v0, v1, p0}, Lcom/android/server/qeg$zta;->tsu(III)V

    sget-object p0, Lcom/android/server/qeg;->Vg:Lcom/android/server/qeg$zta;

    invoke-virtual {p0}, Lcom/android/server/qeg$zta;->updateLightsLocked()V

    return-void
.end method

.method private pm()V
    .locals 2

    iget-boolean v0, p0, Lcom/android/server/qeg;->Ng:Z

    if-eqz v0, :cond_0

    invoke-direct {p0}, Lcom/android/server/qeg;->nm()Z

    move-result v0

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    :goto_0
    iput-boolean v0, p0, Lcom/android/server/qeg;->Mg:Z

    sget-boolean v0, Lcom/android/server/qeg;->DEBUG:Z

    if-eqz v0, :cond_1

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "FastChargeStatus: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-boolean p0, p0, Lcom/android/server/qeg;->Mg:Z

    invoke-virtual {v0, p0}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    const-string v0, "FastCharge"

    invoke-static {v0, p0}, Landroid/util/Slog;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_1
    return-void
.end method

.method static synthetic rtg(Lcom/android/server/qeg;)Z
    .locals 0

    iget-boolean p0, p0, Lcom/android/server/qeg;->Lg:Z

    return p0
.end method

.method static synthetic sis(Lcom/android/server/qeg;)I
    .locals 0

    iget p0, p0, Lcom/android/server/qeg;->mDefLowBatteryWarningLevel:I

    return p0
.end method

.method static synthetic ssp(Lcom/android/server/qeg;)Ljava/lang/Object;
    .locals 0

    iget-object p0, p0, Lcom/android/server/qeg;->mLock:Ljava/lang/Object;

    return-object p0
.end method

.method static synthetic tsu(Lcom/android/server/qeg;)Z
    .locals 0

    iget-boolean p0, p0, Lcom/android/server/qeg;->Mg:Z

    return p0
.end method

.method static synthetic you(Lcom/android/server/qeg;)I
    .locals 0

    iget p0, p0, Lcom/android/server/qeg;->mBatteryStatus:I

    return p0
.end method

.method static synthetic you(Lcom/android/server/qeg;Z)Z
    .locals 0

    iput-boolean p1, p0, Lcom/android/server/qeg;->Lg:Z

    return p1
.end method

.method static synthetic zta(Lcom/android/server/qeg;)Z
    .locals 0

    iget-boolean p0, p0, Lcom/android/server/qeg;->Kg:Z

    return p0
.end method

.method static synthetic zta(Lcom/android/server/qeg;Z)Z
    .locals 0

    iput-boolean p1, p0, Lcom/android/server/qeg;->Kg:Z

    return p1
.end method


# virtual methods
.method public addIntentExtra(Landroid/content/Intent;)V
    .locals 5

    iget-boolean v0, p0, Lcom/android/server/qeg;->Og:Z

    const/4 v1, 0x1

    if-ne v0, v1, :cond_0

    invoke-direct {p0}, Lcom/android/server/qeg;->mm()I

    move-result v0

    goto :goto_0

    :cond_0
    move v0, v1

    :goto_0
    iget-boolean v2, p0, Lcom/android/server/qeg;->Mg:Z

    const/4 v3, 0x0

    if-ne v2, v1, :cond_1

    goto :goto_1

    :cond_1
    move v0, v3

    :goto_1
    const/4 v2, -0x1

    const-string v4, "status"

    invoke-virtual {p1, v4, v2}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v2

    const/4 v4, 0x2

    if-eq v2, v4, :cond_3

    const/4 v4, 0x5

    if-ne v2, v4, :cond_2

    goto :goto_2

    :cond_2
    move v2, v3

    goto :goto_3

    :cond_3
    :goto_2
    move v2, v1

    :goto_3
    const-string v4, "sys/class/power_supply/usb/real_type"

    invoke-virtual {p0, v4}, Lcom/android/server/qeg;->zgw(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p0

    const-string v4, "USB_PD"

    invoke-virtual {p0, v4}, Ljava/lang/String;->equalsIgnoreCase(Ljava/lang/String;)Z

    move-result p0

    if-eqz p0, :cond_4

    if-eqz v2, :cond_4

    goto :goto_4

    :cond_4
    move v1, v3

    :goto_4
    if-eqz p1, :cond_5

    const-string p0, "fastcharge_status"

    invoke-virtual {p1, p0, v0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    const-string p0, "pd_charge"

    invoke-virtual {p1, p0, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;

    :cond_5
    return-void
.end method

.method public chargeVibration()V
    .locals 2

    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/android/server/les;

    invoke-direct {v1, p0}, Lcom/android/server/les;-><init>(Lcom/android/server/qeg;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method public getFastChargeStatus()Z
    .locals 0

    iget-boolean p0, p0, Lcom/android/server/qeg;->Mg:Z

    return p0
.end method

.method public getLastFastChargeStatus()Z
    .locals 0

    iget-boolean p0, p0, Lcom/android/server/qeg;->Pg:Z

    return p0
.end method

.method public init(Landroid/content/Context;)V
    .locals 3

    sget-boolean v0, Lcom/android/server/qeg;->DEBUG:Z

    if-eqz v0, :cond_0

    const-string v0, "FastCharge"

    const-string v1, "init"

    invoke-static {v0, v1}, Landroid/util/Slog;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_0
    new-instance v0, Ljava/io/File;

    const-string v1, "/sys/class/power_supply/battery/fastchg_status"

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    move-result v0

    const/4 v1, 0x1

    if-eqz v0, :cond_1

    iput-boolean v1, p0, Lcom/android/server/qeg;->Ng:Z

    :cond_1
    new-instance v0, Ljava/io/File;

    const-string v2, "/proc/enhance_dash"

    invoke-direct {v0, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    move-result v0

    if-eqz v0, :cond_2

    iput-boolean v1, p0, Lcom/android/server/qeg;->Og:Z

    :cond_2
    if-eqz p1, :cond_3

    sput-object p1, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    sget-object v0, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v1, 0x10e0071

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v0

    iput v0, p0, Lcom/android/server/qeg;->mDefLowBatteryWarningLevel:I

    new-instance v0, Lcom/android/server/qeg$zta;

    const-class v1, Lcom/android/server/lights/LightsManager;

    invoke-static {v1}, Lcom/android/server/LocalServices;->getService(Ljava/lang/Class;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/server/lights/LightsManager;

    invoke-direct {v0, p0, p1, v1}, Lcom/android/server/qeg$zta;-><init>(Lcom/android/server/qeg;Landroid/content/Context;Lcom/android/server/lights/LightsManager;)V

    sput-object v0, Lcom/android/server/qeg;->Vg:Lcom/android/server/qeg$zta;

    :cond_3
    return-void
.end method

.method public registerObserver(ILandroid/os/Handler;)V
    .locals 5

    const/4 v0, 0x0

    const/16 v1, 0x226

    if-ne p1, v1, :cond_0

    const/4 v1, 0x2

    new-array v1, v1, [Lcom/android/server/qeg$you;

    new-instance v2, Lcom/android/server/cgv;

    const-string v3, "battery_led_low_power"

    invoke-direct {v2, p0, p2, v3}, Lcom/android/server/cgv;-><init>(Lcom/android/server/qeg;Landroid/os/Handler;Ljava/lang/String;)V

    aput-object v2, v1, v0

    const/4 v2, 0x1

    new-instance v3, Lcom/android/server/vju;

    const-string v4, "battery_led_charging"

    invoke-direct {v3, p0, p2, v4}, Lcom/android/server/vju;-><init>(Lcom/android/server/qeg;Landroid/os/Handler;Ljava/lang/String;)V

    aput-object v3, v1, v2

    :cond_0
    const/16 v1, 0x3e8

    if-ne p1, v1, :cond_1

    iget-object p1, p0, Lcom/android/server/qeg;->mLock:Ljava/lang/Object;

    monitor-enter p1

    :try_start_0
    new-instance v1, Lcom/android/server/bud;

    invoke-direct {v1, p0, p2}, Lcom/android/server/bud;-><init>(Lcom/android/server/qeg;Landroid/os/Handler;)V

    sget-object p2, Lcom/android/server/qeg;->mContext:Landroid/content/Context;

    invoke-virtual {p2}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object p2

    const-string v2, "battery_light_low_color"

    invoke-static {v2}, Landroid/provider/Settings$System;->getUriFor(Ljava/lang/String;)Landroid/net/Uri;

    move-result-object v2

    const/4 v3, -0x1

    invoke-virtual {p2, v2, v0, v1, v3}, Landroid/content/ContentResolver;->registerContentObserver(Landroid/net/Uri;ZLandroid/database/ContentObserver;I)V

    const-string v2, "battery_light_medium_color"

    invoke-static {v2}, Landroid/provider/Settings$System;->getUriFor(Ljava/lang/String;)Landroid/net/Uri;

    move-result-object v2

    invoke-virtual {p2, v2, v0, v1, v3}, Landroid/content/ContentResolver;->registerContentObserver(Landroid/net/Uri;ZLandroid/database/ContentObserver;I)V

    const-string v2, "battery_light_full_color"

    invoke-static {v2}, Landroid/provider/Settings$System;->getUriFor(Ljava/lang/String;)Landroid/net/Uri;

    move-result-object v2

    invoke-virtual {p2, v2, v0, v1, v3}, Landroid/content/ContentResolver;->registerContentObserver(Landroid/net/Uri;ZLandroid/database/ContentObserver;I)V

    invoke-direct {p0}, Lcom/android/server/qeg;->om()V

    monitor-exit p1

    goto :goto_0

    :catchall_0
    move-exception p0

    monitor-exit p1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw p0

    :cond_1
    :goto_0
    return-void
.end method

.method public update(Landroid/hardware/health/V1_0/HealthInfo;)V
    .locals 1

    if-nez p1, :cond_0

    return-void

    :cond_0
    iget-boolean v0, p1, Landroid/hardware/health/V1_0/HealthInfo;->chargerAcOnline:Z

    if-eqz v0, :cond_1

    const/4 v0, 0x1

    :goto_0
    iput v0, p0, Lcom/android/server/qeg;->mPlugType:I

    goto :goto_1

    :cond_1
    iget-boolean v0, p1, Landroid/hardware/health/V1_0/HealthInfo;->chargerUsbOnline:Z

    if-eqz v0, :cond_2

    const/4 v0, 0x2

    goto :goto_0

    :cond_2
    iget-boolean v0, p1, Landroid/hardware/health/V1_0/HealthInfo;->chargerWirelessOnline:Z

    if-eqz v0, :cond_3

    const/4 v0, 0x4

    goto :goto_0

    :cond_3
    const/4 v0, 0x0

    goto :goto_0

    :goto_1
    iget v0, p1, Landroid/hardware/health/V1_0/HealthInfo;->batteryLevel:I

    iput v0, p0, Lcom/android/server/qeg;->mBatteryLevel:I

    iget p1, p1, Landroid/hardware/health/V1_0/HealthInfo;->batteryStatus:I

    iput p1, p0, Lcom/android/server/qeg;->mBatteryStatus:I

    invoke-direct {p0}, Lcom/android/server/qeg;->pm()V

    return-void
.end method

.method public updateLastFastChargeStatus()Z
    .locals 2

    iget-boolean v0, p0, Lcom/android/server/qeg;->Pg:Z

    iget-boolean v1, p0, Lcom/android/server/qeg;->Mg:Z

    if-eq v0, v1, :cond_0

    const/4 v0, 0x1

    iput-boolean v1, p0, Lcom/android/server/qeg;->Pg:Z

    goto :goto_0

    :cond_0
    const/4 v0, 0x0

    :goto_0
    return v0
.end method

.method public updateLightsLocked()Z
    .locals 0

    sget-object p0, Lcom/android/server/qeg;->Vg:Lcom/android/server/qeg$zta;

    if-eqz p0, :cond_0

    invoke-virtual {p0}, Lcom/android/server/qeg$zta;->updateLightsLocked()V

    const/4 p0, 0x1

    return p0

    :cond_0
    const/4 p0, 0x0

    return p0
.end method

.method public zgw(Ljava/lang/String;)Ljava/lang/String;
    .locals 6

    const-string p0, "BufferedReader close exception :"

    const-string v0, "FastCharge"

    new-instance v1, Ljava/io/File;

    invoke-direct {v1, p1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    const/4 p1, 0x0

    :try_start_0
    new-instance v2, Ljava/io/BufferedReader;

    new-instance v3, Ljava/io/FileReader;

    invoke-direct {v3, v1}, Ljava/io/FileReader;-><init>(Ljava/io/File;)V

    invoke-direct {v2, v3}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_2
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    :try_start_1
    invoke-virtual {v2}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v1

    if-eqz v1, :cond_0

    invoke-virtual {v1}, Ljava/lang/String;->length()I

    move-result v3

    if-lez v3, :cond_0

    move-object p1, v1

    :cond_0
    invoke-virtual {v2}, Ljava/io/BufferedReader;->close()V
    :try_end_1
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    :try_start_2
    invoke-virtual {v2}, Ljava/io/BufferedReader;->close()V
    :try_end_2
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_2

    :catch_0
    move-exception v1

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/io/IOException;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_2

    :catchall_0
    move-exception p1

    move-object v1, p1

    move-object p1, v2

    goto :goto_3

    :catch_1
    move-exception v1

    move-object v5, v2

    move-object v2, p1

    move-object p1, v5

    goto :goto_0

    :catchall_1
    move-exception v1

    goto :goto_3

    :catch_2
    move-exception v1

    move-object v2, p1

    :goto_0
    :try_start_3
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "readNodeValue IO exception:"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/io/IOException;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    if-eqz p1, :cond_1

    :try_start_4
    invoke-virtual {p1}, Ljava/io/BufferedReader;->close()V
    :try_end_4
    .catch Ljava/io/IOException; {:try_start_4 .. :try_end_4} :catch_3

    goto :goto_1

    :catch_3
    move-exception p1

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/io/IOException;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_1
    :goto_1
    move-object p1, v2

    :goto_2
    return-object p1

    :goto_3
    if-eqz p1, :cond_2

    :try_start_5
    invoke-virtual {p1}, Ljava/io/BufferedReader;->close()V
    :try_end_5
    .catch Ljava/io/IOException; {:try_start_5 .. :try_end_5} :catch_4

    goto :goto_4

    :catch_4
    move-exception p1

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {p1}, Ljava/io/IOException;->getMessage()Ljava/lang/String;

    move-result-object p0

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    invoke-static {v0, p0}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_2
    :goto_4
    throw v1
.end method
