package effective_java.item3.example;

public class GenericSingleton2<T> {
	private static final GenericSingleton2<Object> INSTANCE = new GenericSingleton2<>();

	private GenericSingleton2() {
	}

	public static <T> GenericSingleton2<T> getInstance() {
		return (GenericSingleton2<T>) INSTANCE;
	}
}
