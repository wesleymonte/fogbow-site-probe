package cloud.fogbow.probes.core.utils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ManagerTimer {

	private ScheduledExecutorService executor;
	private ScheduledFuture<?> future;


	public ManagerTimer(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public void scheduleAtFixedRate(final Runnable task, long delay, long period) {
		this.future = executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					task.run();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, delay, period, TimeUnit.MILLISECONDS);
	}

	public void cancel() {
		if (future != null) {
			future.cancel(false);
		}
		future = null;
	}

	public boolean isScheduled() {
		return future != null && !future.isCancelled();
	}

}