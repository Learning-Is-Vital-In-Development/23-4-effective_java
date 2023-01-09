package effective_java.item3.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

public class Main {

	public static void main(String[] args) throws Exception {
		attackSingleton1();

		// attackSingleton2();

		// useGenericSingleton();

		// useSupplier();

		// attackSingleton3();

		// serializeTest(Singleton1.INSTANCE);
		// serializeTest(Singleton2.getInstance());
		// serializeTest(Singleton3.INSTANCE);
	}

	private static void attackSingleton1() throws Exception {
		Singleton1 singleton1 = Singleton1.INSTANCE;
		Constructor<Singleton1> constructor = Singleton1.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		Singleton1 reflectionSingleton1 = constructor.newInstance();
		System.out.println(reflectionSingleton1 == singleton1);
	}

	private static void attackSingleton2() throws Exception {
		Singleton2 singleton2 = Singleton2.getInstance();
		Constructor<Singleton2> constructor = Singleton2.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		Singleton2 reflectionSingleton2 = constructor.newInstance();
		System.out.println(reflectionSingleton2 == singleton2);
	}

	private static void useGenericSingleton() {
		GenericSingleton2<String> genericSingleton2 = GenericSingleton2.getInstance();
	}

	private static void useSupplier() {
		String result = Singleton2Supplier.print(Singleton2::getInstance);
		System.out.println("result = " + result);
	}

	private static void attackSingleton3() throws Exception {
		Singleton3 singleton3 = Singleton3.INSTANCE;
		Constructor<Singleton3> constructor = Singleton3.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		Singleton3 reflectionSingleton3 = constructor.newInstance();
		System.out.println(singleton3 == reflectionSingleton3);
	}

	private static <T> void serializeTest(T singleton) throws Exception {
		T before = singleton;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(before);
		byte[] byteArray = bos.toByteArray();

		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInputStream ois = new ObjectInputStream(bis);
		T after = (T)ois.readObject();

		System.out.println(before == after);
	}
}
