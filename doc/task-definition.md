# Acri entities

## Task

A task is an abstract definition used in creating task instances.

### Properties:

#### Accumulability (Optional)

Describes whether a task can have multiple instances incomplete simultaneously. If an unaccumulable task is imperative, it must be completed before its next scheduled instance or it will be marked as permanently failed.

##### Default

Not accumulable

##### Examples:
* "Study grammar" is accumulable, because for schoolwork, a specific timed amount of work is distributed across task instances
* "Brush teeth" is _not_ accumulable, because it doesn't need to be repeated after it is done, even if it is skipped the day prior
* "Work on app" is _not_ accumulable, because it is time-prohibitive to queue 2 hour tasks, and makeup cannot be easily done without considering fatigue.
* "Practice piano" is accumulable, because it requires a specific timed amount of practice per week distributed across task instances.


#### Evaluator (optional)

References a strategy employed in controlling completion.

##### Default

Task is "dynamic" and must be evaluated by the end user. Dynamic task instances are measured  in order to generate averages used in estimating subsequent instances.

[See "Task Evaluator"](#Task Evaluator)

#### Trigger

Defines a strategy employed in activation and focus requests.

##### Types:
* None (default): Activates when previous tasks loses focus; Requests focus if externally invoked.
* Instant - Requests focus at specified point in time.

## Task Evaluator

Defines a strategy employed in controlling task completion.

### Implementations:
* Duration - Completed after specified period of time, e.g., "Practice piano" completes 30 minutes after it starts
* Instant - Completed at a specified point in time, e.g., "Eat lunch" ends at 13:00



## Routine

Task group with children. A routine is like a meta-task that provides attributes inherited from its children.

### Properties:

####

## Alarm

Alarms are disruptive, full-screen alerts that demand immediate action. They are triggered at a specified time and reference a task.

Three actions are available to the end user when an alarm launches, in order of recommendation:
1. Focus - Replace current operating task with referenced task
2. Snooze - Temporarily defer referenced task
3. Defer - Indefinitely defer referenced task
