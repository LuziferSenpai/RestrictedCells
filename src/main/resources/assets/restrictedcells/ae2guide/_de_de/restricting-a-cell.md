---
navigation:
  title: Zelle einschränken
  parent: restrictedcells-index.md
  icon: ae2:cell_workbench
  position: 20
---

# Zelle einschränken

&nbsp;

<Row>
  <ItemImage id="ae2:cell_workbench" scale="4" />

  <ItemImage id="ae2:certus_quartz_wrench" scale="4" />
</Row>

&nbsp;

1.  Lege eine [Eingeschränkte Zelle](restricted-cells.md) in die <ItemLink id="ae2:cell_workbench" />.
2.  Klicke oben rechts auf den Schraubenschlüssel-Reiter. Er bleibt grau, bis der Slot eine Eingeschränkte Zelle enthält.
3.  Gib deine Grenzen ein. Sie gelten schon beim Tippen.

&nbsp;

![Die Speicherzellenwerkbank mit dem Schraubenschlüssel-Reiter oben rechts](workbench-wrench.png)

&nbsp;

## Einschränkungsbildschirm

&nbsp;

![Der Einschränkungsbildschirm](restrictions-screen.png)

&nbsp;

*   **Ressourcenmenge** ist die größte Menge an Ressourcen, die die Zelle speichert. Du kannst die Suffixe k, M, G und T eingeben, zum Beispiel `5k` oder `2,5M`, und einfache Rechnungen wie `2*1000`.
*   **Typen** ist die größte Zahl an Typen, die die Zelle aufnimmt.
*   Ein leeres Feld zählt als 0 und bedeutet keine Grenze.
*   **Zurücksetzen** löscht beide Grenzen.

Die Zeilen unter den Feldern zeigen die Kapazität der Zelle. **Max.** ist die höchste Menge, die du eingeben kannst. Jeder erlaubte Typ reserviert vorab seine Bytes, deshalb erhöht eine kleinere Typzahl **Max.** Der Bildschirm kürzt deine Eingabe auf das, was die Zelle fassen kann.

Ist eine <ItemLink id="ae2:equal_distribution_card" /> eingebaut, gilt **Ressourcenmenge** pro Typ. Siehe [Gleichverteilung](equal-distribution.md).
