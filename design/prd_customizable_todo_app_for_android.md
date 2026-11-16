# PRD: Customizable Todo App for Android (MVP)

**Platform:** Android (Material 3, Jetpack Compose, Glance Widget)  
**Product:** Personal Task & Routine Manager with Recurring Rules  
**Localization:** Indonesian & English ready  
**Offline-first:** Local Room database, zero cloud dependency required for MVP.

### Screen Architecture & Core Views:
1. **Hari Ini (Today):** Date header, quick stats/progress ring, incomplete tasks grouped by priority/routine, completed accordion, quick add FAB.
2. **Mendatang & Terlewat (Upcoming & Overdue):** Multi-day timeline, overdue banner with alert badges, calendar strip scrubber.
3. **Semua Tugas & Rutinitas (All Tasks / Routines):** Master task definitions with schedule rule badges (e.g. 'Setiap Sen, Rab, Jum', 'Tiap 3 hari'), filter pills, active/archived state.
4. **Buat / Edit Tugas (Task Form):** Title, notes, dynamic schedule builder (Sekali, Harian, Mingguan, Bulanan, Tiap N Hari, Tanggal Pilihan), color picker, icon picker, live occurrence preview dates.
5. **Detail Tugas & Riwayat (Task Detail & Occurrences History):** Schedule breakdown, upcoming occurrences calendar view, occurrence completion log.
6. **Android Home Screen Widget Preview:** 4x2 interactive glance widget mockup showcasing quick check-off, progress tally, and open-app intent.
