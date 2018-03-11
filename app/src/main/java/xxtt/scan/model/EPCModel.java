package xxtt.scan.model;

public class EPCModel {
	private int id;
	private String epc;
	private String oepc;
	private String name;
	private int count;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param epc
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * @param epc
	 *            the epc to set
	 */
	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * @return the epc
	 */
	public String getOldEpc() {
		return oepc;
	}

	/**
	 * @param epc
	 *            the oepc to set
	 */
	public void setOldEpc(String oepc) {
		this.oepc = oepc;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EPC [id=" + id + ", epc=" + epc + ", count=" + count + "]";
	}

	public void toEpc() {
		String tepc = this.getOldEpc();
		if (tepc.length() == 32) {
			this.setEpc(tepc.substring(4, 20));
			return;
		}
		this.setEpc(tepc);
	}
}
