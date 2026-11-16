---
name: Nocturne Rhythm
colors:
  surface: '#121318'
  surface-dim: '#121318'
  surface-bright: '#38393e'
  surface-container-lowest: '#0d0e13'
  surface-container-low: '#1a1b20'
  surface-container: '#1e1f25'
  surface-container-high: '#292a2f'
  surface-container-highest: '#34343a'
  on-surface: '#e3e1e9'
  on-surface-variant: '#c6c5d2'
  inverse-surface: '#e3e1e9'
  inverse-on-surface: '#2f3036'
  outline: '#8f909b'
  outline-variant: '#454650'
  surface-tint: '#b8c4ff'
  primary: '#c9d1ff'
  on-primary: '#1a2b6a'
  primary-container: '#a5b4fc'
  on-primary-container: '#354484'
  inverse-primary: '#4b5a9c'
  secondary: '#ffc640'
  on-secondary: '#402d00'
  secondary-container: '#e3aa00'
  on-secondary-container: '#5a4100'
  tertiary: '#56edb0'
  on-tertiary: '#003825'
  tertiary-container: '#2fd096'
  on-tertiary-container: '#005439'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#dde1ff'
  primary-fixed-dim: '#b8c4ff'
  on-primary-fixed: '#001354'
  on-primary-fixed-variant: '#334282'
  secondary-fixed: '#ffdf9f'
  secondary-fixed-dim: '#f9bd22'
  on-secondary-fixed: '#261a00'
  on-secondary-fixed-variant: '#5c4300'
  tertiary-fixed: '#68fcbf'
  tertiary-fixed-dim: '#45dfa4'
  on-tertiary-fixed: '#002114'
  on-tertiary-fixed-variant: '#005137'
  background: '#121318'
  on-background: '#e3e1e9'
  surface-variant: '#34343a'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 44px
    fontWeight: '700'
    lineHeight: 52px
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 34px
    fontWeight: '700'
    lineHeight: 42px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 34px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Space Grotesk
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Space Grotesk
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
  label-sm:
    fontFamily: Space Grotesk
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-sm: 0.75rem
  margin: 1rem
  margin-tablet: 1.5rem
  margin-desktop: 2.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style
The design system establishes a focused, immersive productivity experience tailored for evening planning, deep routine tracking, and low-light daily organization. Designed specifically for modern Android environments (targeting Material You / M3 paradigms on Android 14 and 15), it strikes a careful balance between high-efficiency utility and calming sensory restraint.

The aesthetic fuses contemporary Material 3 tonal layering with subtle tactile discipline. Rather than relying on stark pitch-black interfaces or harsh pure whites, the system leverages deep chromatic slate and obsidian tones with calculated luminous accents. Vibrant violet anchors routine progression, amber fuels behavioral momentum and streaks, emerald rewards task completion, and muted rose flags critical interruptions. The interface feels intentional, calm, and deliberate—minimizing ocular strain during early morning check-ins and late-night debriefs.

## Colors
The palette is built strictly upon Material 3 dark surface container architecture, eliminating reliance on traditional drop shadows in favor of tonal surface luminosity.

### Surface System
- **Surface**: `#121318` (Default screen background)
- **Surface Dim**: `#0e0f14` (Recessed canvas, backdrop beneath sheets)
- **Surface Container Lowest**: `#0b0c10` (Deepest wells, sunken metric tracks)
- **Surface Container Low**: `#191a20` (Subtle grouping backgrounds, background cards)
- **Surface Container**: `#1d1e26` (Standard baseline card & item surface)
- **Surface Container High**: `#272933` (Elevated cards, active items, pickers)
- **Surface Container Highest**: `#323440` (Modals, popups, floating bottom navigation)

### Content & Outlines
- **On Surface**: `#e2e2ec` (High-emphasis text and icons)
- **On Surface Variant**: `#c5c6d3` (Medium-emphasis text, secondary attributes, timestamps)
- **Outline**: `#8e909d` (Active borders, focused states, prominent dividers)
- **Outline Variant**: `#444651` (Structural perimeter borders, card strokes, quiet dividers)

### Key Functional Accents
- **Primary & Routine Engine**:
  - Primary Base: `#a5b4fc` (Primary actions, active tab indicators, key selections)
  - Primary Deep: `#6366f1` (Selected states, filled button pressed states)
  - Primary Container: `#3730a3` (Tonal badges, active chip backgrounds)
  - On Primary Container: `#e0e7ff` (Text/icons on primary container)
- **Streak & Warning Engine**:
  - Main: `#fbbf24`, Deep: `#f59e0b` (Active day streaks, freeze shields, high-priority routine badges)
- **Completion & Health Engine**:
  - Main: `#34d399`, Deep: `#10b981` (Completed checkboxes, streak milestones, target metrics)
- **Alert & Overdue Engine**:
  - Main: `#f87171`, Deep: `#ef4444` (Expired routines, destructive actions, overdue tags)

## Typography
The typographical hierarchy couples **Plus Jakarta Sans** for structural copy and headings with **Space Grotesk** for numerical metrics, streak markers, and technical labels. 

- **Plus Jakarta Sans**: Delivers geometric warmth with friendly, approachable terminals. It prevents dark screens from feeling sterile, remaining exceptionally readable even at low display brightness.
- **Space Grotesk**: Injected exclusively for time stamps, routine counter counts, completion ratios, tags, and progress indicators. Its monospaced-adjacent rhythmic proportion emphasizes data clarity.

All copy rendered in `body-sm` and below must use `#c5c6d3` (On Surface Variant) or brighter to maintain contrast ratios exceeding WCAG AAA standards against `#121318`.

## Layout & Spacing
The design system operates on an 8pt architectural grid system, complemented by a 4pt subgrid strictly for micro-alignments (such as icon-to-label spacing and status dot indicators).

### Canvas & Margin Specifications
- **Mobile (<600dp)**: Single fluid column. Lateral margins set to `margin` (`16px`). Internal card lists use `space-sm` (`8px`) vertical gaps for high density without visual crowding.
- **Tablet (600dp - 1023dp)**: Two-column fluid grid. Margin expands to `margin-tablet` (`24px`), with column `gutter` set to `16px`. Habit lists and routine execution sidebars sit side-by-side.
- **Large Tablet / Desktop Foldables (>=1024dp)**: Fixed maximum content rail width of 1120px centered on screen. Lateral margin scales to `margin-desktop` (`40px`).

### Component Spacing Rhythm
- Routine row cards utilize `space-md` (`16px`) internal horizontal padding and `space-sm` (`8px`) vertical padding to optimize thumb targets (minimum touch target: 48dp).
- Section grouping headers maintain `space-lg` (`24px`) top margin and `space-xs` (`4px`) bottom margin to anchor the typography directly above child cards.

## Elevation & Depth
Elevation eschews diffuse, heavy black drop shadows, which muddy dark interfaces. Instead, depth is structured through a hybrid of **Material 3 Tonal Stepping** and **Subtle Boundary Borders**.

### 1. Tonal Stepping
Higher-priority and interactive layers rise by utilizing brighter surface container tokens:
- **Ground Floor (Canvas)**: `Surface` (`#121318`).
- **Level 1 (Routine Cards, Routine Items)**: `Surface Container` (`#1d1e26`).
- **Level 2 (Active Reordering, Dragged Rows, Dropdowns)**: `Surface Container High` (`#272933`).
- **Level 3 (Modal Bottom Sheets, Time Pickers, SnackBar)**: `Surface Container Highest` (`#323440`).

### 2. Boundary Borders (Ghost Edges)
Every raised container or card must feature a 1px border rendered in `Outline Variant` (`#444651`) at 50% to 100% opacity. This maintains edge fidelity on OLED screens where adjacent dark surfaces might otherwise bleed together.

### 3. Ambient Glows
For active primary Floating Action Buttons (FABs) and completed routine achievements, apply a restrained ambient glow rather than a standard shadow:
- `box-shadow: 0px 8px 24px -4px rgba(99, 102, 241, 0.28);`

## Shapes
Following modern Android 14/15 guidelines, shapes are rounded, fluid, and tactile:

- **Standard Containers & Cards**: `rounded-lg` (`16px`) for standard routine list items, metric cards, and calendar view containers.
- **Prominent Surfaces & Sheets**: `rounded-xl` (`24px`) for persistent bottom sheets, quick-add dialogs, and hero streak summaries.
- **Controls & Micro-elements**: Fully circular (`rounded-full` or pill-shaped) for tag chips, filter buttons, checkboxes, and the primary action button.
- **Input Fields**: Uniformly shaped at `16px` border radius to match card corner curves harmoniously when stacked inside forms.

## Components

### Buttons
- **Primary Filled Button**: Height 48dp. Background `#a5b4fc`, text `#121318` (`label-lg`). Border-radius: pill (9999px). On press: scales subtly down to 0.98, surface shifts to `#6366f1`.
- **Secondary Tonal Button**: Height 48dp. Background `#3730a3`, text `#e0e7ff`. Border-radius: pill.
- **Outlined Button**: Height 48dp. Background transparent, border 1px solid `#444651`, text `#e2e2ec`. Focus/Hover: border changes to `#8e909d`.

### Cards & Routine Items
- **Standard Routine Card**: Surface `#1d1e26`, 1px border `#444651`, border-radius 16px. Internal layout:
  - Left: Custom animated completion trigger (28dp circular target).
  - Center: Routine title in `body-lg` (`#e2e2ec`), sub-details (scheduled time, repeat frequency) in `body-sm` (`#c5c6d3`).
  - Right: Streak indicator badge (`Space Grotesk`, `#fbbf24`) paired with fire icon.
- **Completed Routine State**: Card background shifts to `#191a20` (Surface Container Low), text gets strikethrough with `#8e909d`, and border dims to `#444651` at 30% opacity.

### Checkboxes & Completion Triggers
- Custom circular indicators (24dp) with 2dp border in `#8e909d`.
- **Checked State**: Immediate spring animation expanding to fill with Emerald `#34d399`, displaying a `#0b0c10` checkmark. Includes a subtle emerald ring glow pulse.

### Chips & Filters
- Background `#191a20`, border 1px solid `#444651`, text `#c5c6d3` (`label-md`). Height 32dp, pill radius.
- **Selected State**: Background `#3730a3`, border 1px solid `#a5b4fc`, text `#e0e7ff`.

### Text Inputs & Form Fields
- Container: Surface `#191a20`, border 1px solid `#444651`, radius 16px, padding horizontal 16px, height 56dp.
- Text: `#e2e2ec`, placeholder text: `#8e909d`.
- **Active Focused State**: 2px border `#a5b4fc`, background `#1d1e26`.

### Floating Action Button (FAB)
- Extended or 56dp square-rounded (16px radius) FAB.
- Background: `#a5b4fc`, icon/label: `#121318`. Ambient glow: `rgba(165, 180, 252, 0.24)`.

### Streak Counters & Metrics
- Dedicated micro-component containing an amber flame vector `#f59e0b` paired with numerical text `#fbbf24` in `Space Grotesk` (`label-lg`), encapsulated in a `#191a20` pill container bordered by `#f59e0b` at 30% opacity.