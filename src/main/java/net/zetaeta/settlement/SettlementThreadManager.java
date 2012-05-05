package net.zetaeta.settlement;

import static org.bukkit.Bukkit.getScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SettlementThreadManager implements SettlementConstants {
    public static ExecutorService threadPool;
    
    public static void init() {
        threadPool = Executors.newCachedThreadPool();
    }
    
    public static <T> Future<T> submitAsyncTask(Callable<T> task) {
        return threadPool.submit(task);
    }
    
    public static Future<?> submitAsyncTask(Runnable task) {
        return threadPool.submit(task);
    }
    
    public static <T> Future<T> submitSyncTask(Callable<T> task) {
        return Bukkit.getScheduler().callSyncMethod(plugin, task);
    }
    
    public static Future<?> submitSyncTask(Runnable task) {
        int taskID = getScheduler().scheduleSyncDelayedTask(plugin, task);
        return new SyncFuture(taskID);
    }
    
    private static class SyncFuture implements Future<Object> {
        private int taskID;
        
        public SyncFuture(int taskID) {
            this.taskID = taskID;
        }
        
        @Override
        public boolean cancel(boolean paramBoolean) {
            if (getScheduler().isQueued(taskID)) {
                getScheduler().cancelTask(taskID);
                return true;
            }
            return false;
        }

        @Override
        public boolean isCancelled() {
            return !(getScheduler().isCurrentlyRunning(taskID) && getScheduler().isQueued(taskID));
        }

        @Override
        public boolean isDone() {
            return !(getScheduler().isCurrentlyRunning(taskID) && getScheduler().isQueued(taskID));
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public Object get(long paramLong, TimeUnit paramTimeUnit)
                throws InterruptedException, ExecutionException,
                TimeoutException {
            return null;
        }
        
    }
}