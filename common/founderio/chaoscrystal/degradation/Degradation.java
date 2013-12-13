package founderio.chaoscrystal.degradation;

import java.util.Arrays;

import net.minecraft.item.ItemStack;

public class Degradation {
	public final ItemStack source;
	public final String[] aspects;
	public final int[] amounts;
	public final ItemStack degraded;

	public Degradation(ItemStack source, String[] aspects, int[] amounts,
			ItemStack degraded) {
		this.source = source;
		this.aspects = aspects;
		this.amounts = amounts;
		this.degraded = degraded;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(amounts);
		result = prime * result + Arrays.hashCode(aspects);
		result = prime * result
				+ ((degraded == null) ? 0 : degraded.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		Degradation other = (Degradation) obj;
		if (!Arrays.equals(amounts, other.amounts))
			return false;
		if (!Arrays.equals(aspects, other.aspects))
			return false;
		if (degraded == null) {
			if (other.degraded != null)
				return false;
		} else if (!degraded.equals(other.degraded))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Degradation [source=" + source + ", aspects="
				+ Arrays.toString(aspects) + ", amounts="
				+ Arrays.toString(amounts) + ", degraded=" + degraded + "]";
	}

}
