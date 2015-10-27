package com.annimon.tsvc.tasks;

/**
 *
 * @author aNNiMON
 * @param <T> input argument type
 * @param <R> result type
 */
public interface Task<T, R> {
    
    public R process(T args) throws Exception;
}
