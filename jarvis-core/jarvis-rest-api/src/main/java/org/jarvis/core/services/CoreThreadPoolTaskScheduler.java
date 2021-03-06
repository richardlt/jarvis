package org.jarvis.core.services;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * core task thread
 */
@Component
public class CoreThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 7750643698080731384L;

	/**
	 * core task thread
	 */
	public CoreThreadPoolTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("jarvis-job-");
		scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		scheduler.setRemoveOnCancelPolicy(true);
	}
}
