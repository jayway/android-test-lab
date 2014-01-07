Android Test Lab
================

TDD with Fizz-Buzz
------------------


Refactoring for Testability Lab
-------------------------------

The goal of this exercise is to write tests to validate the scheduling algorithm in the LoggerService class. The algorithm is as follows:

1. If the service was started by the AlarmManager and the database is empty: Exit.
2. If less than a day has passed since the first log entry was written to the database, schedule the service to be restarted 24 hours after the timestamp of that log entry, then exit.
3. If we are on wifi, send the log entries to the server, then exit.
4. Otherwise, wipe the database and exit.

Suggested steps for a solution:

* Create an Android Instrumentation Test project. Also create a dummy test to check that 1 + 1 = 2, to see that it works.
* Move the actual algorithm to a separate class (LoggerScheduler) that is easier to instantiate and test than a Service.
* Move all database dependencies to a helper class that can be instantiated and injected to the LoggerScheduler. This lets you create a fake version of the database functionality in your tests, to simulate different states. (See http://stackoverflow.com/a/130862/638902 for a short description of dependency injection.)
* Write a test to verify the first point in the algorithm above.
* Inject the Dispatcher and the PhoneInfo objects as well, to help with further state simulations.
* Write tests for the rest of the algorithm. Make sure that all exit points are covered.

