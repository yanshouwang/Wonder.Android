package dev.yanshouwang.wonder.launcher.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Extension of {@link AbstractExecutorService} which executed on a provided looper.
 */
public class LooperExecutor extends AbstractExecutorService {
    private final Handler mHandler;

    public LooperExecutor(Looper looper) {
        this.mHandler = new Handler(looper);
    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * Same as execute, but never runs the action inline.
     */
    public void post(Runnable command) {
        mHandler.post(command);
    }

    /**
     * Returns the thread for this executor
     */
    public Thread getThread() {
        return mHandler.getLooper().getThread();
    }

    /**
     * Returns the looper for this executor
     */
    public Looper getLooper() {
        return mHandler.getLooper();
    }

    /**
     * Set the priority of a thread, based on Linux priorities.
     *
     * @param priority Linux priority level, from -20 for highest scheduling priority
     *                 to 19 for lowest scheduling priority.
     * @see Process#setThreadPriority(int, int)
     */
    public void setThreadPriority(int priority) {
        final HandlerThread thread = (HandlerThread) getThread();
        final int tid = thread.getThreadId();
        Process.setThreadPriority(tid, priority);
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Override
    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Override
    @Deprecated
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(Runnable command) {
        if (mHandler.getLooper() == Looper.myLooper()) {
            command.run();
        } else {
            mHandler.post(command);
        }
    }
}
