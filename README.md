## LED Matrix (English)

This project was created during an internship at the [Hochschule Bochum](http://www.hochschule-bochum.de/).  
The task was to program a LED table on which you can play the retro game Tetris. 200 RGBs with the chip APA102 were
used.

## Installation

The installation script is a bit outdated, but most steps can be reproduced.
Thanks to the rewrite from Bluetooth to Websockets, most of the configuration is straight forward.
After the first start adjust the `settings.json`.

## Development Environment

For a more pleasant programming environment, the
plugin [Embedded Linux JVM used](https://plugins.jetbrains.com/plugin/7738-embedded-linux-jvm-debugger-raspberry-pi-beaglebone-black-intel-galileo-ii-and-several-other-iot-devices-).

## Websocket API

Documentation of the Websocket API can be found in the `/doc` folder.

## MQTT API

The MQTT interface has not been actively documented so far, but the code of `MqttClient.java` is quite easy to
understand.


---

## LED Matrix (German)

Dieses Projekt entstand während eines Praktikums an der [Hochschule Bochum](http://www.hochschule-bochum.de/).  
Aufgabe war es einen LED Tisch zu programmieren auf dem man das Retrospiel Tetris spielen kann. Genutzt worden sind 200
RGBs mit dem Chip APA102.

## Installation

Da Installationsscript ist etwas veraltet, jedoch lassen sich die meisten Schritte reproduzieren.
Dank dem Rewrite von Bluetooth auf Websockets, ist die meiste Konfiguration straight forward.
Nach dem ersten Start die `settings.json` anpassen.

## Development Umgebung

Für eine angenehmere Programmierumgebung wurde das
Plugin [Embedded Linux JVM genutzt](https://plugins.jetbrains.com/plugin/7738-embedded-linux-jvm-debugger-raspberry-pi-beaglebone-black-intel-galileo-ii-and-several-other-iot-devices-).

## Websocket API

Eine Dokumentation der Websocket API findet sich im Ordner `/doc`.

## MQTT API

Die MQTT Schnittstelle wurde bisher nicht aktiv dokumentiert, jedoch ist der Code des `MqttClient.java` recht gut zu
verstehen.



<p align="center">
    <img src="assets/ledtisch_einzelbild.png" /><br/>
    <img height="500px" src="assets/ledtisch_apa.jpg" />
</p>
