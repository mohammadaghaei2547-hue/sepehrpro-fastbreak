# SEPEHR FAST BREAK

مود کلاینت‌ساید Fabric برای **Minecraft 1.21.11** — دو بخش: موتور Fast Break و **پنل Performance**.

## بخش ۱: Fast Break

- شکستن فوق‌سریع بلوک با قدرت قابل تنظیم **x1 تا x100**
- منوی تنظیمات با کلید **G**: اسلایدر قدرت، پریست‌های x1/x10/x25/x100، روشن/خاموش
- وقتی سرور پیشرفت شکستن را reset کند، موتور بلافاصله حمله را از نو آغاز می‌کند
- ذخیره تنظیمات در `config/sepehr-fastbreak.json`

## بخش ۲: Performance Control Panel (کلید P)

قابلیت‌های واقعی و قابل اندازه‌گیری:

- **پایش زنده**: FPS، میانگین Frame Time، **1% Low**، بدترین فریم، مصرف Heap، بار پردازشی پروسه، تعداد Thread
- **شمارش Stutter**: فریم‌های بالای ۵۰ میلی‌ثانیه شمارش می‌شوند
- **پایش GC**: تعداد و مجموع زمان جمع‌آوری زباله + نمایش آخرین مکث GC
- **پروفایل‌های آماده** (اعمال واقعی گزینه‌های بازی): **Battery / Balanced / Max FPS** — رندر دیستنس، سیمولیشن دیستنس، ذرات، فاصله رندر موجودیت‌ها، سایه‌روشن (AO)، ابرها، VSync و حداکثر FPS
- **Auto-Optimizer**: اگر میانگین FPS زیر ۴۰ بیفتد، خودکار پروفایل Balanced اعمال می‌شود و اعلان می‌دهد
- **Diagnostics & Benchmark**: صفحه گزارش کامل برای مقایسه‌ی قبل/بعد (تست‌ها را در شرایط یکسان خودت انجام بده — هیچ عدد ساختگی‌ای توسط مود گزارش نمی‌شود)

### درباره‌ی GPU و VRAM

کلاینت جاوا اطلاعاتی از VRAM/GPU ارائه نمی‌دهد و هر ابزاری که بدون کتابخانه native چنین ادعایی کند، ساختگی است. برای پایش GPU از NVIDIA Overlay (Alt+R) استفاده کن. بازنویسی موتور رندر در سطح Sodium خارج از محدوده‌ی این پروژه است.

### سازگاری سرور

- ماژول‌های Performance فقط مانیتورینگ و تنظیمات کلاینت‌اند؛ **هیچ پاکتی دست‌کاری نمی‌شود**
- روی Vanilla / Paper / Spigot / Purpur / Fabric / Velocity کاملاً امن است
- هیچ قابلیت دور زدن Anti-Cheat (GrimAC/Vulcan/...) وجود ندارد و اضافه نخواهد شد
- موتور Fast Break فقط پیش‌بینی کلاینت است؛ سرور شکستن را با منطق خودش اعتبارسنجی می‌کند

## بیلد

JDK 25 + Gradle 9.8.2:

```bash
gradle build
```

خروجی: `build/libs/sepehr-fastbreak-2.0.0.jar` — به‌همراه **Fabric API** در پوشه `mods`.

نسخه‌های کامپایل‌شده در پوشه [`dist`](dist/) هم قرار می‌گیرند.

## ساختار پروژه

```
com.sepehr.fastbreak/
    SepehrFastBreak.java          ← نقطه ورود، کلیدها، HUD
    FastBreakEngine.java          ← موتور Fast Break
    FastBreakConfigScreen.java    ← منوی Fast Break
    FastBreakConfig.java          ← تنظیمات
    perf/
        FrameTimeTracker.java     ← اندازه‌گیری فریم و 1% Low
        SystemMonitor.java        ← Heap/CPU/GC/Thread
        PerfProfile.java          ← پروفایل‌های Battery/Balanced/MaxFPS
        PerfWatchdog.java         ← Auto-Optimizer
    gui/
        PerfControlScreen.java    ← پنل کنترل عملکرد
        PerfDiagnosticsScreen.java← عیب‌یابی و بنچمارک
```
