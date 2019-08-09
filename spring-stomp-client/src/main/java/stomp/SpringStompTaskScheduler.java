package stomp;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public class SpringStompTaskScheduler implements TaskScheduler {
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return null;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return null;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return null;
    }
}
