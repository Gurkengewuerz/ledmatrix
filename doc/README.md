# Websocket API

Damit der Tisch nur als "Display" dient, kann dieser einern Websocket Server starten. Dann funktionieren allerdings nicht mehr die Tisch-Eigen Funktionen wie Tetris oder das Webinterface.  
Dazu muss in der Konfiguration der Punkt "just_webserver" zu *true* geändert werden.

Der Websocket kann über einen beliebigen Client angespchen werden bspw.
- Android
- [Javascript](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_client_applications)
- [Java](https://apexapps.oracle.com/pls/apex/f?p=44785:112:0:::112:P112_CONTENT_ID:8735)
- [Python](https://pypi.python.org/pypi/websocket-client)

Der Websocket ist auf **0.0.0.0:8025** gebunden.

## Hinweise
- Die Pixel fangen bei 1/1 an und **nicht** bei 0/0. 
- "command" ist case insensitive

## Kommandos
### Informationen des Tisches holen
**Request**

    {
        "command": "get"
    }

**Response**  

    {
        "brightness": 1,
        "length": 10,
        "width": 20
    }

### Information über einen Pixel holen
**Request**  

    {
        "command": "getPixel",
        "x": 10,
        "y": 2
    }

**Response**  

    {
        "x": 10,
        "y": 2,
        "rgb": [
            51,153,255
        ]
    }

### Gesamte Helligkeit verändern
**Request**  

    {
        "command": "brightness"
        "brightness": 0.75
    }

**Response**  
None

### Alle Pixel deaktivieren / Auf [255,255,255] setzen
**Request**  

    {
        "command": "clear"
    }

**Response**  
None

### Update aller Pixel erzwingen
**Request**  

    {
        "command": "force_update"
    }
    
**Response**  
None

### Setzen eines Pixels
**Request**  

    {
        "command": "setSingle",
        "x": 1,
        "y": 1,
        "rgb": [
            0,153,153
        ]
    }
    
**Response**  
None

### Setzen mehrerer Pixel in verschiedenen Farben
**Request**  

    {
        "command": "setMultiple",
        {
            "x": 10,
            "y": 2,
            "rgb": [
                255,255,255
            ]    
        },
        {
            "x": 5,
            "y": 5,
            "rgb": [
                102,255,153
            ]    
        }
    }

**Response**  
None

### Setzen mehrerer Pixel in einer Farbe / Füllen
**Request**  

    {
        "command": "fill",
        "rgb": [
            51,51,255
        ]    
    }

**Response**  
None