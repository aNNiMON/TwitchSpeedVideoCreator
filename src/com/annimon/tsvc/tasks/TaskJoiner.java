package com.annimon.tsvc.tasks;

import javafx.concurrent.Task;

/**
 * Join {@link PartialTask} tasks.
 * 
 * @author aNNiMON
 */
public final class TaskJoiner extends Task<Boolean> implements TaskListener<Boolean> {
    
    private final PartialTask[] partialTasks;

    public TaskJoiner(PartialTask... tasks) {
        this.partialTasks = tasks;
        for (PartialTask task : tasks) {
            task.setListener(TaskJoiner.this);
        }
    }
    
    @Override
    protected Boolean call() throws Exception {
        for (PartialTask partialTask : partialTasks) {
            partialTask.call();
        }
        return true;
    }

    @Override
    public void updateTaskValue(Boolean value) {
        updateValue(value);
    }

    @Override
    public void updateTaskTitle(String title) {
        updateTitle(title);
    }

    @Override
    public void updateTaskMessage(String message) {
        updateMessage(message);
    }

    @Override
    public void updateTaskProgress(double workDone, double max) {
        updateProgress(workDone, max);
    }

    @Override
    public void updateTaskProgress(long workDone, long max) {
        updateProgress(workDone, max);
    }

    @Override
    public boolean isTaskCancelled() {
        return isCancelled();
    }
    
}
