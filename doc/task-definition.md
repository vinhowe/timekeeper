## Alarm properties

Alarms are disruptive, full-screen alerts that demand immediate action. They are triggered at a specified time and reference a task.

Three actions are available to the end user when an alarm launches, in order of recommendation:
1. Focus - Replace current operating task with referenced task
2. Snooze - Temporarily defer referenced task
3. Defer - Indefinitely defer referenced task



## Task properties

### Accumulability

Describes whether a task can have multiple instances pending simultaneously. If an unaccumulable task is imperative, it must be completed before its next scheduled instance or it will be marked as permanently failed.

Tasks are, by default, not accumulable.

##### Examples
* "Study grammar" is accumulable, because for schoolwork, a specific timed amount of work is distributed across task instances
* "Brush teeth" is _not_ accumulable, because it doesn't need to be repeated after it is done, even if it is skipped the day prior
* "Work on app" is _not_ accumulable, because it is time-prohibitive to queue 2 hour tasks, and makeup cannot be easily done without considering fatigue.
* "Practice piano" is accumulable, because it requires a specific timed amount of practice per week distributed across task instances.


### Evaluator

Defines a strategy employed in controlling completion.

#### Types:
* Dynamic (default) - User marks task as complete and time is used to estimate next instance duration, e.g., "Brush teeth" takes an average of 2 minutes before being marked as completed
* Duration - Completed after specified period of time, e.g., "Practice piano" completes 30 minutes after it starts
* Instant - Completed at a specified point in time, e.g., "Eat lunch" ends at 13:00

### Trigger

Defines a strategy employed in activation and focus requests.

#### Types:
* Sequential (default) - _Activates_ when previous task is completed. or deferred
* Instant - Requests focus at specified point in time.
* None - Direct reference is required to activate task.
