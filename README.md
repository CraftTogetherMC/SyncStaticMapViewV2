# SyncStaticMapView 1.21.11 rebuild

Neu aufgesetzt fuer Paper 1.21.11.

## Aenderungen
- Maven-Projekt fuer `io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT`
- Java 21
- alte versionsgebundene 1.17/1.18/1.19-Branches entfernt
- neue 1.21-Branch mit Reflection-basiertem Packet-Senden
- Tracking ueber `Entity#getTrackedBy()` statt eigener NMS-Tracker-Zugriffe
- keine Netty-Pipeline-Injection mehr
- Item-Handling ueber Bukkit/Paper-API statt harter CraftBukkit-Versionen

## Wichtige Einschraenkung
Das Projekt wurde hier als Quellprojekt neu aufgebaut, aber in dieser Umgebung konnte ich es nicht gegen die echten Paper-Artefakte kompilieren, weil Maven und der externe Repo-Zugriff im Container fehlen. Das heisst:
- Struktur und Portierung sind fertig
- die letzte Compile-/Runtime-Verifikation auf einem echten Paper-1.21.11-Server musst du lokal machen
- der heikelste Teil bleibt `Branch_21_Packet`, weil Mojang Packet-Signaturen sich zwischen Versionen aendern koennen

## Lokal bauen
```bash
mvn clean package
```

## Ziel
Die Portierung ist bewusst so gebaut, dass moeglichst wenig von `v1_XX_RX`-Imports abhaengt. Dadurch ist das Projekt deutlich wartbarer als die alte Version.
