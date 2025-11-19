# Vocabulary Learning App

แอปพลิเคชันเรียนคำศัพท์ภาษาอังกฤษบน Android

## เทคโนโลยีที่ใช้

- **Kotlin** - ภาษาโปรแกรมหลัก
- **Jetpack Compose** - UI Framework แบบ Declarative
- **Hilt** - Dependency Injection
- **Material 3** - Design System
- **Navigation Compose** - การจัดการ Navigation
- **ViewModel** - State Management

## โครงสร้างโปรเจค

```
app/src/main/java/com/vocabulary/
├── VocabularyApplication.kt    # Application class พร้อม Hilt setup
├── MainActivity.kt              # Main Activity พร้อม Compose
└── ui/
    └── theme/                   # Theme configuration
        ├── Color.kt
        ├── Type.kt
        └── Theme.kt
```

## การ Build โปรเจค

```bash
./gradlew build
```

## การรันแอป

1. เปิดโปรเจคด้วย Android Studio
2. รอให้ Gradle Sync เสร็จ
3. เชื่อมต่ออุปกรณ์ Android หรือเปิด Emulator
4. กด Run

## ความต้องการระบบ

- Android SDK 24 (Android 7.0) ขึ้นไป
- Kotlin 2.0.21
- Gradle 8.13
- Java 17
