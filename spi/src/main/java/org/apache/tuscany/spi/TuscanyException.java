package org.apache.tuscany.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * The root checked exception for the Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public abstract class TuscanyException extends Exception {
    private static final long serialVersionUID = -7847121698339635268L;
    private List<String> contextStack;
    private String identifier;

    /**
     * Override constructor from Exception.
     *
     * @see Exception
     */
    public TuscanyException() {
        super();
    }

    /**
     * Override constructor from Exception.
     *
     * @param message passed to Exception
     * @see Exception
     */
    public TuscanyException(String message) {
        super(message);
    }

    /**
     * Override constructor from Exception.
     *
     * @param message passed to Exception
     * @param cause   passed to Exception
     * @see Exception
     */
    public TuscanyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from Exception.
     *
     * @param cause passed to Exception
     * @see Exception
     */
    public TuscanyException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns a collection of names representing the context call stack where the error occured. The top of the stack
     * is the first element in the collection.
     *
     * @return a collection of names representing the context call stack
     */
    public List<String> returnContextNames() {
        if (contextStack == null) {
            contextStack = new ArrayList<String>();
        }
        return contextStack;
    }

    /**
     * Pushes a context name where an error occured onto the call stack.
     *
     * @param name the name of a context to push on the stack
     */
    public void addContextName(String name) {
        if (contextStack == null) {
            contextStack = new ArrayList<String>();
        }
        contextStack.add(name);
    }

    /**
     * Returns a string representing additional error information referred to in the error message.
     *
     * @return additional error information
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets an additional error information referred to in the error message.
     *
     * @param identifier additional error information
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMessage() {
        if (identifier == null && contextStack == null) {
            return super.getMessage();
        }
        StringBuilder b = new StringBuilder(256);
        b.append(super.getMessage());

        if (identifier != null) {
            b.append(" [").append(identifier).append(']');
        }
        if (contextStack != null) {
            b.append("\nContext stack trace: ");
            for (int i = contextStack.size() - 1; i >= 0; i--) {
                b.append('[').append(contextStack.get(i)).append(']');
            }
        }
        return b.toString();
    }
}
