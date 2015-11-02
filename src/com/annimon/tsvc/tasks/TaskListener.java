package com.annimon.tsvc.tasks;

/**
 *
 * @author aNNiMON
 */
public interface TaskListener<T> {

    void updateTaskValue(T value);

    void updateTaskTitle(String title);

    void updateTaskMessage(String message);

    void updateTaskProgress(double workDone, double max);

    void updateTaskProgress(long workDone, long max);

    boolean isTaskCancelled();
}
