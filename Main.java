import java.util.*;

class Main {
  static Queue<String> queue;
  static int CAP = 2;
  static int OPS = 4;
  // CAP: capacity of queue
  // OPS: number to add()s / remove()s

  static Thread consumer(String id, long wait) {
    Thread t = new Thread(() -> {
      try {
      for(int i=0; i<OPS; i++) {
        log(id+": remove()\t"+queue);
        String x = queue.remove();
        log(id+": sleep()\t"+x);
        Thread.sleep(wait);
      }
      }
      catch(InterruptedException e) {}
    });
    t.start();
    return t;
  }

  static Thread producer(String id, long wait) {
    Thread t = new Thread(() -> {
      try {
      for(int i=0; i<OPS; i++) {
        log(id+": add()\t"+queue);
        queue.add(id);
        log(id+": sleep()\t"+queue);
        Thread.sleep(wait);
      }
      }
      catch(InterruptedException e) {}
    });
    t.start();
    return t;
  }

  // For test to pass, consumers A, B
  // should not remove same value.
  static void testThreads(long waitc, long waitp) {
    log("For test to pass, consumers A, B");
    log("should not remove same value.");
    Thread A = consumer("A", waitc);
    Thread B = consumer("B", waitc);
    Thread C = producer("C", waitp);
    Thread D = producer("D", waitp);
    try {
    A.join();
    B.join();
    C.join();
    D.join();
    }
    catch(InterruptedException e) {}
    log("Test done.\n");
  }

  public static void main(String[] args) {
    queue = new LockedQueue<>(CAP);
    log("Starting fast consumers test ...");
    testThreads(10, 20);
    log("Starting fast producers test ...");
    testThreads(20, 10);
  }

  static void log(String x) {
    System.out.println(x);
  }
}
