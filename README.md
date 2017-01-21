## Overview

### Privacy Friendly Werewolf

Privacy Friendly Werewolf is an open source Android implementation of the famous Werewolf card game. You can play it with your friends inside a distributed network so that everyone can use its own phone without the need of a game master. The is build to respect your privacy by only demanding as less permissions as possible and not including any tracking algorithms.


## Motivation

Nowadays there are many apps that require many or all permissions available in Android in order to be installed. However these apps do not always need all of the permissions they ask for. With this project we want to offer an app where the user can be sure that private information such as contacts, location, identity etc., are not being used by the application. This app is part of the Privacy Friendly Apps group develop by Technische Universität Darmstadt, Germany. More information can be found under https://secuso.org/pfa

## Download and more Information

The application is currently under development. It will be released starting April 2017.

###Building

The app can be installed by using IntelliJ or Android Studio (we recommend to use version 2.2.2 or higher).

1. Download the source code: $ git clone https://github.com/SecUSo/privacy-friendly-werewolf.git
2. Add the local.properties and the build.properties file to the root by removing the extension ".copy" from "local.properties.copy" and "build.gradle.copy"
3. Open the IDE of your choice
4. Connect the mobile device to the computer
5. Setup a Run Configuration "Android Application" and select "app" under Module
6. Click run and select "Choose a running device" from the "Device Chooser" Dialog

### API Reference

Mininum SDK: 17
Target SDK: 24

## License

Copyright 2017 by Daniel Gölz, Tobias Kowalski and Florian Staubach

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

The icons used in the nagivation drawer are licensed under the [CC BY 2.5] (http://creativecommons.org/licenses/by/2.5/). In addition to them the app uses icons from [Google Design Material Icons](https://design.google.com/icons/index.html) licensed under Apache License Version 2.0. All other images (the logo of Privacy Friendly Apps, the SECUSO logo, the header in the navigation drawer and the splash screen icon) copyright [Technische Universtität Darmstadt] (www.tu-darmstadt.de) (2016). The logo used for the notifications is part of the main logo of Privacy Friendly Werewolf.

## Contributors

App-Icon: <br/>
Florian Staubach<br/>

Github-Users: <br/>
Daniel Gölz<br/>
Tobias Kowalski<br/>
Florian Staubach<br/>
Christopher Beckmann<br/>
Karola Marky<br/>
Peter Mayer
