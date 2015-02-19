package com.bensmann.queue;

public class MyBoundedBlockingQueueImpl implements BoundedBlockingQueue {

    /**
     * Size of fixed queue.
     */
    private int queueSize;

    /**
     * Backing array for this queue implementation.
     */
    private /*final*/ Object[] backingArray;

    /**
     * Last/highest index in queue. Used to get index only once.
     */
    private int lastIndex;

    /**
     * Position in array; where to add an element?
     */
    private int position;

    /**
     * Initialize fixed size queue.
     * @param queueSize Size of queue.
     */
    public MyBoundedBlockingQueueImpl(int queueSize) {
        this.queueSize = queueSize;
        backingArray = new Object[queueSize];
        lastIndex = backingArray.length - 1;
    }

    public synchronized void put(Object element) throws InterruptedException {
        this.notify(); // Notification is sent when method is left
        // Wait if queue is full
        while (backingArray[lastIndex] != null) {
            this.wait(); // waits for notify()-call from get()
        }
        // Put element at next position
        backingArray[position] = element;
        // Increment position
        position++;
    }

    public synchronized Object get() throws InterruptedException {
        this.notify(); // Notification is sent when method is left
        // Check position of first element in backing array
        int firstElementPosition = 0;
        while (null == backingArray[firstElementPosition]) {
            this.wait(); // wait for notify()-call from put(Object)
        }
        // Get first element from queue
        Object firstElement = backingArray[firstElementPosition];
        // Unset/remove reference to first element
        backingArray[firstElementPosition] = null;
        // Decrement position (for new element)
        position--;
        // Move elements to front of queue
        if (position > 0) { // Queue is empty when position == 0, so no reordering needed
            // Solution 1: create a new array, copy elements, and change reference
            Object[] backing = new Object[queueSize];
            System.arraycopy(backingArray, 1, backing, 0, backingArray.length - 1);
            backingArray = backing;
            /* 
            // Solution 2: Reorder elements within array
            int length = backingArray.length;
            for (int i = 1; i < length; i++) {
                backingArray[i] = backingArray[i - 1];
            }
            // System.arraycopy would be the better choice in terms of performance.
            // But we could declare the reference to our backing array 'final',
            // although this is a bit of an esoteric discussion...
            */
        }
        // Return first element
        return firstElement;
    }

}
