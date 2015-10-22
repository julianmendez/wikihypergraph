package de.tudresden.inf.lat.wikihypergraph.selector;

/**
 * 
 * An object of this class is a tuple tailored for the axiom selector.
 * 
 * @author Julian Mendez
 *
 */
public class SelectorTuple {

	public static final String CSV_SEPARATOR = ", ";

	private final String statement;
	private final String subject;
	private final String relation;
	private final String object;

	/**
	 * Constructs a new select tuple.
	 * 
	 * @param statement
	 *            statement
	 * @param subject
	 *            subject
	 * @param relation
	 *            relation
	 * @param object
	 *            object
	 */
	public SelectorTuple(String statement, String subject, String relation, String object) {
		if (statement == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (subject == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (relation == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (object == null) {
			throw new IllegalArgumentException("Null argument.");
		}

		this.statement = statement;
		this.subject = subject;
		this.relation = relation;
		this.object = object;
	}

	/**
	 * Returns the statement.
	 * 
	 * @return the statement
	 */
	public String getStatement() {
		return this.statement;
	}

	/**
	 * Returns the subject.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * Returns the relation.
	 * 
	 * @return the relation
	 */
	public String getRelation() {
		return this.relation;
	}

	/**
	 * Returns the object.
	 * 
	 * @return the object
	 */
	public String getObject() {
		return this.object;
	}

	@Override
	public int hashCode() {
		return this.statement.hashCode() + (0x1F
				* (this.subject.hashCode() + 0x1F * (this.relation.hashCode() + 0x1F * this.object.hashCode())));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof SelectorTuple) {
			SelectorTuple other = (SelectorTuple) obj;
			return getStatement().equals(other.getStatement()) && getSubject().equals(other.getSubject())
					&& getRelation().equals(other.getRelation()) && getObject().equals(other.getObject());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.statement + CSV_SEPARATOR + this.subject + CSV_SEPARATOR + this.relation + CSV_SEPARATOR
				+ this.object;
	}

}
