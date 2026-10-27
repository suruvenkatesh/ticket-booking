# Interview notes

## Week 1: equals, hashCode, HashSet, records

### What is casting, and why check `instanceof` first?
Casting tells Java how to view an object: `(Seat) o` means "treat `o` as a Seat". It doesn't change the object itself, only the type of the variable pointing to it. If the object isn't really a `Seat`, the cast throws a `ClassCastException`, so we first check `o instanceof Seat`, which returns `true` or `false`. Bonus: `null instanceof Seat` is `false`, so `equals(null)` safely returns `false`.

### Why must you override `hashCode()` when you override `equals()`?
Because `HashSet` and `HashMap` call `hashCode()` first to choose which drawer (bucket) to look in, and only then call `equals()` on what's inside. If two equal objects have different hashCodes, they land in different drawers, `equals()` is never called, and the set stores both as duplicates. The rule (the equals/hashCode contract): **equal objects must have equal hashCodes.**

### How does a `HashSet` decide whether an element is a duplicate?
1. It calls `hashCode()` and turns the number into a drawer index.
2. In that drawer, it only looks at elements with exactly the same hashCode.
3. It calls `equals()` on those. If one returns `true`, it's a duplicate and `add()` returns `false`.

`hashCode()` only narrows the search; `equals()` makes the final decision.

### What's the difference between identity and value equality?
Identity means the same object in memory, checked with `a == b`. Value equality means two separate objects hold the same data, checked with an overridden `a.equals(b)`. The default `equals()` and `hashCode()` from `Object` only check identity. Example: `add(s); add(s);` gives size 1 even without overriding, because there is one object, but adding two `new Seat("A", 1)` gives size 2 unless `Seat` has value equality.

### Is the default `hashCode()` unique? If two hashCodes are equal, are the objects equal?
No to both.
- **Not unique.** The default `hashCode()` is a generated number (not the memory address in modern JVMs). Two objects collide only about 1 in 2 billion times, but among 200,000 objects there were 9 repeats (the birthday problem).
- **Equal hashCodes don't mean equal objects.** `new Seat("A", 32)` and `new Seat("B", 1)` both have hashCode 2047 on Java 21 but aren't equal. So do `"Aa"` and `"BB"` (both 2112).

The rule only goes one way: equal objects → equal hashCodes.

### Why is `HashSet.contains()` faster than `List.contains()`?
`HashSet.contains()` uses `hashCode()` to jump straight to one drawer and checks only the few elements in it: **O(1)** on average. `List.contains()` calls `equals()` on every element one by one: **O(n)**. With 10,000 seats, that's a handful of checks versus up to 10,000.

### Is `return 42;` a valid `hashCode()`? Is it a good one?
Valid: equal objects still get equal hashCodes, so the contract holds and the set still gives correct results. Bad: every object lands in the same drawer, so each lookup calls `equals()` on everything and the set becomes as slow as a list (O(n)). A good `hashCode()` spreads objects across many drawers, for example `Objects.hash(row, number)`.

### What does a record generate for you?
For `record Seat(String row, int number)`, Java generates `private final` fields, a constructor, accessors `row()` and `number()` (no `get` prefix), and `equals()`, `hashCode()` and `toString()` based on all the fields. So a record has value equality out of the box: two seats with the same row and number are equal.

## Week 1: Venue

### Why did we use a `Set` for the venue's seats, and not a `List`?
Two reasons. **No duplicates:** a hall can't have seat A1 twice, and a `Set` doesn't allow duplicates (a `List` does). **Fast to check:** `contains()` jumps straight to one drawer using `hashCode()`, so "does this venue have seat A1?" is O(1) on average, while a `List` checks every seat (O(n)).

### What does `Set.copyOf` do?
It makes a locked copy of the set. Two effects:
- **Own copy:** changing the original set later doesn't change the venue.
- **Locked:** nobody can add or remove seats through `venue.seats()`. Trying it throws `UnsupportedOperationException`.

This makes `Venue` fully immutable: without it, the record would be only shallowly immutable, because the field couldn't change but the set inside could. To "remodel" a venue, you create a new `Venue` instead of changing the old one.

### Why does `hasSeat` return `false` instead of throwing an error?
Because "do you have this seat?" is a normal yes/no question, and "no" is a normal answer, not a mistake. Exceptions are for wrong input, like a blank venue name. A method named `has...` or `is...` should just return `true` or `false`.
