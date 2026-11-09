---
navigation:
  title: Bytes and Limits
  parent: restrictedcells-index.md
  icon: restrictedcells:restricted_item_cell_256k
  position: 35
---

# Bytes and Limits

A cell stores everything in bytes. [Bytes and Types](ae2:ae2-mechanics/bytes-and-types.md) explains the basics.
Restrictions use the same numbers.

&nbsp;

## What Uses Bytes

*   Each allowed type reserves bytes up front, even when the cell holds none of it. The restrictions screen shows this as
    **Bytes per type**.
*   Each resource uses one bit. An item cell stores 8 items per byte. A fluid cell stores 8,000 mB (8 buckets) per byte,
    and you enter fluid amounts in millibuckets.

&nbsp;

## Maximum Amount

The **Max** line on the restrictions screen is the highest amount you can enter:

**Max** = (**Total bytes** − **Bytes per type** × allowed types) × **Resources per byte**

The allowed types are your **Types** limit. When the limit is 0, every type the cell supports is allowed.

A 256k ME Item Storage Cell has 262,144 total bytes and reserves 2,048 bytes per type. With **Types** set to 10:

*   The 10 types reserve 20,480 bytes.
*   The remaining 241,664 bytes hold 1,933,312 items, so **Max** is 1,933,312.

With **Resource amount** set to 5,000, the cell uses 625 bytes for resources. **Allocated bytes** shows 21,105 and
**Free bytes** shows 241,039.

&nbsp;

## With an Equal Distribution Card

An <ItemLink id="ae2:equal_distribution_card" /> splits **Max** between the allowed types and rounds up. In the example
above, each of the 10 types can hold up to 193,332 items.

The screen counts **Resource amount** once per type. With 5,000 per type, the 10 types hold 50,000 items and use 6,250
bytes for resources.

In a partitioned cell, the partitioned types count as the allowed types.
