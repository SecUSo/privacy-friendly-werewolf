# Privacy Friendly Werewolf

Privacy Friendly Werewolf is an open source Android implementation of the Werewolf card game. You can play it with your friends inside a distributed network so that everyone can use its own phone without the need of a game master. The is build to respect your privacy by only demanding as less permissions as possible and not including any tracking algorithms.

[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/packages/org.secuso.privacyfriendlycardgameone/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
      alt="Get it on Google Play"
      height="80">](https://play.google.com/store/apps/details?id=org.secuso.privacyfriendlycardgameone)

## Motivation

Nowadays there are many apps that require many or all permissions available in Android in order to be installed. However these apps do not always need all of the permissions they ask for. With this project we want to offer an app where the user can be sure that private information such as contacts, location, identity etc., are not being used by the application. This app is part of the Privacy Friendly Apps group develop by Technische Universität Darmstadt, Germany. More information can be found under https://secuso.org/pfa

## Building

For further development we recommend Android Studio (version 2.3.1 or higher).

1. Download the source code: `git clone https://github.com/SecUSo/privacy-friendly-werewolf.git`
2. Open Android Studio
3. Connect the mobile device to the computer
4. Setup a Run Configuration "Android Application" and select "app" under Module
5. Click run and select "Choose a running device" from the "Device Chooser" Dialog

### API Reference

Minimum SDK: 21
Target SDK: 25

### How to set up a new Game

Each participant needs to have Wifi activated, and keep it activated throughout the whole game. When starting a new game (host-player) or respectively connecting to a game (client-player), the player needs to state an in-game name and follow the given instructions. If something goes wrong, there should be a notification telling what went wrong and a suggestion what should be adjusted. After the notification the player is usually directed to the main menu, where he can apply the adjustments and try to start/connect to a game again.

#### Host

One of the players needs to act as the game host (although he participates in the game as everyone else). The host creates a lobby, by navigating to the main menu (or through the Navigation Drawer) and press "New Game". He also needs to have Wifi activated, or else he will get shown a notification to do so. If done so, his socket address is shown on the screen, which he now can tell to each other player who wants to participate. From now on the Host just waits till everyone is connected to the Lobby (shown as List of Players connected), and then start the game by pressing the "Start"-Button.

#### Clients

Each other player (aside from the host) acts as game clients (although in-game they have the same rights as the host - except for invoking a new round). When the host set up the lobby, and told his socket address to each participant, the client can connect to the lobby by entering the given socket address into the "ip-address" field, and pressing the "Connect"-Button. After a short while, the client gets notified if the connection was successfully established or not. He now just has to wait till the host starts the game and then follow the in-game instructions (same as for the host).

#### Warning

Due to the complexity of network communications in the game, be advised that repeated (and intentional ;)) false inputs, e.g. when trying to connect to a non-existent host, that becomes existent short after, may in some cases lead to unexpected results. So in case of doubt (like for example game says you are connected, but the host does not see you in his list), just go to the main menu and connect to
again. Also note that this should not happen in normal cases, but even so, now you know what to do.
Another thing: For the clients it is expected to stay in the game as long as the host does. Only the host may end the game (by pressing the X-Button in-game or pressing the hardware back-button).

## License

Privacy Friendly Werewolf is licensed under the GPLv3.
Copyright (C) 2016 Daniel Gölz, Tobias Kowalski and Florian Staubach

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

The icons used in the navigation drawer are licensed under the [CC BY 2.5](http://creativecommons.org/licenses/by/2.5/). In addition to them the app uses icons from [Google Design Material Icons](https://design.google.com/icons/index.html) licensed under Apache License Version 2.0. All other images (the logo of Privacy Friendly Apps, the SECUSO logo, the app logo, the app logo in the navigation drawer and the splash screen icon) copyright [Technische Universtität Darmstadt](www.tu-darmstadt.de) (2016). The logo used for the notifications is part of the main logo of Privacy Friendly Werewolf. All graphics used in the game are licensed under the [CC BY-NC-ND 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/) and made by Florian Staubach. 

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
