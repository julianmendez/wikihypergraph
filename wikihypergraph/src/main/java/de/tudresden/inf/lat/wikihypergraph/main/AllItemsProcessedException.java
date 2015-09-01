package de.tudresden.inf.lat.wikihypergraph.main;

public class AllItemsProcessedException extends RuntimeException {

	private static final long serialVersionUID = -7262244559311838849L;

	/**
	 * Constructs an all items processed exception.
	 * 
	 * @param message
	 *            message to be displayed
	 */
	public AllItemsProcessedException(String message) {
		super(message);
	}

	/**
	 * Constructs an all items processed exception.
	 * 
	 * @param message
	 *            message to be displayed
	 * @param cause
	 *            cause of the exception
	 */
	public AllItemsProcessedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an all items processed exception.
	 * 
	 * @param cause
	 *            cause of the exception
	 */
	public AllItemsProcessedException(Throwable cause) {
		super(cause);
	}

}
