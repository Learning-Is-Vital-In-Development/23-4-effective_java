package effective_java.item3.example;

import java.util.function.Supplier;

public class Singleton2Supplier {

	public static String use(Supplier<Singleton2> singleton2) {
		return singleton2.get().toString();
	}
}
