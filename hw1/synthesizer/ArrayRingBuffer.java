package synthesizer;
import java.util.Iterator;

public class ArrayRingBuffer<T> extends AbstractBoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;            // index for the next dequeue or peek
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;
    //FillCount and capacity are automatically inherited?


    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) {
        this.capacity = capacity;
        rb = (T[]) new Object[capacity];
        fillCount = 0;
        first = 0;
        last = 0;
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException("Ring buffer overflow"). Exceptions
     * covered Monday.
     */
    @Override
    public void enqueue(T x) {
        if (this.isFull()) {
            this.dequeue();
        }
        if (this.isEmpty()) {
            rb[0] = x;
            fillCount += 1;
            return;
        }
        rb[(last + 1) % capacity] = x;
        last = (last + 1) % capacity;
        fillCount += 1;
    }

    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow"). Exceptions
     * covered Monday.
     */
    @Override
    public T dequeue() {
        if (this.isEmpty()) {
            throw new RuntimeException("Ring buffer underflow");
        }
        if (first == last) {
            T x = rb[first];
            first = (first + 1) % capacity;
            last = first;
            fillCount -= 1;
            return x;
        } else {
            T x = rb[first];
            first = (first + 1) % capacity;
            fillCount -= 1;
            return x;
        }
    }

    /**
     * Return oldest item, but don't remove it.
     */
    @Override
    public T peek() {
        if (this.isEmpty()) {
            throw new RuntimeException("Ring buffer underflow");
        }
        return rb[first];
    }

    private class ArrayIterator implements Iterator<T> {
        private int ptr;
        ArrayIterator() {
            ptr = 0;
        }
        public boolean hasNext() {
            return (ptr != fillCount);
        }
        public T next() {
            T returnItem = rb[ptr];
            ptr += 1;
            return returnItem;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }
}
