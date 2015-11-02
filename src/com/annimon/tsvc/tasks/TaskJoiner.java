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
        super.updateValue(value);
    }

    @Override
    public void updateTaskTitle(String title) {
        super.updateTitle(title);
    }

    @Override
    public void updateTaskMessage(String message) {
        super.updateMessage(message);
    }

    @Override
    public void updateTaskProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }

    @Override
    public void updateTaskProgress(long workDone, long max) {
        super.updateProgress(workDone, max);
    }

    @Override
    public boolean isTaskCancelled() {
        return isCancelled();
    }
    
}
