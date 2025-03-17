# Load testing
This document contains instructions on how to set up and run load testing for your HTTP server using Apache JMeter. The report presents the test results with various parameters (virtual/classic streams, proprietary parser/GSON).

## 1. Preliminary requirements
Before you start, make sure that you have the following components installed:

Java Development Kit (JDK):
* Version 23 or higher.
* You can download it from the official Oracle website.
Apache JMeter:
* Version 5.4 or higher.
* You can download it from the official Apache JMeter website.


## 2. Configuring the HTTP Server
### 2.1. Creating an executable jar
You need to open clone a project from this repository. After that, you need to perform the gradle task, which is described in the build.in the gradle file.
```bash
gradle fatJar
```

### 2.2. Starting the server
Start the server locally from the assembled jar.:
```bash
java -jar http-server-testing.jar
```

## 3. Configuring Apache JMeter
### 3.1. Installing JMeter
Download and unpack Apache JMeter.
Run JMeter from the bin folder of the installed JMeter:
```bash
./jmeter.sh  # Для Linux/Mac
jmeter.bat    # Для Windows
```
### 3.2. Creating a test plan
Add a Thread Group:
* Right-click on Test Plan → Add → Threads (Users) → Thread Group.
* Specify the number of threads (users), the number of requests, and the execution time.
  
Add HTTP Request Defaults:
* Right-click on Thread Group → Add → Config Element → HTTP Request Defaults.
* Set up the basic query parameters as shown in Picture 1 of [отчёта](Bogdan Report.pdf).
  
Add HTTP Request:
* Right-click on Thread Group → Add → Sampler → HTTP Request.
* Set up requests parameters as shown in Picture 2 and 3 of [отчёта](Bogdan Report.pdf) for Request 1 and Request 2:

Добавьте Listener:
* Right-click on Thread Group → Add → Listener → Graph Results.
* Add other Listeners, such as Summary Report or Aggregate Report, to analyze the results.

### 3.3. Running the test
Click the Start button in JMeter.

After completing the test, view the results in the selected Listeners.
