Functionality
=============

* Provide user with interface to control bot
* Bot can interact with patient
* Bot can check status of patient

Usability
=========

* Easily scriptable command-line interface for automation
* JAR that can be double-clicked to run on most platforms
* Support comments and blank lines, particularly for scripts
* Tell the user when an invalid command is entered
* In-program help
* Display help when program is launched

Reliability
===========

* Proper exception handling; recover from errors without exiting
* Avoid race conditions
* Make no assumptions about performance or consistency of threads

Performance
===========

* Asynchronous event-driven model with thread pool
* Avoid locking

Supportability
==============

* Will be extended to read scripts directly from files in future version
* Application is divided into modular components
* Designed to support many more components in the future
* Support for future commands that take arguments

Miscellaneous
=============

* Java 8 required
* Works best on a multi-core CPU
* Simulation, not actual bot/patient; user is real