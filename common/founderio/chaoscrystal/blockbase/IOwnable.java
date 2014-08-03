package founderio.chaoscrystal.blockbase;

public interface IOwnable {
	/**
	 * Set the Username of the owner.
	 * @param username The username or an empty string to remove the owner. Null values have to be converted to "".
	 */
	public void setOwner(String username);
	/**
	 * Get the Username of the owner
	 * @return The username or an empty string if no owner was set.
	 */
	public String getOwner();
}
