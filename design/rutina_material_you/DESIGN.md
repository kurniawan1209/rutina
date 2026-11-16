---
name: Rutina Material You
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#464555'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#777587'
  outline-variant: '#c7c4d8'
  surface-tint: '#4d44e3'
  primary: '#3525cd'
  on-primary: '#ffffff'
  primary-container: '#4f46e5'
  on-primary-container: '#dad7ff'
  inverse-primary: '#c3c0ff'
  secondary: '#855300'
  on-secondary: '#ffffff'
  secondary-container: '#fea619'
  on-secondary-container: '#684000'
  tertiary: '#00505f'
  on-tertiary: '#ffffff'
  tertiary-container: '#006a7c'
  on-tertiary-container: '#93e8ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2dfff'
  primary-fixed-dim: '#c3c0ff'
  on-primary-fixed: '#0f0069'
  on-primary-fixed-variant: '#3323cc'
  secondary-fixed: '#ffddb8'
  secondary-fixed-dim: '#ffb95f'
  on-secondary-fixed: '#2a1700'
  on-secondary-fixed-variant: '#653e00'
  tertiary-fixed: '#acedff'
  tertiary-fixed-dim: '#4cd7f6'
  on-tertiary-fixed: '#001f26'
  on-tertiary-fixed-variant: '#004e5c'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-lg:
    fontFamily: Roboto Flex
    fontSize: 57px
    fontWeight: '400'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Roboto Flex
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Roboto Flex
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  headline-md:
    fontFamily: Roboto Flex
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  headline-md-mobile:
    fontFamily: Roboto Flex
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Roboto Flex
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  title-lg:
    fontFamily: Roboto Flex
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 26px
  title-md:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0.15px
  title-sm:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
  body-lg:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  body-sm:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.4px
  label-lg:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
  label-sm:
    fontFamily: Roboto Flex
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.5px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  margin: 1rem
  margin-tablet: 1.5rem
  margin-desktop: 2rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system embodies the expressive philosophy of Android Material 3 (Material You), tailored specifically for an offline-first task and recurring routine manager. It balances systematic precision with tactile, humanistic warmth, making disciplined habit tracking feel rewarding rather than burdensome.

### Visual Aesthetic & Philosophy
- **Style:** Material 3 Expressive. Deeply rooted in Google's Dynamic Color extraction paradigm, utilizing container tonal hierarchy, distinct outline variants, and fluid motion.
- **Atmosphere:** Focused, serene, and intentional. The interface minimizes cognitive friction while offering high tactile feedback through purposeful surface elevations, soft tonal tints, and pill geometries.
- **Offline-First Clarity:** Visual indicators clearly delineate synchronized state, local caching, and repeating cadence states (daily, lunar, custom cron-style intervals) without clutter.
- **Tone of Voice (Indonesian Localization):** Direct, friendly, respectful (*santun*), and concise (*lugas*). Uses clear microcopy such as "Simpan Perubahan", "Jadwal Rutin", "Selesai", and "Belum sinkron" to avoid cognitive fatigue.

## Colors

The palette is engineered to support Material 3 tonal palettes with dynamic state mappings. Light mode utilizes a soft, cool-slate foundation (`#F8FAFC`) to reduce glare, while dark mode leverages deep slate (`#0F172A`) over pitch-black for superior perception of depth and surface tinting.

### Role Mapping
- **Primary (`#4F46E5` / Deep Indigo):** Anchor actions, key floating buttons, primary selection states, and core progression meters.
  - Light Container: `#EEF2FF` with `#312E81` on-container text.
  - Dark Container: `#3730A3` with `#E0E7FF` on-container text.
- **Secondary (`#F59E0B` / Amber Accent):** Streak counters, recurring task alerts, priority tags, and habit completion milestones.
  - Container: Soft amber tints (`#FEF3C7`) signaling warmth and positive accomplishment.
- **Tertiary (`#06B6D4` / Cyan):** Category identifiers, secondary routine frequencies, and Glance widget auxiliary data.
- **Surfaces & Tiers:**
  - `surface`: `#F8FAFC` (Light) / `#0F172A` (Dark).
  - `surface-container-low`: `#F1F5F9` (Light) / `#1E293B` (Dark).
  - `surface-container-high`: `#E2E8F0` (Light) / `#334155` (Dark).
  - `outline`: `#CBD5E1` (Light) / `#475569` (Dark) with 1dp boundary definition.
  - `outline-variant`: `#E2E8F0` (Light) / `#1E293B` (Dark) for subtle dividers.

## Typography

Typography relies on **Roboto Flex** to emulate native Android modern system rendering (compatible with Google Sans aesthetics). It delivers structural legibility across variable weights, ensuring compact line bounds on dense routine listings.

### Typesetting & Hierarchy Guidelines
- **Display & Large Headlines:** Reserved for daily overview summaries, streak counters, and date headers (e.g., "Hari Ini", "12 Tugas Selesai").
- **Title Roles:** Defined for card headers, routine group titles, and modal sheets.
- **Body Text:** Calibrated line-heights prevent truncation in multi-line task descriptions and nested sub-task checklists.
- **Label Roles:** Applied strictly to pill chips, repeating frequency metadata (e.g., "Setiap Senin & Kamis"), status tags, and app bar actions.
- **Indonesian Language Metrics:** Indonesian text tends to require 10–15% more horizontal width than English. Keep task names single-line with end-ellipsizing where needed, providing full titles on detail bottom sheets.

## Layout & Spacing

The layout is built upon standard Material 3 adaptive responsive specifications utilizing an 8dp grid base with 4dp increments for dense UI components.

### Form Factor Behavior
- **Mobile (Phone, <600dp):** Single-column vertical stream. Margin is 16dp (`1rem`), gutters are 16dp. Bottom App Bar or Navigation Bar handles primary tabs. FAB rests pinned bottom-end with 16dp margins above the navigation bar.
- **Foldable & Tablet (600dp–840dp):** Two-pane layout. Navigation Rail on the start edge. Left pane displays lists and routine categories; right pane houses active task execution or rule creation. Margins expand to 24dp (`1.5rem`).
- **Desktop & Large Displays (>840dp):** Multi-pane workflow. Permanent navigation drawer with dynamic task detail and calendar view side-by-side. Outer margins set to 32dp (`2rem`).

### Component Spacing Rhythm
- Task item interior padding: `space-md` (16dp horizontal, 12dp vertical).
- Gap between grouped routine cards: `space-sm` (8dp).
- Gap between major sections (e.g., "Mendesak", "Rutin Pagi"): `space-xl` (32dp).

## Elevation & Depth

In alignment with Material 3 Expressive standards, depth is primarily communicated through **tonal color shifts** rather than dense drop shadows. Higher surfaces receive higher color luminescence and primary tinting.

### Elevation Levels
- **Level 0 (Base Canvas):** Pure `surface`. Flat, 0dp elevation.
- **Level 1 (Tonal Container Cards & Glance Widgets):** `surface-container-low` with subtle 1dp border (`outline-variant`). No ambient shadow, creating a contemporary crisp aesthetic.
- **Level 2 (Outlined / Elevated Cards):** `surface-container` with light diffused ambient shadow (`blur: 6dp`, `y: 2dp`, `rgba(15, 23, 42, 0.04)`).
- **Level 3 (Floating Action Button & Modals):** `surface-container-high` accompanied by dynamic tonal elevation and soft shadow (`blur: 12dp`, `y: 4dp`, `rgba(15, 23, 42, 0.08)`).
- **Level 4 (Modal Bottom Sheets & Navigation Bars):** Elevated over content with backdrop dimming (`scrim: rgba(15, 23, 42, 0.32)`).

### Glance Widget Elevation
- Android Glance home screen widgets apply an ultra-subtle backdrop tint (`surface-container-low` at 85% opacity with 20dp background blur on supported Android 12+ launchers) bordered by a 1dp `outline-variant` stroke.

## Shapes

The design system implements characteristic Material 3 expressive curvatures, emphasizing pill-shaped controls and generous rounded corners for content cards.

### Shape Class Specifications
- **Small (8px / `rounded-sm`):** Tooltips, small badging, and sub-checkboxes.
- **Medium (12px to 16px / `rounded-md` to `rounded-lg`):** Task list items, dialog containers, and text input fields.
- **Large (24px / `rounded-xl`):** Routine milestone summary cards, recurrence rule configuration panels.
- **Extra Large (28px / `rounded-2xl`):** Material 3 modal bottom sheets and expandable rule drawers.
- **Full / Pill (`rounded-full`):** Filter chips, frequency badges, Floating Action Buttons (FAB), and pill-style quick-action pills.

## Components

### 1. Buttons & FAB
- **Floating Action Button (M3):** Standard 56x56dp or Extended FAB with icon + label ("Tambah Tugas"). Pill-shaped (`rounded-full` or `rounded-2xl` 16dp curvature). Primary container fill (`#4F46E5`) with high-contrast text/icon.
- **Filled Button:** Used for primary submissions ("Simpan", "Mulai Rutinitas"). Full pill radius, 40dp height.
- **Tonal Button:** Secondary actions ("Atur Ulang", "Lewati"). Surface container tint with primary text color.
- **Outlined Button:** Neutral actions with a 1dp `outline` border and transparent background.

### 2. Routine & Task Cards
- **Tonal Container Card:** Default state for routine blocks. Background is `surface-container-low` with 16dp rounded corners. Holds completion checkbox, routine title, streak pill, and recurrence label.
- **Outlined Card:** Inactive or future routines. `outline-variant` border with transparent surface.
- **Interactive States:** Swiping start-to-end reveals an Amber/Green completion indicator ("Selesai"); swiping end-to-start reveals reschedule options ("Tunda").

### 3. Chips & Metadata Indicators
- **Filter Chips:** Pill-shaped, 32dp height. Selected chips use `secondary-container` (`#FEF3C7`) with Amber text for routine filters (e.g., "Harian", "Mingguan", "Fokus Kerja").
- **Recurrence Rule Chip:** Compact label chip indicating rule frequency (e.g., "Ulangi: Setiap 2 hari"). Features a leading calendar/refresh glyph.

### 4. Selection Controls
- **M3 Checkbox:** 20x20dp with 4dp rounded corners. Unchecked: 2dp `outline` border. Checked: Filled with primary `#4F46E5` displaying a smooth morphing SVG checkmark.
- **Switch:** Standard M3 switch with oversized thumb for routine toggle activation.

### 5. Input Fields
- **Filled Text Field:** 56dp height with bottom active indicator line and top 12dp rounded corners. Localized placeholders (e.g., "Nama tugas atau kebiasaan...").
- **Outlined Text Field:** Used inside routine builder bottom sheets for recurrence interval inputs ("Ulangi setiap...", "Berakhir pada...").

### 6. Glance App Widget
- **Home Screen Routine Snapshot:** 16dp/24dp corner radii. Surface backdrop with semi-transparent frosted styling (`#F8FAFC` at 90% in light, `#0F172A` at 90% in dark). Displays quick-check list with offline-safe local action triggers.