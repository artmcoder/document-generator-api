package com.document.generator.pdf;

/**
 * Types of verification of a digital signature on a pdf document depending on the size of the document text
 * @author Artem Yakunin
 * @version 1.0
 */
public enum CheckType {
    WITHOUT_SIGNATURE, SIGNATURE, DOUBLE_SIGNATURE
}
