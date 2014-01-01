package founderio.util;

import java.util.List;
import java.util.Random;

public final class ListUtil {
	private ListUtil() {
		// Util class
	}

	public static <T> T getRandomFromList(List<T> list, Random rand) {
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(rand.nextInt(list.size()));
		}
	}
}
