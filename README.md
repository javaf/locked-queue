Locked Queue uses locks and conditions to block
when queue is empty, or it is full. Just as
locks are inherently vulnerable to deadlock,
Condition objects are inherently vulnerable to
lost wakeups in which one or more threads wait
forever without realizing that the condition
for which they are waiting has become true.

This queue signals "not empty" whenever an item
is added to the queue, and "not full" whenever
an item is removed from the queue. However,
consider an optimization, where you only signal
"not empty" if the queue was empty. Bang! Lost
wakeup is suddenly possible.

To see how that is possible, consider 2
consumers A & B and 2 producers C & D. When
queue is empty and both A & B have to remove(),
they are blocked until C or D can add(). If C
add()s, followed by D, only 1 "not empty"
condition would be active causing C to wakeup,
but not D.

Hence, one needs to be careful when working with
both locks and condition objects.

The functionality of this queue is similar to
BlockingQueue and does not suffer from the lost
wakeup problem.

> **Course**: [Concurrent Data Structures], Monsoon 2020\
> **Taught by**: Prof. Govindarajulu Regeti

[Concurrent Data Structures]: https://github.com/iiithf/concurrent-data-structures

```java
add():
1. Acquire lock before any action.
2. Wait for queue being not full.
3. Add item to queue.
4. Release the lock.
```

```java
remove():
1. Acquire lock before any action.
2. Wait for queue being not empty.
3. Remove item from queue.
4. Release the lock.
```

See [LockedQueue.java] for code, [Main.java] for test, and [repl.it] for output.

[LockedQueue.java]: https://repl.it/@wolfram77/locked-queue#LockedQueue.java
[Main.java]: https://repl.it/@wolfram77/locked-queue#Main.java
[repl.it]: https://locked-queue.wolfram77.repl.run


### references

- [The Art of Multiprocessor Programming :: Maurice Herlihy, Nir Shavit](https://dl.acm.org/doi/book/10.5555/2385452)

![](https://ga-beacon.deno.dev/G-G1E8HNDZYY:v51jklKGTLmC3LAZ4rJbIQ/github.com/javaf/locked-queue)
