import edu.princeton.cs.algs4.Queue;
import java.util.Iterator;

public class MergeSort {
    /**
     * Removes and returns the smallest item that is in q1 or q2.
     *
     * The method assumes that both q1 and q2 are in sorted order, with the smallest item first. At
     * most one of q1 or q2 can be empty (but both cannot be empty).
     *
     * @param q1 A Queue in sorted order from least to greatest.
     * @param q2 A Queue in sorted order from least to greatest.
     * @return The smallest item that is in q1 or q2.
     */
    private static <Item extends Comparable> Item getMin(
            Queue<Item> q1, Queue<Item> q2) {
        if (q1.isEmpty()) {
            return q2.dequeue();
        } else if (q2.isEmpty()) {
            return q1.dequeue();
        } else {
            // Peek at the minimum item in each queue (which will be at the front, since the
            // queues are sorted) to determine which is smaller.
            Comparable q1Min = q1.peek();
            Comparable q2Min = q2.peek();
            if (q1Min.compareTo(q2Min) <= 0) {
                // Make sure to call dequeue, so that the minimum item gets removed.
                return q1.dequeue();
            } else {
                return q2.dequeue();
            }
        }
    }

    /** Returns a queue of queues that each contain one item from items. */
    private static <Item extends Comparable> Queue<Queue<Item>>
            makeSingleItemQueues(Queue<Item> items) {
        Queue<Queue<Item>> qOfq = new Queue<Queue<Item>>();
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Queue<Item> insert = new Queue<Item>();
            insert.enqueue(iterator.next());
            qOfq.enqueue(insert);
        }
        return qOfq;
    }

    /**
     * Returns a new queue that contains the items in q1 and q2 in sorted order.
     *
     * This method should take time linear in the total number of items in q1 and q2.  After
     * running this method, q1 and q2 will be empty, and all of their items will be in the
     * returned queue.
     *
     * @param q1 A Queue in sorted order from least to greatest.
     * @param q2 A Queue in sorted order from least to greatest.
     * @returns A Queue containing all of the q1 and q2 in sorted order, from least to
     *     greatest.
     *
     */
    private static <Item extends Comparable> Queue<Item> mergeSortedQueues(
            Queue<Item> q1, Queue<Item> q2) {
        Queue<Item> result = new Queue<Item>();
        while (q1.size() + q2.size() > 0) {
            result.enqueue(getMin(q1, q2));
        }
        return result;
    }

    /** Returns a Queue that contains the given items sorted from least to greatest. */
    public static <Item extends Comparable> Queue<Item> mergeSort(
            Queue<Item> items) {
        if (items.size() < 2) {
            return items;
        }
        Queue<Queue<Item>> qOfq = makeSingleItemQueues(items);
        Queue<Queue<Item>> intermediate = build(qOfq);
        Queue<Item> result = intermediate.dequeue();
        return result;
    }

    private static <Item extends Comparable> Queue<Queue<Item>> build(Queue<Queue<Item>> input) {
        if (input.size() == 1) {
            return input;
        }
        Queue<Queue<Item>> result = new Queue<Queue<Item>>();
        while (!input.isEmpty()) {
            Queue<Item> q1 = input.dequeue();
            Queue<Item> q2 = new Queue<Item>();
            if (!input.isEmpty()) {
                q2 = input.dequeue();
            }
            result.enqueue(mergeSortedQueues(q1, q2));
        }
        return build(result);
    }

    public static void main(String[] args) {
        Queue<String> q = new Queue<String>();
        q.enqueue("agea");
        q.enqueue("alexPan");
        q.enqueue("koop");
        q.enqueue("jolly_joshy");
        q.enqueue("agur");
        q.enqueue("workday");
        q.enqueue("bruh");
        q.enqueue("yung chicken");
        Queue<String> s = MergeSort.mergeSort(q);
        while (!q.isEmpty()) {
            System.out.println("q " + q.dequeue());
        }
        while (!s.isEmpty()) {
            System.out.println("s " + s.dequeue());
        }
    }
}
