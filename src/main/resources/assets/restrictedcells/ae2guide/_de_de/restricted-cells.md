---
navigation:
  title: Eingeschränkte Zellen
  parent: restrictedcells-index.md
  icon: restrictedcells:restricted_item_cell_1k
  position: 10
item_ids:
  - restrictedcells:restricted_item_cell_1k
  - restrictedcells:restricted_item_cell_4k
  - restrictedcells:restricted_item_cell_16k
  - restrictedcells:restricted_item_cell_64k
  - restrictedcells:restricted_item_cell_256k
  - restrictedcells:restricted_fluid_cell_1k
  - restrictedcells:restricted_fluid_cell_4k
  - restrictedcells:restricted_fluid_cell_16k
  - restrictedcells:restricted_fluid_cell_64k
  - restrictedcells:restricted_fluid_cell_256k
  - restrictedcells:restricted_portable_item_cell_1k
  - restrictedcells:restricted_portable_item_cell_4k
  - restrictedcells:restricted_portable_item_cell_16k
  - restrictedcells:restricted_portable_item_cell_64k
  - restrictedcells:restricted_portable_item_cell_256k
  - restrictedcells:restricted_portable_fluid_cell_1k
  - restrictedcells:restricted_portable_fluid_cell_4k
  - restrictedcells:restricted_portable_fluid_cell_16k
  - restrictedcells:restricted_portable_fluid_cell_64k
  - restrictedcells:restricted_portable_fluid_cell_256k
---

# Eingeschränkte Zellen

&nbsp;

<Row>
  <ItemImage id="restrictedcells:restricted_item_cell_1k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_4k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_16k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_64k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_256k" scale="4" />
</Row>

&nbsp;

Eingeschränkte Zellen sind [Speicherzellen](ae2:items-blocks-machines/storage_cells.md) mit einstellbaren Grenzen. Sie passen in ein <ItemLink id="ae2:drive" />, eine <ItemLink id="ae2:chest" /> oder einen <ItemLink id="ae2:io_port" /> und nehmen dieselben [Upgradekarten](ae2:items-blocks-machines/upgrade_cards.md) an wie normale Zellen.

Ohne Grenzen verhält sich eine Eingeschränkte Zelle wie ihr normales Gegenstück. Du kannst zwei Grenzen setzen:

*   **Max. Ressourcen** ist die größte Menge an Gegenständen oder Flüssigkeit, die eine Zelle insgesamt speichert.
*   **Max. Typen** ist die größte Zahl verschiedener Gegenstände oder Flüssigkeiten in der Zelle.

Eine Grenze von 0 bedeutet keine Grenze. Grenzen verhindern, dass eine Ressource eine ganze Zelle füllt, und teilen den Speicher eines Netzwerks auf mehrere Zellen auf.

Die Grenzen stellst du in der <ItemLink id="ae2:cell_workbench" /> ein. Siehe [Zelle einschränken](restricting-a-cell.md).

&nbsp;

## Herstellung

Kombiniere eine beliebige Speicherzelle mit einem <ItemLink id="ae2:logic_processor" /> in einem Handwerksraster, um ihre Eingeschränkte Version zu erhalten. Die neue Zelle behält Inhalt, Partitionierung und Upgrades der alten. Das gilt auch für tragbare Zellen, und die neue tragbare Zelle behält ihre gespeicherte Energie.

Zum Rückgängigmachen schleichst du und klickst mit einer Eingeschränkten Zelle in der Hand mit der rechten Maustaste. Du erhältst die ursprüngliche Zelle mit ihrem Inhalt und den Logikprozessor zurück. Die Grenzen entfallen.

Eine Eingeschränkte tragbare Zelle folgt den Regeln für tragbare Zellen: Du kannst sie nur im leeren Zustand zerlegen.

<RecipeFor id="restrictedcells:restricted_item_cell_1k" />
