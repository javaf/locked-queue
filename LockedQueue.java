import java.util.*;
import java.util.concurrent.locks.*;

// Locked Queue uses locks and conditions to block
// when queue is empty, or it is full. Just as
// locks are inherently vulnerable to deadlock,
// Condition objects are inherently vulnerable to
// lost wakeups in which one or more threads wait
// forever without realizing that the condition
// for which they are waiting has become true.
// 
// This queue signals "not empty" whenever an item
// is added to the queue, and "not full" whenever
// an item is removed from the queue. However,
// consider an optimization, where you only signal
// "not empty" if the queue was empty. Bang! Lost
// wakeup is suddenly possible.
// 
// To see how that is possible, consider 2
// consumers A & B and 2 producers C & D. When
// queue is empty and both A & B have to remove(),
// they are blocked until C or D can add(). If C
// add()s, followed by D, only 1 "not empty"
// condition would be active causing C to wakeup,
// but not D.
// 
// Hence, one needs to be careful when working with
// both locks and condition objects.
// 
// The functionality of this queue is similar to
// BlockingQueue and does not suffer from the lost
// wakeup problem.

class LockedQueue<T> extends AbstractQueue<T> {
  final Lock lock = new ReentrantLock();
  final Condition notFull = lock.newCondition();
  final Condition notEmpty = lock.newCondition();
  int head, tail, size;
  final T[] items;
  // lock: central (coarse) lock for queue
  // notFull: condition indicating queue not full
  // notEmpty: condition indicating queue not empty
  // head: items are removed from here
  // tail: iterms are added to here
  // size: number of items in queue
  // items: space for items in queue

  @SuppressWarnings("unchecked")
  public LockedQueue(int capacity) {
    items = (T[]) new Object[capacity];
  }

  // 1. Acquire lock before any action.
  // 2. Wait for queue being not full.
  // 3. Add item to queue.
  // 4. Release the lock.
  @Override
  public boolean add(T x) {
    lock.lock(); // 1
    try { // 2
      while (size == items.length) notFull.await();
      addUnchecked(x); // 3
    }
    catch(InterruptedException e) {}
    finally {
      lock.unlock(); // 4
    }
    return true;
  }

  // 1. Acquire lock before any action.
  // 2. Wait for queue being not empty.
  // 3. Remove item from queue.
  // 4. Release the lock.
  @Override
  public T remove() {
    T x = null;
    lock.lock(); // 1
    try {
      while (size == 0) notEmpty.await(); // 2
      x = removeUnchecked(); // 3
    }
    catch(InterruptedException e) {}
    finally {
      lock.unlock(); // 4
    }
    return x;
  }

  // 1. Store item in queue, while locking.
  // 2. If no space available, return false.
  @Override
  public boolean offer(T x) {
    lock.lock();     // 1
    if (size == items.length) return false; // 2
    addUnchecked(x); // 1
    lock.unlock();   // 1
    return true;
  }

  // 1. Peek item in queue, without removing.
  // 2. If no item exists, return null.
  @Override
  public T peek() {
    return size > 0? items[head] : null; // 1, 2
  }

  // 1. Remove item from queue, while locking.
  // 2. If no items exists, return null.
  @Override
  public T poll() {
    lock.lock();             // 1
    if (size == 0) return null; // 2
    T x = removeUnchecked(); // 1
    lock.unlock();           // 1
    return x;
  }

  // 1. Store item at the tail end.
  // 2. Move tail to the next free slot.
  // 3. Signal that queue is not empty.
  private void addUnchecked(T x) {
    items[tail] = x; // 1
    if (++tail == items.length) tail = 0; // 2
    ++size;                               // 2
    notEmpty.signal(); // 3
  }

  // 1. Fetch item at the head of queue.
  // 2. Move head to the next item.
  // 3. Signal that queue is not full.
  private T removeUnchecked() {
    T x = items[head]; // 1
    if (++head == items.length) head = 0; // 2
    --size;                               // 2
    notFull.signal(); // 3
    return x;
  }

  @Override
  public Iterator<T> iterator() {
    lock.lock();
    Collection<T> a = new ArrayList<>();
    for (int i=0; i<size; i++)
      a.add(items[(head+i) % size]);
    lock.unlock();
    return a.iterator();
  }
  
  @Override
  public int size() {
    return size;
  }
}
