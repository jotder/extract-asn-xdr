package com.gamma.asn1.cli;

/**
 * Defines the error handling strategies for the processing pipeline.
 */
public enum ProcessingMode {
    /**
     * Throws an exception and stops all processing immediately.
     */
    FAIL_FAST,

    /**
     * Logs the error, discards the current record, and attempts to recover.
     */
    SKIP_RECORD,

    /**
     * Logs the error, sets the problematic field to null, and continues processing the record.
     */
    SKIP_FIELD
}