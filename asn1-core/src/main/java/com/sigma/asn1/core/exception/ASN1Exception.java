package com.sigma.asn1.core.exception;

/**
 * Base class for general ASN.1 processing exceptions.
 * Contains common context like approximate byte offset and tag path.
 */
public class ASN1Exception extends Exception {
    private long approximateByteOffset = -1;
    private String tagPath; // e.g., "callEventRecord.servingNetwork.imsi"

    public ASN1Exception(String message) {
        super(message);
    }

    public ASN1Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public ASN1Exception(String message, long approximateByteOffset, String tagPath) {
        super(message);
        this.approximateByteOffset = approximateByteOffset;
        this.tagPath = tagPath;
    }

    public ASN1Exception(String message, Throwable cause, long approximateByteOffset, String tagPath) {
        super(message, cause);
        this.approximateByteOffset = approximateByteOffset;
        this.tagPath = tagPath;
    }

    public long getApproximateByteOffset() {
        return approximateByteOffset;
    }

    public void setApproximateByteOffset(long approximateByteOffset) {
        this.approximateByteOffset = approximateByteOffset;
    }

    public String getTagPath() {
        return tagPath;
    }

    public void setTagPath(String tagPath) {
        this.tagPath = tagPath;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (tagPath != null && !tagPath.isEmpty()) {
            sb.append(" (Path: ").append(tagPath).append(")");
        }
        if (approximateByteOffset >= 0) {
            sb.append(" (Offset: ~").append(approximateByteOffset).append(")");
        }
        return sb.toString();
    }
}
