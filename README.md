# HW04 Sensors and Notifications
In this assignment, an Android application was developed that integrates device sensors with background notifications. The app requests notification permission at runtime on Android 13 and above and creates a high-importance notification channel. A background notification is scheduled using WorkManager with a 10-second delay, allowing the notification to appear even when the app is not in the foreground. The notification is interactable and opens the application when tapped. The Ambient Temperature sensor (Sensor.TYPE_AMBIENT_TEMPERATURE) is used to read real-time temperature data, which is displayed in the user interface and included in the notification text. The functionality was tested using the Android Emulator by modifying temperature values through the Virtual Sensors settings.

## Notification permission
Notification permission is requested at runtime on Android 13+ (API 33+) using an ActivityResultContracts.RequestPermission() launcher. Before requesting, the app checks the permission state with ContextCompat.checkSelfPermission() for POST_NOTIFICATIONS. If permission is not granted, the system permission dialog is shown.

## Triggering a notification while the app is not in foreground
A background notification is triggered using WorkManager. When the user presses the “Schedule notification in 10 seconds” button, the app enqueues a OneTimeWorkRequest with setInitialDelay(10, TimeUnit.SECONDS). After pressing the button, the app can be sent to the background (Home button), and the notification is still triggered after the delay.

## Making the notification interactive
The notification includes a PendingIntent that opens MainActivity when tapped (setContentIntent(pendingIntent)), and setAutoCancel(true) removes the notification from the shade after the tap.

## Sensor used and how it’s read
The app uses the Ambient Temperature sensor (Sensor.TYPE_AMBIENT_TEMPERATURE). A SensorEventListener reads temperature updates and stores the latest value in a Compose state variable. The listener is registered in onResume() and unregistered in onPause() to follow the activity lifecycle.

## What the sensor input is used for
The current temperature value is displayed on screen and also included in the notification text. In the emulator, the temperature is changed via Extended Controls-Virtual sensors- Additional sensors - Ambient temperature (°C). The notification text shows the temperature value captured at the moment the work request is scheduled.

## AI Usage Declaration
Generative AI tools were used to provide guidance and troubleshooting support during the development of this exercise, particularly for resolving emulator crashes and challenges related to image selection and persistence. All implementation decisions and final code were reviewed and written by the author.

## Author
**Mohosina Akhter**
