// Created: 17 Juli 2024
package de.freese.player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Thomas Freese
 */
public final class PlayerSettings {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(8, Thread.ofPlatform().daemon().name("player-", 1).factory());
    private static final ExecutorService EXECUTOR_SERVICE_PIPE_READER = Executors.newFixedThreadPool(3, Thread.ofPlatform().daemon().name("pipe-reader-", 1).factory());

    public static ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }

    public static ExecutorService getExecutorServicePipeReader() {
        return EXECUTOR_SERVICE_PIPE_READER;
    }

    private PlayerSettings() {
        super();
    }
}
