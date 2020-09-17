Test-and-set Lock uses an atomic value for
indicating that some thread has engaged the lock
and is executing its critical section (CS).

Each thread that wants to enter CS tries
to engage lock, and checks to see if it was
the one who managed to engage it (with an
atomic operation). This has no effect if
it was already engaged. If it managed to
engage it, it proceeds to its CS, otherwise
it just retries.

Once the thread is done with CS, it simply
disengages the lock.

As all thread repeatedly attempt to engage the
lock for themselves, it leads a storm on the
processor memory bus (since the atomic operation
ignores the cache). Since bus traffic is always
high, it makes it difficult for the lock holder
to disengage his lock (due to traffic). Also
this scheme does not provide first-come-first-
served fairness. Hence, this type of lock is
only suitable for educational purposes.

```java
1. When thread wants to access critical
   section, it tries to engage lock, for
   itself, with an atomic operation. This
   has no effect if it was already engaged.
   If it managed to engage it, it proceeds
   to its CS.
2. If not, it retries again.
```

```java
1. When a thread is done with its critical
   section, it simply sets the "locked" state
   to false.
```

See [TASLock.java] for code, [Main.java] for test, and [repl.it] for output.

[TASLock.java]: https://repl.it/@wolfram77/tas-lock#TASLock.java
[Main.java]: https://repl.it/@wolfram77/tas-lock#Main.java
[repl.it]: https://tas-lock.wolfram77.repl.run


### references

- [The Art of Multiprocessor Programming :: Maurice Herlihy, Nir Shavit](https://dl.acm.org/doi/book/10.5555/2385452)
