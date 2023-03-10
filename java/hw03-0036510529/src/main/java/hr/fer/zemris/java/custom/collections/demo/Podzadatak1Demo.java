package hr.fer.zemris.java.custom.collections.demo;



import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.collections.Collection;
import hr.fer.zemris.java.custom.collections.ElementsGetter;
import hr.fer.zemris.java.custom.collections.LinkedListIndexedCollection;
import hr.fer.zemris.java.custom.collections.List;

public class Podzadatak1Demo {

    public static void main(String[] args) {
//	Collection col1 = new ArrayIndexedCollection();
//	Collection col2 = new ArrayIndexedCollection();
//	col1.add("Ivo");
//	col1.add("Ana");
//	col1.add("Jasna");
//	col2.add("Jasmina");
//	col2.add("Štefanija");
//	col2.add("Karmela");
//	ElementsGetter getter1 = col1.createElementsGetter();
//	ElementsGetter getter2 = col1.createElementsGetter();
//	ElementsGetter getter3 = col2.createElementsGetter();
//	System.out.println("Jedan element: " + getter1.getNextElement());
//	System.out.println("Jedan element: " + getter1.getNextElement());
//	System.out.println("Jedan element: " + getter2.getNextElement());
//	System.out.println("Jedan element: " + getter3.getNextElement());
//	System.out.println("Jedan element: " + getter3.getNextElement());	

	List col1 = new ArrayIndexedCollection();
	List col2 = new LinkedListIndexedCollection();
	col1.add("Ivana");
	col2.add("Jasna");
	Collection col3 = col1;
	Collection col4 = col2;
	col1.get(0);
	col2.get(0);
//	col3.get(0); // neće se prevesti! Razumijete li zašto?
//	col4.get(0); // neće se prevesti! Razumijete li zašto?
	col1.forEach(System.out::println); // Ivana
	col2.forEach(System.out::println); // Jasna
	col3.forEach(System.out::println); // Ivana
	col4.forEach(System.out::println); // Jasna

    }

}
