# PRD: Customizable Todo App for Android

**Status:** MVP specification\
**Platform:** Android\
**Product type:** Personal task and routine manager\
**Primary language:** Indonesian-ready (all user-facing text should
support localization)

## 1. Product overview

A lightweight Android todo application for managing one-time tasks and
recurring routines. Users can create tasks with flexible schedules, mark
each scheduled occurrence as complete, and view upcoming or overdue
tasks. A configurable Android home-screen widget provides a quick view
of today's tasks and allows users to complete tasks without opening the
app.

The app should be **offline-first**. Task data is stored locally and the
core features must work without an internet connection. No account or
login is required for the MVP.

## 2. Goals

1.  Make it easy to create tasks that happen once or repeat on a
    flexible schedule.
2.  Distinguish a recurring task from each of its scheduled occurrences.
3.  Make daily task status visible at a glance.
4.  Let users complete tasks from the Android home-screen widget.
5.  Keep the app simple, responsive, and reliable without unnecessary
    infrastructure.

### Non-goals for the MVP

-   Cloud synchronization or multi-device collaboration.
-   Shared lists, comments, attachments, or team assignments.
-   Subtasks, project management, and complex dependencies.
-   Calendar-provider synchronization.
-   Social features, gamification, or AI-generated schedules.

## 3. Target users and primary use cases

The app is intended for an individual who wants to track both ordinary
to-dos and repeated routines.

Examples: - Submit a report once on a specific date. - Exercise every
Monday, Wednesday, and Friday. - Pay rent on the 1st of every month. -
Take an item to work every weekday. - Clean a room every 3 days. -
Complete a personal task during a defined active period.

## 4. Core concepts

The data model must distinguish these concepts:

-   **Task:** The reusable definition, title, notes, schedule, and other
    settings.
-   **Occurrence:** One scheduled instance of a task on a particular
    date (and optionally time).
-   **Completion:** The status of an occurrence, not a permanent status
    on the task itself.

For example, a task named "Exercise" scheduled every Monday and
Wednesday creates separate occurrences. Completing Monday's occurrence
must not complete Wednesday's occurrence.

## 5. Functional requirements

### 5.1 Task creation and editing

Users can: - Create, edit, archive, restore, and delete a task. - Enter
a required task title. - Add optional notes or a description. - Choose a
schedule type. - Set an optional start date and end date for a recurring
schedule. - Set an optional reminder time. - Choose a visual color or
icon (simple predefined choices). - See a preview of the next scheduled
dates before saving.

Validation: - A title cannot be empty. - End date cannot be earlier than
start date. - Schedule-specific fields must be valid before saving. -
Editing a task's schedule must not silently rewrite historical
completion records.

### 5.2 Schedule types

The MVP must support the following schedule options.

  -----------------------------------------------------------------------
  Schedule                User configuration      Example
  ----------------------- ----------------------- -----------------------
  Once / specific date    Select one calendar     Submit report on 20 Nov
                          date                    

  Daily                   Every day, optionally   Drink water every day
                          with an interval        

  Weekly                  Select one or more      Every Monday and
                          weekdays                Thursday

  Monthly                 Select a day of the     Every 5th day of the
                          month, or a relative    month
                          weekday rule if         
                          feasible                

  Selected dates          Select multiple         20 Nov, 25 Nov, 2 Dec
                          specific calendar dates 

  Every N days            Set an interval and the Every 3 days
                          first date              
  -----------------------------------------------------------------------

**Schedule behavior and edge cases**

-   **Once:** Creates one occurrence on the selected date.
-   **Daily:** Creates an occurrence every day within the active date
    range.
-   **Weekly:** Creates occurrences only on the selected weekdays.
-   **Monthly by date:** Creates an occurrence on the selected day
    number. If a month does not contain that day (for example, the
    31st), skip that month. Do not silently move it to another date.
-   **Selected dates:** Creates occurrences only for the dates
    explicitly selected by the user.
-   **Every N days:** The first selected date is the anchor; subsequent
    occurrences are calculated from that date.
-   **Active date range:** Start date is inclusive. End date is
    inclusive. If no end date is set, the schedule continues
    indefinitely.
-   Store schedule rules in a structured form rather than storing only a
    human-readable label.

For the MVP, "monthly by date" is required. "Second Monday of every
month" and similar ordinal weekday rules can be deferred unless
implementation effort is low.

### 5.3 Task lists and filtering

The main screen should provide:

-   **Today:** All occurrences due today, grouped by completion status.
-   **Upcoming:** Future occurrences, grouped by date.
-   **Overdue:** Incomplete occurrences whose due date is before today.
-   **All tasks:** Active task definitions, including their schedule
    summaries.
-   **Completed:** Completed occurrences, filterable by date range.

Users can: - Mark an occurrence complete or incomplete. - Filter by
status and schedule type. - Search task titles. - Open a task to view
its schedule and occurrence history.

A task with no occurrence today should not appear in the Today list.

### 5.4 Occurrence completion

-   Completion is stored against a specific occurrence.
-   Toggling an occurrence changes only that occurrence.
-   Completing one occurrence must not complete future occurrences.
-   A user can undo completion.
-   Deleting or archiving a task must preserve historical records
    according to the data-retention behavior in section 9.
-   If the user changes a recurring schedule, already completed
    historical occurrences remain unchanged. Future occurrences are
    generated using the updated schedule.

### 5.5 Android home-screen widget

The widget is a core feature, not a decorative extra.

**MVP widget behavior** - Display today's date. - Display incomplete
tasks due today. - Show a checkbox or equivalent completion control for
each displayed task. - Show a completed count, such as
`3 / 5 completed`. - Provide an "Open app" action. - Refresh when the
date changes, when task data changes, and after a widget interaction. -
If there are no tasks today, show a useful empty state such as "No tasks
for today". - If today's task list is longer than the widget can
display, show a limited number and provide an action to open the full
list.

**Widget customization** - Users can choose a widget size supported by
Android. - Support at least a compact widget and a larger list widget. -
Allow the user to choose whether completed tasks remain visible. - Allow
a simple light/dark or system-following appearance if supported by the
widget framework.

**Widget technical constraints** - Android widgets are rendered and
refreshed under operating-system constraints. Do not assume the widget
can update continuously. - Widget interactions should update local
storage and then request a widget refresh. - The widget must read from
the same local data source as the app. - Handle process death and device
restart without losing task data. - Do not depend on an always-running
background service.

### 5.6 Reminders (MVP optional, recommended)

If reminders are included in the first release: - Users can set a
reminder time for a task. - A recurring task reminder is scheduled for
each applicable occurrence. - Users can disable reminders per task. -
Notification permission must be requested at an appropriate point, not
immediately on first launch. - Handle Android notification permission
requirements and device reboot. - Explain that exact delivery time can
be affected by Android battery and alarm restrictions.

If reminder implementation threatens the core schedule and widget
features, defer reminders to the next release.

## 6. Main screens and navigation

### 6.1 Today screen

-   Date header and completion summary.
-   Incomplete tasks first, completed tasks below or in a collapsible
    section.
-   Quick action to add a task.
-   Empty state when there are no tasks.

### 6.2 Upcoming screen

-   Future occurrences grouped by date.
-   Simple date navigation.
-   Ability to complete an occurrence early if desired.

### 6.3 All tasks screen

-   List of active task definitions.
-   Each item displays title and a readable schedule summary, such as
    "Every Mon, Wed, Fri".
-   Actions to edit, archive, or delete.

### 6.4 Add/edit task screen

-   Title and optional notes.
-   Schedule selector.
-   Schedule-specific fields.
-   Active date range.
-   Optional reminder.
-   Color/icon selection.
-   Preview of upcoming occurrences.
-   Save and cancel actions.

### 6.5 Task detail screen

-   Task information and schedule.
-   Upcoming occurrences.
-   Completion history.
-   Edit, archive, and delete actions.

### 6.6 Settings screen

-   First day of week.
-   Theme: system, light, dark.
-   Default widget preferences where supported.
-   Data export/import (can be deferred).

## 7. Suggested user flow

1.  User opens the app and sees today's tasks.
2.  User taps Add Task.
3.  User enters a title and selects a schedule type.
4.  The form displays only the fields relevant to that schedule.
5.  User reviews the next occurrence dates and saves.
6.  The app creates or updates the task and its scheduled occurrences.
7.  The Today screen and home-screen widget refresh.
8.  User checks off an occurrence in either the app or widget.

## 8. Technical approach

A native Android implementation is recommended because reliable
home-screen widget support is a central requirement.

### Suggested stack

  -----------------------------------------------------------------------
  Layer                   Recommendation          Purpose
  ----------------------- ----------------------- -----------------------
  Language                Kotlin                  Native Android
                                                  development

  UI                      Jetpack Compose         App screens

  Widget                  Jetpack Glance          Android home-screen
                                                  widgets

  Local database          Room                    Persistent tasks and
                                                  occurrences

  Preferences             DataStore               Theme and user settings

  Background work         WorkManager             Non-exact maintenance
                                                  and refresh work

  Architecture            MVVM with Repository    Clear separation of UI
                                                  and data

  Dependency injection    Manual initially or     Keep the MVP simple
                          Hilt                    
  -----------------------------------------------------------------------

Avoid adding a backend, login system, or cloud database until
synchronization is an actual requirement.

### Suggested project structure

``` text
app/
  data/
    local/
      dao/
      database/
      entity/
    repository/
  domain/
    model/
    scheduler/
    usecase/
  ui/
    today/
    upcoming/
    tasks/
    taskform/
    taskdetail/
    settings/
    components/
  widget/
    TodayWidget.kt
    WidgetDataRepository.kt
  worker/
  navigation/
```

This is a starting point, not a commandment. Keep the structure small
while the app is small.

## 9. Data model

A practical initial schema:

### Task

  -----------------------------------------------------------------------
  Field                   Type                    Notes
  ----------------------- ----------------------- -----------------------
  id                      Long / UUID             Primary key

  title                   String                  Required

  notes                   String?                 Optional

  scheduleType            Enum/String             ONCE, DAILY, WEEKLY,
                                                  MONTHLY,
                                                  SELECTED_DATES,
                                                  INTERVAL

  scheduleRule            JSON or normalized      Structured schedule
                          fields                  configuration

  startDate               LocalDate               Inclusive

  endDate                 LocalDate?              Inclusive; null means
                                                  no end

  reminderTime            LocalTime?              Optional

  color                   String?                 Optional predefined
                                                  color key

  icon                    String?                 Optional predefined
                                                  icon key

  isArchived              Boolean                 Default false

  createdAt               Instant                 Creation timestamp

  updatedAt               Instant                 Last update timestamp
  -----------------------------------------------------------------------

### TaskOccurrence

  Field            Type          Notes
  ---------------- ------------- ----------------------
  id               Long / UUID   Primary key
  taskId           Long / UUID   Foreign key to Task
  occurrenceDate   LocalDate     Date the task is due
  status           Enum/String   PENDING or COMPLETED
  completedAt      Instant?      Null until completed
  createdAt        Instant       Record creation time

Add a unique constraint on `(taskId, occurrenceDate)` for date-based
tasks so the same task cannot create duplicate occurrences for one date.

### Schedule rule examples

``` json
{
  "type": "WEEKLY",
  "weekdays": ["MONDAY", "WEDNESDAY", "FRIDAY"]
}
```

``` json
{
  "type": "MONTHLY",
  "dayOfMonth": 5
}
```

``` json
{
  "type": "INTERVAL",
  "intervalDays": 3,
  "anchorDate": "2026-11-14"
}
```

``` json
{
  "type": "SELECTED_DATES",
  "dates": ["2026-11-20", "2026-11-25", "2026-12-02"]
}
```

Use ISO-8601 date/time representations. Keep the schedule rule
structured and validate it when loading or saving.

### Historical data policy

-   Archiving hides a task from active lists but keeps its occurrences
    and history.
-   Deleting should require confirmation. For the MVP, either
    soft-delete the task or delete it with its occurrences; choose one
    policy and apply it consistently. Soft-delete is safer for
    preserving history.
-   Editing a schedule must not change completed historical occurrences.
-   Rebuild only future, uncompleted occurrences affected by the
    schedule change.

## 10. Scheduling and occurrence generation

Do not create an unlimited number of future occurrence rows.

Recommended approach: 1. Store the task's schedule definition as the
source of truth. 2. Generate occurrences for a limited rolling horizon,
for example the next 60--90 days. 3. Ensure generation is idempotent
using the unique `(taskId, occurrenceDate)` constraint. 4. Generate or
reconcile occurrences when the app opens, when a task is saved, and
through suitable background work. 5. Keep historical occurrences so
completion history remains accurate. 6. Do not delete completed
occurrences during schedule reconciliation.

For the MVP, a simpler alternative is to calculate occurrences for the
visible date range when querying. Whichever approach is chosen, avoid
duplicate occurrences and preserve completion status.

## 11. Widget data and update strategy

-   Use Room as the source of truth.
-   Expose a small repository/API for the widget to retrieve today's
    occurrences.
-   When a checkbox is tapped in the widget, update the corresponding
    occurrence in Room.
-   Request a Glance widget update after the database write succeeds.
-   Refresh on app-side task creation, editing, archiving, deletion, and
    completion.
-   Use date-aware refresh logic so the widget does not keep showing
    yesterday's tasks after midnight. Android may delay background work,
    so refresh again when the app or widget is next used.
-   Test compact and expanded widget layouts on multiple launcher sizes.

## 12. Non-functional requirements

-   Core functionality works offline.
-   Task data persists across app restarts and device reboots.
-   Main task list loads quickly for a personal-scale dataset.
-   UI remains usable with hundreds or thousands of tasks/occurrences.
-   No duplicate occurrences are created after repeated app launches.
-   Dates are handled using the device's local calendar and time zone.
-   The app must handle daylight-saving/time-zone changes without
    corrupting date-based schedules.
-   Accessibility: readable text, sufficient contrast, and touch targets
    suitable for checkboxes and actions.
-   Avoid collecting personal data that is not needed by the app.

## 13. MVP acceptance criteria

The MVP is considered complete when all of the following work:

-   [ ] User can create, edit, archive, and delete a task.
-   [ ] User can create a one-time task for a selected date.
-   [ ] User can create a daily recurring task.
-   [ ] User can select multiple weekdays for a weekly task.
-   [ ] User can create a monthly task by day number.
-   [ ] User can select multiple specific dates.
-   [ ] User can create an every-N-days task.
-   [ ] User can set an active start date and optional end date.
-   [ ] Each due date has an independent completion status.
-   [ ] Completing one occurrence does not complete future occurrences.
-   [ ] Today, Upcoming, Overdue, and All Tasks views show the correct
    data.
-   [ ] The Android home-screen widget displays today's tasks.
-   [ ] User can complete and undo a task from the widget.
-   [ ] Widget refreshes after relevant changes and handles an empty
    task list.
-   [ ] Data remains available offline and after app restart.
-   [ ] Schedule edits do not erase completed historical occurrences.
-   [ ] Occurrence generation does not create duplicates.

## 14. Testing scenarios

At minimum, test these cases:

1.  Daily task created today appears today and on following dates.
2.  Weekly task selected for Monday and Friday appears only on those
    weekdays.
3.  Monthly task on the 31st skips months without a 31st.
4.  Selected-date task appears only on the chosen dates.
5.  Every-3-days task follows its anchor date consistently.
6.  End date is inclusive; no occurrence appears after it.
7.  Completing today's occurrence leaves tomorrow's occurrence pending.
8.  Reopening the app does not duplicate occurrences.
9.  Editing a schedule preserves completed history.
10. Archiving a task removes it from active lists but preserves history.
11. Widget completion updates the app's Today screen.
12. App completion updates the widget.
13. Widget handles no tasks, many tasks, and completed tasks.
14. Date rollover does not leave the widget permanently showing the
    previous day.
15. App works in airplane mode.

## 15. Delivery phases

### Phase 1: Foundation

-   Create Android project.
-   Set up Compose navigation and theme.
-   Set up Room entities, DAOs, and repositories.
-   Implement task creation and editing for one-time tasks.

### Phase 2: Recurrence

-   Implement schedule rule models and validation.
-   Implement occurrence calculation/generation.
-   Add daily, weekly, monthly, selected-date, and interval schedules.
-   Add Today, Upcoming, Overdue, and history views.

### Phase 3: Home-screen widget

-   Implement compact and expanded Glance widgets.
-   Display today's incomplete occurrences.
-   Add widget checkbox interactions.
-   Implement widget refresh after data changes.

### Phase 4: Quality and release

-   Add archive/delete behavior.
-   Test date boundaries and schedule edits.
-   Test widget behavior across launchers and Android versions.
-   Add accessibility and empty states.
-   Prepare release build and basic privacy information.

## 16. Decisions to keep explicit

Before implementation, confirm these product decisions:

-   Whether a task may have more than one occurrence on the same date.
    Recommended MVP answer: no.
-   Whether completed tasks remain visible in the widget. Recommended
    default: hide them, with a user setting to show them.
-   Whether a monthly task on a missing day is skipped or moved to the
    month's last day. Recommended: skip, and make the behavior clear in
    the UI.
-   Whether reminders are part of the first release. Recommended: defer
    if they slow down recurrence and widget delivery.
-   Whether deletion permanently removes history. Recommended: use
    archive by default and make permanent deletion explicit.

------------------------------------------------------------------------

**MVP priority:** Get recurrence and occurrence completion correct
first, then make the widget reliable. A pretty widget showing incorrect
tasks is just a tiny, attractive way to be wrong.
