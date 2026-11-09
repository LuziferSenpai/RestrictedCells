---
navigation:
  title: Bytes und Grenzen
  parent: restrictedcells-index.md
  icon: restrictedcells:restricted_item_cell_256k
  position: 35
---

# Bytes und Grenzen

Eine Zelle speichert alles in Bytes. [Bytes und Typen](ae2:ae2-mechanics/bytes-and-types.md) erklärt die Grundlagen. Einschränkungen nutzen dieselben Zahlen.

&nbsp;

## Was Bytes verbraucht

*   Jeder erlaubte Typ reserviert vorab Bytes, auch wenn die Zelle nichts davon enthält. Der Bildschirm zeigt das als **Bytes pro Typ**.
*   Jede Ressource belegt ein Bit. Eine Gegenstandszelle speichert 8 Gegenstände pro Byte. Eine Flüssigkeitszelle speichert 8.000 mB (8 Eimer) pro Byte, und du gibst Flüssigkeitsmengen in mB ein.

&nbsp;

## Maximale Menge

Die Zeile **Max.** im Bildschirm ist die höchste Menge, die du eingeben kannst:

**Max.** = (**Bytes gesamt** − **Bytes pro Typ** × erlaubte Typen) × **Ressourcen pro Byte**

Die erlaubten Typen sind deine **Typen**-Grenze. Bei 0 sind alle Typen erlaubt, die die Zelle unterstützt.

Eine 256k-ME-Speicherzelle hat 262.144 Bytes insgesamt und reserviert 2.048 Bytes pro Typ. Bei **Typen** 10 gilt:

*   Die 10 Typen reservieren 20.480 Bytes.
*   Die übrigen 241.664 Bytes fassen 1.933.312 Gegenstände, also ist **Max.** 1.933.312.

Bei einer **Ressourcenmenge** von 5.000 belegt die Zelle 625 Bytes für Ressourcen. **Belegte Bytes** zeigt 21.105 und **Freie Bytes** 241.039.

&nbsp;

## Mit einer Gleichverteilungskarte

Eine <ItemLink id="ae2:equal_distribution_card" /> teilt **Max.** auf die erlaubten Typen auf und rundet auf. Im Beispiel kann jeder der 10 Typen bis zu 193.332 Gegenstände fassen.

Der Bildschirm zählt **Ressourcenmenge** einmal pro Typ. Bei 5.000 pro Typ fassen die 10 Typen 50.000 Gegenstände und belegen 6.250 Bytes für Ressourcen.

In einer partitionierten Zelle gelten die partitionierten Typen als erlaubte Typen.
