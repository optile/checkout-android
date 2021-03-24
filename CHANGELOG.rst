Changelog - Android SDK
-----------------------

**Version checkout-android 0.70.0**

Version **0.70.0** is a major release whereby the package names have changed from com.payoneer.mrs.payment to com.payoneer.checkout.
This release also includes changing the repository from "android-sdk" to "checkout-android".

**Feature**

* [PCX-1421] - Change android-sdk package structure and example apps
* [PCX-1422] - Install new example apps for testing on Google PlayStore
* [PCX-1227] - Update RXJava/Android versions in the example apps
* [PCX-1399] - Update Android-SDK list request body for automated testing
* [PCX-1416] - Change package names
* [PCX-1430] - Rename repository to checkout-android

**Version android-sdk 0.60.0**

Version **0.60.0** is a major release, it changes the name optile to payoneer.
For example the package net.optile.payment is now changed to com.payoneer.mrs.payment.

**Bug**

* [PCX-1322] - Fix PayPal automated test using TESTPSP

**Feature**

* [PCX-1165] - Convert theming configuration in ExampleApp to switch
* [PCX-1189] - Generate and submit userAgent string
* [PCX-1209] - Minimise warnings in code / code cleanup
* [PCX-1214] - Remove deprecated classes in UI Tests
* [PCX-1261] - Add missing model classes to the Android-SDK
* [PCX-1263] - Provide browser parameters (browserData) within CHARGE/PRESET
* [PCX-1271] - Replace optile with payoneer in code
* [PCX-1275] - Implement changes in userAgent for unknown data
* [PCX-1277] - Support all AccountInputData in Android-SDK
* [PCX-1316] - PayPal redirect automated test
* [PCX-1326] - Update GitHub documentation
* [PCX-1331] - Rename Jira fixVersions of the android-sdk

**Version android-sdk 0.52.0**

**Bug**

* [PCX-1208] - Fix "remove duplicated resource warning" in android studio
* [PCX-1232] - Soft keyboard closes when entering credit card
* [PCX-1235] - Local title "Oops" is not shown in connection error popup dialogs
* [PCX-1236] - Return error to merchant when no payment methods are available
* [PCX-1238] - UI Tests do not wait correctly for confirm screen
  
**Version android-sdk 0.51.0**

**Bug**

* [PCX-1187] - Redirect is broken when compiling for Android 30

**Feature**

* [PCX-900] - Store method logos locally in Android SDK
* [PCX-956] - [Android-sdk] Add X button to text input fields to clear fields

**Version android-sdk 0.50.0**

Version **0.50.0** is a major release, it is mandatory to use the MOBILE_NATIVE integration type.

**Feature**

* [PCX-760] - Android SDK to use MOBILE_NATIVE integration
* [PCX-730] - Support POST redirects
* [PCX-960] - Respect only MOBILE_NATIVE LIST objects
* [PCX-961] - Use only localisation files provided in the list
* [PCX-962] - Create button label logic
* [PCX-1001] - Use JSON localisation files instead of properties 
* [PCX-1146] - Remove support for secure SSL connections
* [PCX-1147] - Verify MIT copyright statements
* [PCX-1148] - Remove ProGuard and investigate obfuscation
* [PCX-1183] - Prepare Android-SDK release 0.50.0 with mobile-native

**Version android-sdk 0.40.0**

Version **0.40.0** is a major release, changes to the payment result handling causes a breaking change.

**Bug**

* [PCX-983] - Two cards open at the same time
* [PCX-998] - TRY_OTHER_NETWORK causes the wrong card to be opened
* [PCX-999] - Focus first input field of a preselected payment method
* [PCX-1000] - Example Demo crashes when Summary page is shown but PresetAccount is missing

**Feature**

* [PCX-785] - Update form fields with new designs
* [PCX-786] - Update payment screens/dialogs with new designs
* [PCX-841] - Support single AccountRegistration in ListResult
* [PCX-879] - Make adjustments to CVV tooltip
* [PCX-899] - Default message for unknown interaction code/reason
* [PCX-913] - Adjust reaction to backend responses
* [PCX-916] - Remove custom Android-SDK theming and deprecated features
* [PCX-929] - Apply material styling to existing payment page
* [PCX-934] - Update styling section of documentation
* [PCX-935] - Update example apps with new styling/theming
* [PCX-937] - Change RESULT_CANCELED to RESULT_ERROR
* [PCX-957] - Network logo images in network cards
* [PCX-986] - Replace Android-SDK theming with material theming
* [PCX-991] - Disable method grouping configuration
* [PCX-1002] - Disable custom validation configuration
* [PCX-1006] - Move android-sdk packages optile/repo to optile/android on packagecloud
* [PCX-1019] - Meaningful onActivityResult - resultCodes in android-sdk
* [PCX-1025] - Remove duplication of Interaction + resultInfo from PaymentResult
* [PCX-1028] - Update custom theme colors
* [PCX-1079] - Rename example apps for the android-sdk
* [PCX-1083] - Prepare android-sdk release 0.40.0
* [PCX-1068] - Add theming illustration to documentation

**Version android-sdk-0.32.0**

* [PCX-767] - Apply changes to registration checkboxes/labels
* [PCX-768] - Show title and text for interactions
* [PCX-868] - Refer to new names of renamed keys
* [PCX-922] - Lint is disabled in build      

**Version android-sdk-0.31.1**

* [PCX-873] - Demo app and Android SDK crash when PresetAccount has no AccountMask

**Version android-sdk-0.31.0**

* [PCX-849] - Reset minor & patch version part when major is increased
* [PCX-721] - Support PayPal redirect flow
* [PCX-780] - Redo expiry date field
* [PCX-840] - Support all credit and debit cards in Android-SDK

**Version android-sdk-0.30.0**

Version **0.30.0** is a major release with a breaking change, the PaymentUI.RESULT_CODE_ERROR is removed.
When Interaction, PaymentError and OperationResult objects are set in the PaymentResult Object has changed as well,
please check the README.rst for more information.

* [PCX-500] - Avoid returning Internet errors to the merchant resulthandler
* [PCX-729] - POST redirects should not be followed
* [PCX-788] - Remove check for activate button label
* [PCX-287] - Run functional tests automatically for each release build
* [PCX-483] - [Android SDK] Optimize inflation of Widgets
* [PCX-484] - [Android SDK] Move Localization handling in own service
* [PCX-717] - Update images for CVV popup (Android)
* [PCX-720] - Reject LISTs with operationType other than CHARGE/PRESET
* [PCX-789] - Update documentation using resources subdomain for redirects

**Version android-sdk-0.21.4**

* [PCX-428] - Support 3DS1 flow on Android SDK
* [PCX-485] - Update external library versions used in the android SDK.

**Version android-sdk-0.21.3**

* [PCX-201] - Write Mastercard[credit cards] UI Test for Android SDK
* [PCX-429] - Account number digit-grouping on Android SDK
* [PCX-491] - Change the default error message to match Payment Page Widget

**Version android-sdk-0.21.2**

* [PCX-492] - Move PaymentTheme class to same level as other public UI classes, this is a breaking change if using the PaymentTheme class.

**Version android-sdk-0.20.1**

* [PCX-479] - Remove gradle properties dependency from build scripts

**Version android-sdk-0.20.0**

Version **0.20.0** is a major release with a breaking change switching from the discontinued AppCompat libraries to the new AndroidX material libraries.

* [PCX-452] - Replace support library in the Android-sdk with the new AndroidX library

**Version android-sdk-0.11.18**

* [PCX-388] - Bug, Button labels don't have a default fall-back
* [PCX-407] - Bug, paymentpage.properties loading fails
* [PCX-416] - Bug, Preselected card in ListResult does not open correct card
* [PCX-378] - Separate processing payment from payment page
* [PCX-379] - Create separate services for processing payments
* [PCX-420] - Write automated UI test to load the payment page and open the first payment card 
* [PCX-430] - Write automated UI test to validate payment with presetFirst:true
* [PCX-431] - Write automated UI test to validate payment with presetFirst:false

**Version android-sdk-0.11.17**

* [PCX-321] - Add JCB UNIONPAY DINERS and DISCOVER to default credit card grouping

**Version android-sdk-0.11.16**

* [PCX-288] - Support different test merchants for functional tests

**Version android-sdk-0.11.15**

* [PCX-289] - Launch and show payment page functional tests

**Version android-sdk-0.11.14**
      
* [PCX-259] - Rename Android SDK Example app names

**Version android-sdk-0.11.13**

* [PCX-182] - [Android SDK] Create demo app for Android SDK with summary page
* [PCX-191] - [Android SDK] Example app with summary page

**Version android-sdk-0.11.12**

* [PCX-231] - Fix automated UI tests for Android SDK

**Version android-sdk-0.11.11**

* [PCX-210] - Update model classes Redirect and Installment Plan
* [PCX-213] - Update Android versioning with GoCD
* [PCX-193] - Set Up Test Framework for Android Payment SDK Automated Testing
* [PCX-194] - Make LIST request for different environments
* [PCX-211] - Improve Android SDK Documentation

**Version android-sdk-0.11.5 - 0.11.10**

Versions created during development & testing of GoCD scripts for Android projects.  
These versions do not include changes / updates of the Android SDK.

**Version android-sdk-0.11.4**

* [PCX-173] - Make Android SDK publicly available

**Version android-sdk-0.11.3**

* [PCX-178] - Update build scripts for public hosting of Android SDK artifact

**Version android-sdk-0.11.2**

* [PCX-187] - Provide only the payment status when the page was closed
* [PCX-188] - Example app uses same platform as Android SDK (Compat, Androidx)
* [PCX-185] - Add List URL input field to example app

**Version android-sdk-0.11.1**

* [PCX-172] - Add Android annotations for validating resource parameters
* [PCX-175] - Fix horizontal mode tooltip crashes app
* [PCX-174] - Customize input fields of payment methods

**Version android-sdk-0.11.0**

* [PCX-33] - Add smart selection of credit card forms
* [PCX-34] - Client-side theming of PaymentPage
* [PCX-54] - Setup build environment for Android Payment SDK
* [PCX-56] - CHARGE timeout handling
* [PCX-59] - Apply official optile UI design
* [PCX-161] - Support PRESET Flow
* [PCX-162] - Display preset account
