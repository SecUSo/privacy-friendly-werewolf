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

Minimum SDK: 21
Target SDK: 25

### How to set up a new Game

Each participant needs to have Wifi activated, and keep it activated throughout the whole game. When starting a new game (host-player) or respectively connecting to a game (client-player), the player needs to state an in-game name and follow the given instructions. If something goes wrong, there should be a notification telling what went wrong and a suggestion what should be adjusted. After the notification the player is usually directed to the main menu, where he can apply the adjustments and try to start/connect to a game again.

#### Host

One of the players needs to act as the game host (although he participates in the game as everyone else). The host creates a lobby, by navigating to the main menu (or through the Navigation Drawer) and press "New Game". He also needs to have Wifi activated, or else he will get shown a notification to do so. If done so, his socket adress is shown on the screen, which he now can tell to each other player who wants to participate. From now on the Host just waits till everyone is connected to the Lobby (shown as List of Players connected), and then start the game by pressing the "Start"-Button.

#### Clients

Each other player (aside from the host) acts as game clients (although in-game they have the same rights as the host - except for invoking a new round). When the host set up the lobby, and told his socket adress to each participant, the client can connect to the lobby by entering the given socket adress in to the "ip-address" field, and pressing the "Connect"-Button. After a short while, the client gets notified if the connection was successfully established or not. He now just has to wait till the host starts the game and then follow the in-game instructions (same as for the host).

#### Warning

Due to the complexity of network communications in the game, be advised that repeated (and intentional ;)) false inputs, e.g. when trying to connect to a non-existent host, that becomes existent short after, may in some cases lead to unexpected results. So in case of doubt (like for example game says you are connected, but the host does not see you in his list), just go to the main menu and connect to
again. Also note that this should not happen in normal cases, but even so, now you know what to do.
Another thing: For the clients it is expected to stay in the game as long as the host does. Only the host may end the game (by pressing the X-Button in-game or pressing the hardware back-button).


## License

Copyright 2017 by Daniel Gölz, Tobias Kowalski and Florian Staubach

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

The icons used in the nagivation drawer are licensed under the [CC BY 2.5] (http://creativecommons.org/licenses/by/2.5/). In addition to them the app uses icons from [Google Design Material Icons](https://design.google.com/icons/index.html) licensed under Apache License Version 2.0. All other images (the logo of Privacy Friendly Apps, the SECUSO logo, the header in the navigation drawer and the splash screen icon) copyright [Technische Universtität Darmstadt] (www.tu-darmstadt.de) (2016). The logo used for the notifications is part of the main logo of Privacy Friendly Werewolf.

## Contributors

App-Icon: <br/>
Markus Hau<br/>

Github-Users: <br/>
Daniel Gölz<br/>
Tobias Kowalski<br/>
Florian Staubach<br/>
Christopher Beckmann<br/>
Karola Marky<br/>
Peter Mayer
