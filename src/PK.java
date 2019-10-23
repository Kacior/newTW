import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PK {

    public static void main(String[] args) throws InterruptedException {
        ItemQueue itemQueue = new ItemQueue(8);

        //Create a producer and a consumer.
        int c=10;
        int p=10;

            Thread producer = new Producer(itemQueue);
            Thread consumer = new Consumer(itemQueue);
            producer.start();
            consumer.start();

    }

    static class ItemQueue {
        boolean isCFirstOccupied=false;
        boolean isPFirstOccupied=false;
        private ArrayList<String> items= new ArrayList<>();
        private int current = 0;
        private int capacity=0;

        private final Lock lock;
        private final Condition isPFirst;
        private final Condition isPRest;
        private final Condition isCFirst;
        private final Condition isCRest;

        public ItemQueue(int capacity) {
            this.capacity=capacity;
            lock = new ReentrantLock();
            isCFirst = lock.newCondition();
            isCRest = lock.newCondition();
            isPFirst=lock.newCondition();
            isPRest=lock.newCondition();
        }

        public void add() throws InterruptedException {
            lock.lock();
            int randProduct=(int)(Math.random() * 4 + 1);
            while(isPFirstOccupied)
                isPRest.await();
                while (items.size() + randProduct > capacity)
                    isPFirst.await();
                for (int i = 0; i < randProduct; i++)
                    items.add(current++, "Item");
                //Notify the consumer that there is data available.
                isPFirstOccupied=true;
                isPRest.signal();
                isCFirst.signal();
                lock.unlock();
        }

        public void remove() throws InterruptedException {
            lock.lock();
            int randConsume=(int)(Math.random() * 4 + 1);
            while (items.size()-randConsume < 0) {
                isFirst.await();
            }
            for(int i=0;i<randConsume;i++)
                items.remove(--current);
            //Notify the producer that there is space available.
            isRest.signal();
            lock.unlock();
        }
    }

    static class Producer extends Thread {
        private final ItemQueue queue;

        public Producer(ItemQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true){
                try {
                    queue.add();
                    System.out.println("[Produced]");
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer extends Thread {
        private final ItemQueue queue;

        public Consumer(ItemQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true){
                try {
                    queue.remove();
                    System.out.println("[Consumed]:");
                }catch(InterruptedException e ){
                    e.printStackTrace();
                }
            }
        }
    }
}