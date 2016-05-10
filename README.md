# License #

This project is licensed under GNU AGPL v3.0.  See the `LICENSE` file for the complete text.

# Compiling and running #

**Note that this project requires JDK 1.8 or higher.**

This project uses Maven.  If you are using an IDE, you should add this repository to your IDE as a Maven project.  If
you are compiling from the command line, install Maven 3.x and run:

    mvn package

This will produce a JAR and a ZIP in the `package` folder.  The JAR can be double-clicked to run the program, and the
ZIP contains a simplified project structure to be turned in for grading.  The ZIP also includes the JAR, for
convenience.

If double-clicking the JAR doesn't run the program, the file associations on your computer are broken.  If you have
Maven, you can run:

    mvn exec

This will compile and run the project.  Otherwise, you can run Java directly:

    java -jar PaulBuonopane-proj5.jar

If `java` isn't in your `%PATH%` (Windows) or `$PATH` (POSIX, incl. Mac/Linux/BSD), you may need to determine the
location of your JDK.  For example, on Windows x64 with Oracle JDK 1.8.0u92 x64, the command would be:

    "C:\Program Files\Java\jdk1.8.0_92\bin\java.exe" -jar PaulBuonopane-proj5.jar