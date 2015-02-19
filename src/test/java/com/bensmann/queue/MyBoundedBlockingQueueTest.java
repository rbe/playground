package com.bensmann.queue;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBoundedBlockingQueueTest {

    private static final Logger logger = Logger.getLogger(MyBoundedBlockingQueueTest.class.getName());

    private int queueSize = 10;

    /**
     * This test will put and get one element.
     * @throws InterruptedException
     */
    @Test
    public void testOneElement() throws InterruptedException {
        final BoundedBlockingQueue myQueue = new MyBoundedBlockingQueueImpl(queueSize);
        int i = 1;
        // Insert element into queue
        myQueue.put(String.valueOf(i));
        // Retrieve elements
        Object element = myQueue.get();
        // Test
        logger.info(String.format("%s == %d? %s", element, i, String.valueOf(i).equals(element)));
        Assert.assertEquals(String.valueOf(i), element);
    }

    /**
     * This test will put and get some elements.
     * @throws InterruptedException
     */
    @Test
    public void testSomeElements() throws InterruptedException {
        final BoundedBlockingQueue myQueue = new MyBoundedBlockingQueueImpl(queueSize);
        int count = queueSize / 2;
        // Insert elements into queue
        for (int i = 0; i < count; i++) {
            myQueue.put(String.valueOf(i));
            logger.info(String.format("Inserted element '%d' into queue", i));
        }
        // Retrieve elements
        Object element;
        for (int i = 0; i < count; i++) {
            element = myQueue.get();
            // Test
            logger.info(String.format("%s == %d? %s", element, i, String.valueOf(i).equals(element)));
            Assert.assertEquals(String.valueOf(i), element);
        }
    }

    /**
     * This test will put and get 'queueSize' elements.
     * @throws InterruptedException
     */
    @Test
    public void testFullQueue() throws InterruptedException {
        final BoundedBlockingQueue myQueue = new MyBoundedBlockingQueueImpl(queueSize);
        int count = queueSize;
        // Insert elements into queue
        for (int i = 0; i < count; i++) {
            myQueue.put(String.valueOf(i));
            logger.info(String.format("Inserted element '%d' into queue", i));
        }
        // Retrieve elements
        Object element;
        for (int i = 0; i < count; i++) {
            element = myQueue.get();
            // Test
            logger.info(String.format("%s == %d? %s", element, i, String.valueOf(i).equals(element)));
            Assert.assertEquals(String.valueOf(i), element);
        }
    }

    /**
     * This test uses more than 'queueSize' elements to test blocking.
     * Blocking of inserting too many elements in the queue is tested by creating a thread which
     * inserts queueSize + 1 more element, thus blocks, and gets cancelled after a period of time.
     * <p>Example:</p>
     * Thread 1 tries to insert more than queueSize elements, blocking at queueSize:
     * <pre>
     *     Mai 22, 2013 3:50:59 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$1 call
     *     INFO: testInsertTooManyElements.putIntoQueueTask: Inserted element '0' into queue
     *     [...]
     *     Mai 22, 2013 3:50:59 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$1 call
     *     INFO: testInsertTooManyElements.putIntoQueueTask: Inserted element '9' into queue
     * </pre>
     * Thread 2 starts consuming elements from the queue, notice the 2 seconds delay:
     * <pre>
     *     Mai 22, 2013 3:51:01 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$2 call
     *     INFO: testInsertTooManyElements.getFromQueueTask: Wait for get element...
     *     Mai 22, 2013 3:51:01 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$2 call
     *     INFO: testInsertTooManyElements.getFromQueueTask: Got element '0'
     * </pre>
     * Thread 1 continues and inserts another element as the queue now has capacity:
     * <pre>
     *     Mai 22, 2013 3:51:01 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$1 call
     *     INFO: testInsertTooManyElements.putIntoQueueTask: Inserted element '10' into queue
     * </pre>
     * Thread 2 continues consuming elements:
     * <pre>
     *     Mai 22, 2013 3:51:01 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$2 call
     *     INFO: testInsertTooManyElements.getFromQueueTask: Wait for get element...
     *     Mai 22, 2013 3:51:01 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$2 call
     *     INFO: testInsertTooManyElements.getFromQueueTask: Got element '1'
     *     [...]
     *     INFO: testInsertTooManyElements.getFromQueueTask: Wait for get element...
     *     Mai 22, 2013 3:51:01 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$2 call
     *     INFO: testInsertTooManyElements.getFromQueueTask: Got element '10'
     * </pre>
     * @throws InterruptedException
     */
    @Test
    public void testInsertTooManyElements() throws InterruptedException {
        final BoundedBlockingQueue myQueue = new MyBoundedBlockingQueueImpl(queueSize);
        // Insert elements into queue
        Callable putIntoQueueTask = new Callable() {
            @Override
            public Object call() throws Exception {
                logger.entering(MyBoundedBlockingQueueTest.class.getName(), "testInsertTooManyElements.putIntoQueueTask");
                try {
                    for (int i = 0; i < queueSize + 1; i++) {
                        myQueue.put(String.valueOf(i)); // This call will block when trying to insert element queueSize + 1
                        logger.info(String.format("testInsertTooManyElements.putIntoQueueTask: Inserted element '%d' into queue", i));
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "testInsertTooManyElements.putIntoQueueTask interrupted", e);
                }
                return null;
            }
        };
        // Retrieve elements
        Callable<List<Object>> getFromQueueTask = new Callable<List<Object>>() {
            @Override
            public List<Object> call() throws Exception {
                logger.entering(MyBoundedBlockingQueueTest.class.getName(), "testInsertTooManyElements.getFromQueueTask");
                List<Object> elements = new ArrayList<>();
                try {
                    Object element;
                    for (int i = 0; i < queueSize + 1; i++) {
                        logger.info("testInsertTooManyElements.getFromQueueTask: Wait for get element...");
                        element = myQueue.get();
                        logger.info(String.format("testInsertTooManyElements.getFromQueueTask: Got element '%s'", element));
                        elements.add(element);
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "testInsertTooManyElements.getFromQueueTask interrupted", e);
                }
                return elements;
            }
        };
        // Executor
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Start task for MyQueue.put(Object) so first task must complete successfully
        executor.submit(putIntoQueueTask);
        // Wait some time
        Thread.sleep(2 * 1000);
        // Start task for MyQueue.get()
        executor.submit(getFromQueueTask);
        // Await termination
        executor.awaitTermination(5, TimeUnit.SECONDS);
        logger.info("completed");
    }

    /**
     * The queue should block when a thread tries to get an element while there are no elements.
     * When an element in put into the queue, the thread should get that element and proceed.
     * <p>Example:</p>
     * Thread 1 calls MyQueue.get():
     * <pre>
     *     Mai 22, 2013 2:45:52 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$2 call
     *     INFO: testGetFromEmptyQueue.getFromQueueTask: Wait for get element...
     * </pre>
     * Thread 2 puts an element into the queue, notice the 2 seconds delay:
     * <pre>
     *     Mai 22, 2013 2:45:54 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$3 call
     *     INFO: testGetFromEmptyQueue.putIntoQueueTask: Putting element into queue (Hello other task, please go on)...
     * </pre>
     * Thread 1 get the element and proceeds:
     * <pre>
     *     Mai 22, 2013 2:45:54 PM com.yieldlab.queue.MyBoundedBlockingQueueTest$2 call
     *     INFO: testGetFromEmptyQueue.getFromQueueTask: Got element Hello other task, please go on
     * </pre>
     * @throws InterruptedException
     */
    @Test
    public void testGetFromEmptyQueue() throws InterruptedException {
        final BoundedBlockingQueue myQueue = new MyBoundedBlockingQueueImpl(queueSize);
        Callable getFromQueueTask = new Callable() {
            @Override
            public Object call() throws Exception {
                logger.entering(MyBoundedBlockingQueueTest.class.getName(), "testGetFromEmptyQueue.getFromQueueTask");
                Object element = null;
                try {
                    logger.info("testGetFromEmptyQueue.getFromQueueTask: Wait for get element...");
                    element = myQueue.get();
                    logger.info(String.format("testGetFromEmptyQueue.getFromQueueTask: Got element '%s'", element));
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "testGetFromEmptyQueue.getFromQueueTask interrupted", e);
                }
                return element;
            }
        };
        Callable putIntoQueueTask = new Callable() {
            @Override
            public Object call() throws Exception {
                logger.entering(MyBoundedBlockingQueueTest.class.getName(), "testGetFromEmptyQueue.putIntoQueueTask");
                String element = "Hello other task, please go on";
                try {
                    logger.info(String.format("testGetFromEmptyQueue.putIntoQueueTask: Putting element '%s'into queue", element));
                    myQueue.put(element);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "testGetFromEmptyQueue.putIntoQueueTask interrupted", e);
                }
                return element;
            }
        };
        // Executor
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Start task for MyQueue.get()
        executor.submit(getFromQueueTask);
        // Wait some time
        Thread.sleep(2 * 1000);
        // Start task for MyQueue.put(Object) so first task must complete successfully
        executor.submit(putIntoQueueTask);
        // Await termination
        executor.awaitTermination(5, TimeUnit.SECONDS);
        logger.info("completed");
    }

}
