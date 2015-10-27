package com.annimon.tsvc;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handling exceptions.
 * @author aNNiMON
 */
public class ExceptionHandler {

    private static final boolean DEBUG = true;
    private static final String TAG = "tsvc::ExceptionHandler";
    private static final Logger LOGGER = Logger.getLogger(TAG);
    
    public static void log(String message) {
        if (DEBUG) {
            LOGGER.log(Level.INFO, message);
        }
    }
    
    public static void log(String message, Exception ex) {
        if (DEBUG) {
            LOGGER.log(Level.WARNING, message, ex);
        }
    }
    
    public static void log(Exception ex) {
        if (DEBUG) {
            LOGGER.log(Level.WARNING, getErrorMessage(ex), ex);
        }
    }
    
    private static String getErrorMessage(Exception ex) {
        return ex.getMessage();
    }
}
