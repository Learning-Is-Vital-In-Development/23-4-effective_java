package effective_java.item3.example;

import java.io.Serializable;

public class Singleton2 implements Serializable {
	private static final Singleton2 INSTANCE = new Singleton2();
	private static int count;

	private Singleton2() {
		// count++;
		// if (count != 1) {
		// 	throw new RuntimeException("Singleton instance is already exist!");
		// }
	}

	public static Singleton2 getInstance() {
		return INSTANCE;
	}
}
