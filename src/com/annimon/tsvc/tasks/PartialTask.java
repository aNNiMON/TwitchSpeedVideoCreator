package com.annimon.tsvc.tasks;

/**
 *
 * @author aNNiMON
 */
public abstract class PartialTask<T> {
    
    private TaskListener<T> listener;

    public void setListener(TaskListener<T> listener) {
        this.listener = listener;
    }
    
    protected abstract T call() throws Exception;
    
    protected void updateValue(T value) {
        listener.updateTaskValue(value);
    }

    protected void updateTitle(String title) {
        listener.updateTaskTitle(title);
    }

    protected void updateMessage(String message) {
        listener.updateTaskMessage(message);
    }

    protected void updateProgress(double workDone, double max) {
        listener.updateTaskProgress(workDone, max);
    }

    protected void updateProgress(long workDone, long max) {
        listener.updateTaskProgress(workDone, max);
    }

    public boolean isCancelled() {
        return listener.isTaskCancelled();
    }
    
}
