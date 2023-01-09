package effective_java.item3.example;

import java.io.Serializable;

public class Singleton1 implements Serializable {
	public static final Singleton1 INSTANCE = new Singleton1();
	private static int count;

	private Singleton1() {
		// count++;
		// if (count != 1) {
		// 	throw new RuntimeException("Singleton instance is already exist!");
		// }
	}
}
