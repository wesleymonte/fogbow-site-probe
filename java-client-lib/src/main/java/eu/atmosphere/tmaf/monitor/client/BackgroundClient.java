/**
 * <b>ATMOSPHERE</b> - http://www.atmosphere-eubrazil.eu/
 * ***
 * <p>
 * <b>Trustworthiness Monitoring & Assessment Framework</b>
 * Component: Monitor - Client
 * <p>
 * Repository: https://github.com/eubr-atmosphere/tma-framework
 * License: https://github.com/eubr-atmosphere/tma-framework/blob/master/LICENSE
 * <p>
 * <p>
 */
package eu.atmosphere.tmaf.monitor.client;

import eu.atmosphere.tmaf.monitor.message.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <p>
 * <p>
 *
 * @author Nuno Antunes <nmsa@dei.uc.pt>
 */
public class BackgroundClient extends MonitorClient implements Callable<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorClient.class);
    private static final int QUEUE_WAIT_MILLIS = 200;

    private final BlockingQueue<Message> queue;
    private final ExecutorService executor;
    private final AtomicBoolean running;
    private final AtomicInteger enqueuedMessages;
    private Future<Integer> task;

    public BackgroundClient() {
        super();
        this.queue = new ArrayBlockingQueue<>(100000);
        this.executor = Executors.newSingleThreadExecutor();
        this.running = new AtomicBoolean(false);
        this.enqueuedMessages = new AtomicInteger();
    }

    public BackgroundClient(String endpoint) {
        super(endpoint);
        this.queue = new ArrayBlockingQueue<>(100000);
        this.executor = Executors.newSingleThreadExecutor();
        this.running = new AtomicBoolean(false);
        this.enqueuedMessages = new AtomicInteger();
    }

    @Override
    public int dispatch(Message message) {
        int size = this.queue.size();
        if (this.queue.offer(message)) {
            int incrementAndGet = enqueuedMessages.incrementAndGet();
            LOGGER.debug("dispatch++ = " + incrementAndGet);
            return size + 1;
        }
        return -1;
    }

    public boolean start() {
        LOGGER.debug("SenderThread trying to start!");
        if (running.compareAndSet(false, true)) {
            LOGGER.info("SenderThread started!");
            task = executor.submit(this);
            return true;
        }
        return false;
    }

    public boolean shutdown() {
        this.executor.shutdownNow();
        return true;
    }

    public boolean stop() throws InterruptedException, ExecutionException {
        LOGGER.debug("SenderThread trying to stop!");
        if (running.compareAndSet(true, false)) {
            Integer result = task.get();
            LOGGER.info("SenderThread stopped, sent messages: {}!", result);
            return true;
        }
        return false;
    }

    @Override
    public Integer call() {
        int sent = 0;
        Message message;
        while (running.get()) {
            try {
                if ((message = this.queue.poll(QUEUE_WAIT_MILLIS, TimeUnit.MILLISECONDS)) != null) {
                    super.post(message);
                    sent++;
                    LOGGER.debug("sent++ = " + sent);
                }
            } catch (InterruptedException ie) {
                LOGGER.warn("InterruptedException !", ie);
            }
        }
        return sent;
    }
}
