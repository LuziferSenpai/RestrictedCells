---
navigation:
  title: Restricted Cells
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

# Restricted Cells

&nbsp;

<Row>
  <ItemImage id="restrictedcells:restricted_item_cell_1k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_4k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_16k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_64k" scale="4" />

  <ItemImage id="restrictedcells:restricted_item_cell_256k" scale="4" />
</Row>

&nbsp;

Restricted Cells are [Storage Cells](ae2:items-blocks-machines/storage_cells.md) with adjustable limits. They go in
<ItemLink id="ae2:drive" />s, <ItemLink id="ae2:chest" />s and <ItemLink id="ae2:io_port" />s, and they accept the same
[upgrade cards](ae2:items-blocks-machines/upgrade_cards.md) as normal cells.

Without limits, a restricted cell works like its normal counterpart. You can set two limits:

*   **Max resources** is the most items or fluid a cell stores in total.
*   **Max types** is the most different items or fluids a cell holds.

A limit of 0 means no limit. Limits keep one resource from filling a whole cell and let you split a network's storage
between cells.

Set the limits in the <ItemLink id="ae2:cell_workbench" />. See [Restricting a Cell](restricting-a-cell.md).

&nbsp;

## Crafting

Combine any storage cell with a <ItemLink id="ae2:logic_processor" /> in a crafting grid to get its Restricted version. The new cell keeps the contents, partition and upgrades of the old one. This works for portable cells too, and the new portable cell keeps its stored energy.

To undo it, sneak and right-click with a Restricted Cell in your hand. You get the original cell back with its contents, and the Logic Processor. The limits are removed.

A Restricted Portable Cell follows the portable cell rules instead: you can only take it apart when it is empty.

<RecipeFor id="restrictedcells:restricted_item_cell_1k" />
