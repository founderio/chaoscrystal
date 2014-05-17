package founderio.chaoscrystal.aspects;

import java.util.Arrays;

import net.minecraft.item.Item;

public class Repair {
	public final Item item;
	public final String[] aspects;
	public final int[] amounts;

	public Repair(Item item, String[] aspects, int[] amounts) {
		this.item = item;
		this.aspects = aspects;
		this.amounts = amounts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(amounts);
		result = prime * result + Arrays.hashCode(aspects);
		result = prime * result + item.hashCode();
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
		if (item != other.item)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Repair [item=" + item + ", aspects="
				+ Arrays.toString(aspects) + ", amounts="
				+ Arrays.toString(amounts) + "]";
	}

}
