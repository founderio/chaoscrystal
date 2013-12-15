package founderio.chaoscrystal.degradation;

import java.util.Arrays;

public class Repair {
	public final int itemId;
	public final String[] aspects;
	public final int[] amounts;
	public Repair(int itemId, String[] aspects, int[] amounts) {
		super();
		this.itemId = itemId;
		this.aspects = aspects;
		this.amounts = amounts;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(amounts);
		result = prime * result + Arrays.hashCode(aspects);
		result = prime * result + itemId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Repair other = (Repair) obj;
		if (!Arrays.equals(amounts, other.amounts))
			return false;
		if (!Arrays.equals(aspects, other.aspects))
			return false;
		if (itemId != other.itemId)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Repair [itemId=" + itemId + ", aspects="
				+ Arrays.toString(aspects) + ", amounts="
				+ Arrays.toString(amounts) + "]";
	}
	
	
}
