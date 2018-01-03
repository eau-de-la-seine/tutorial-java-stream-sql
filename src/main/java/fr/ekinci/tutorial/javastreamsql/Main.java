package fr.ekinci.tutorial.javastreamsql;

import fr.ekinci.dbmsreplication.inmemorysql.InMemorySQL;
import fr.ekinci.tutorial.javastreamsql.models.Employee;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Gokan EKINCI
 */
public class Main {
	public static void main(String[] args) throws Exception {
		// You can uncomment each of those exercise below :
		// ex1_a();
		// ex1_b();
		// ex2();
		// ex3_a();
		// ex3_b();
		// ex4();
		// ex5();
		// ex6();
		// ex7_a();
		// ex7_b();
		// ex8_a();
		// ex8_b();
		// ex9_a();
		// ex9_b();
	}

	/**
	 * Ex1: WHERE + ORDER BY + LIMIT with Stream version
	 *
	 * Top 3 richest men
	 *
	 *		SELECT *
	 *		FROM employees
	 *		WHERE gender = 'M'
	 *		ORDER BY salary DESC
	 *		LIMIT 3
	 */
	public static void ex1_a () {
		List<Employee> result = employees().stream()
			.filter(employee -> "M".equals(employee.getGender()))	// WHERE gender = 'M'
			.sorted((e1, e2) -> e2.getSalary() - e1.getSalary())	// ORDER BY salary DESC
			.limit(3)												// LIMIT X
			.collect(Collectors.toList());

		// Print
		System.out.println("Salary | Name");
		result.forEach(employee ->
			System.out.println(
				String.format("%s   | %s", employee.getSalary(), employee.getName())
			)
		);
	}

	/**
	 * Ex1: WITHOUT Stream version
	 */
	public static void ex1_b () {
		List<Employee> temp = new ArrayList<>();
		for (Employee employee : employees()) {
			if ("M".equals(employee.getGender())) { // WHERE gender = 'M'
				temp.add(employee);
			}
		}

		Collections.sort(temp, (e1, e2) -> e2.getSalary() - e1.getSalary()); // ORDER BY salary DESC
		int limit = (temp.size() > 0 && temp.size() <= 3) ? temp.size() : 3;
		List<Employee> result = (
			temp.isEmpty()) ? Collections.emptyList() : temp.subList(0, limit); // LIMIT X

		// Print
		System.out.println("Salary | Name");
		result.forEach(employee ->
			System.out.println(
				String.format("%s   | %s", employee.getSalary(), employee.getName())
			)
		);
	}

	/**
	 * Ex2: ORDER BY multiple fields
	 *
	 *		SELECT *
	 *		FROM employees
	 *		ORDER BY gender ASC, salary ASC, name ASC
	 */
	public static void ex2 () {
		List<Employee> result = employees().stream()
			.sorted(
				Comparator.comparing(Employee::getGender)	// ORDER BY gender ASC
					.thenComparing(Employee::getSalary)		// , salary ASC
					.thenComparing(Employee::getName)		// , name ASC
			)
			.collect(Collectors.toList());

		// Print
		System.out.println("Gender | Salary | Name");
		result.forEach(employee ->
				System.out.println(String.format("%s      | %s   | %s", employee.getGender(), employee.getSalary(), employee.getName()))
		);
	}

	/**
	 * Ex3: Aggregate functions: `MIN` + `MAX` + `AVG` + `COUNT` + `SUM`
	 *
	 * `MIN`: Youngest age
	 *		SELECT MIN(age) AS youngestAge FROM employees;
	 *
	 * `MAX`: Highest salary
	 *		SELECT MAX(salary) AS highestSalary FROM employees;
	 *
	 * `AVG`: Average age of employees
	 *		SELECT AVG(age) AS averageAge FROM employees;
	 *
	 * `COUNT`: Number of women
	 *		SELECT COUNT(*) AS womenCount FROM employees WHERE gender = 'F';
	 *
	 * `SUM`: Sum of salary with 21.7% taxes
	 *		SELECT SUM(salary) * 1.217 AS salarySumWithTaxes FROM employees;
	 */
	public static void ex3_a () {
		Optional<Integer> youngestAge = employees().stream()
			.map(Employee::getAge)
			.min(Integer::compare);		// MIN(age)

		Optional<Integer> highestSalary =  employees().stream()
			.map(Employee::getSalary)
			.max(Integer::compare);		// MAX(salary)

		OptionalDouble averageAge = employees().stream()
			.mapToInt(Employee::getAge)
			.average();					// AVG(age)

		long womenCount = employees().stream()
			.filter(employee -> "F".equals(employee.getGender()))	// WHERE gender = 'F'
			.count();												// COUNT(*)

		double salarySumWithTaxes = employees().stream()
			.mapToDouble(Employee::getSalary)
			.sum() * 1.217;							// SUM(salary) * 1.217

		// Print
		System.out.println("WITHOUT REDUCE()");
		System.out.println(String.format("Youngest Age                  : %s", youngestAge.orElse(null)));
		System.out.println(String.format("Highest salary                : %s", highestSalary.orElse(null)));
		System.out.println(String.format("Average age of employees      : %s", averageAge.orElse(0)));
		System.out.println(String.format("Number of women               : %s", womenCount));
		System.out.println(String.format("Sum of salary with 21.7%% taxes: %s", salarySumWithTaxes));
	}

	/**
	 * Ex3: with reduce() syntax
	 */
	public static void ex3_b () {
		Optional<Integer> youngestAge = employees().stream()
			.map(Employee::getAge)
			.reduce((a, b) -> (a < b) ? a : b);		// MIN(age)

		Optional<Integer> highestSalary =  employees().stream()
			.map(Employee::getSalary)
			.reduce((a, b) -> (a > b) ? a : b);		// MAX(age)

		// AVG => SUM() / COUNT(*)
		AtomicInteger count = new AtomicInteger(1);
		OptionalDouble sumAge = employees().stream()
			.mapToDouble(Employee::getAge)
			.reduce((a, b) -> {
				count.incrementAndGet();
				return a + b;
			});
		OptionalDouble averageAge = sumAge.isPresent() ?
			OptionalDouble.of(sumAge.getAsDouble() / count.get()) : OptionalDouble.empty();

		long womenCount = employees().stream()
			.filter(employee -> "F".equals(employee.getGender()))	// WHERE gender = 'F'
			.map(e -> 1L)
			.reduce(0L, (a, b) -> a + b);			// COUNT(*)

		double salarySumWithTaxes = employees().stream()
			.map(Employee::getSalary)
			.reduce(0, (a, b) -> (a + b)) * 1.217;	// SUM(salary) * 1.217

		// Print
		System.out.println("WITH REDUCE()");
		System.out.println(String.format("Youngest Age                  : %s", youngestAge.orElse(null)));
		System.out.println(String.format("Highest salary                : %s", highestSalary.orElse(null)));
		System.out.println(String.format("Average age of employees      : %s", averageAge));
		System.out.println(String.format("Number of women               : %s", womenCount));
		System.out.println(String.format("Sum of salary with 21.7%% taxes: %s", salarySumWithTaxes));
	}

	/**
	 * Ex4: MAX + GROUP BY
	 * Richest man and richest woman
	 *
	 *		SELECT gender, MAX(salary) AS salary
	 *		FROM employees
	 *		GROUP BY gender;
	 */
	public static void ex4 () {
		Map<String, Optional<Integer>> result = employees().stream()
			.collect(Collectors.groupingBy(
				Employee::getGender,														// GROUP BY gender
				Collectors.mapping(Employee::getSalary, Collectors.maxBy(Integer::compare))	// MAX(salary)
			));

		// Print
		result.entrySet().forEach(entry ->
			System.out.println(String.format("Gender: %s | Max Salary: %s", entry.getKey(), entry.getValue()))
		);
	}

	/**
	 * Ex5: AVG + GROUP BY
	 * Average salary of men and women
	 *
	 *		SELECT gender, AVG(salary) AS salary
	 *		FROM employees
	 *		GROUP BY gender;
	 */
	public static void ex5 () {
		Map<String, Double> result = employees().stream()
			.collect(Collectors.groupingBy(
				Employee::getGender,							// GROUP BY gender
				Collectors.averagingDouble(Employee::getSalary)	// AVG(salary)
			));

		// Print
		result.entrySet().forEach(entry ->
			System.out.println(String.format("Gender: %s | Average Salary: %s", entry.getKey(), entry.getValue()))
		);
	}

	/**
	 * Ex6: COUNT + GROUP BY
	 * Number of men and women
	 *
	 *		SELECT gender, COUNT(*) AS count
	 *		FROM employees
	 *		GROUP BY gender;
	 */
	public static void ex6 () {
		Map<String, Long> result = employees().stream()
			.collect(Collectors.groupingBy(
				Employee::getGender,		// GROUP BY gender
				Collectors.counting()		// COUNT(*)
			));

		// Print
		result.entrySet().forEach(entry ->
			System.out.println(String.format("Gender: %s | Count: %s", entry.getKey(), entry.getValue()))
		);
	}

	/**
	 * Ex7 (A): DISTINCT and GROUP BY
	 *
	 *		SELECT salary FROM employees GROUP BY salary;
	 *		-- Equivalent:
	 *		SELECT DISTINCT salary FROM employees;
	 */
	public static void ex7_a () {
		Map<Integer, List<Employee>> genders2 = employees().stream()
			.collect(Collectors.groupingBy(Employee::getSalary));		// GROUP BY

		// Print
		System.out.println(String.format("Genders: %s", genders2.entrySet().stream().map(Map.Entry::getKey).map(Object::toString).collect(Collectors.joining(", "))));
	}

	/**
	 * Ex7 (B): DISTINCT and GROUP BY
	 *
	 *		SELECT salary FROM employees GROUP BY salary;
	 *		-- Equivalent:
	 *		SELECT DISTINCT salary FROM employees;
	 */
	public static void ex7_b () {
		List<Integer> genders1 = employees().stream()
			.map(Employee::getSalary)
			.distinct()													// DISTINCT
			.collect(Collectors.toList());

		// Print
		System.out.println(String.format("Genders: %s", genders1.stream().map(Object::toString).collect(Collectors.joining(", "))));
	}

	/**
	 * Ex8: CROSS JOIN
	 *
	 * Cartesian product
	 *
	 *		SELECT t1.e AS x, t2.e AS y, t3.e AS z FROM t1 CROSS JOIN t2 CROSS JOIN t3;
	 *		-- Equivalent:
	 *		SELECT t1.e AS x, t2.e AS y, t3.e AS z FROM t1, t2, t3;
	 */
	public static void ex8_a () {
		String[] t1 = {"A", "B", "C"};
		String[] t2 = {"B", "C", "D"};
		String[] t3 = {"C", "F", "E"};

		// CROSS JOIN
		List<Container> list = Stream.of(t1)
			.flatMap(x -> Arrays.stream(t2)				// t1 CROSS JOIN t2
				.flatMap(y -> Arrays.stream(t3)			// t2 CROSS JOIN t3
					.map(z -> new Container(x, y, z))
				)
			)
			.collect(Collectors.toList());

		// Print
		list.forEach(System.out::println);
	}

	/**
	 * Ex8: with InMemorySQL
	 */
	public static void ex8_b () throws SQLException {
		List<Tuple> t1 = tupleInit("A", "B", "C");
		List<Tuple> t2 = tupleInit("B", "C", "D");
		List<Tuple> t3 = tupleInit("C", "D", "E");

		String request = "SELECT t1.e AS x, t2.e AS y, t3.e AS z FROM t1 CROSS JOIN t2 CROSS JOIN t3";
		new InMemorySQL()
			.add(Tuple.class, t1)
			.add(Tuple.class, t2)
			.add(Tuple.class, t3)
			.executeQuery(Container.class, request)
			.forEach(System.out::println);
	}

	/**
	 * Ex9: INNER JOIN
	 *
	 * 		SELECT t1.e AS x, t2.e AS y, t3.e AS z
	 * 		FROM t1
	 * 		INNER JOIN t2
	 * 		ON t1.e = t2.e
	 * 		INNER JOIN t3
	 * 		ON t2.e = t3.e;
	 */
	public static void ex9_a () {
		String[] t1 = {"A", "B", "C"};
		String[] t2 = {"B", "C", "D"};
		String[] t3 = {"C", "F", "E"};

		// CROSS JOIN
		List<Container> list = Stream.of(t1)
			.flatMap(x -> Arrays.stream(t2)				// t1 INNER JOIN t2
				.filter(y -> Objects.equals(x, y))		// ON t1.e = t2.e
				.flatMap(y -> Arrays.stream(t3)			// t2 INNER JOIN t3
					.filter(z -> Objects.equals(y, z))	// ON t2.e = t3.e
					.map(z -> new Container(x, y, z))
				)
			).collect(Collectors.toList());

		// Print
		list.forEach(System.out::println);
	}

	/**
	 * Ex9: with InMemorySQL
	 */
	public static void ex9_b () throws SQLException {
		List<Tuple> t1 = tupleInit("A", "B", "C");
		List<Tuple> t2 = tupleInit("B", "C", "D");
		List<Tuple> t3 = tupleInit("C", "D", "E");

		String request = "SELECT t1.e AS x, t2.e AS y, t3.e AS z " +
				"FROM t1 " +
				"INNER JOIN t2 " +
				"ON t1.e = t2.e " +
				"INNER JOIN t3 " +
				"ON t2.e = t3.e";
		new InMemorySQL()
			.add(Tuple.class, t1)
			.add(Tuple.class, t2)
			.add(Tuple.class, t3)
			.executeQuery(Container.class, request)
			.forEach(System.out::println);
	}

	public static List<Employee> employees() {
		return new ArrayList<Employee>(){{
			add(Employee.builder().gender("M").age(40).salary(9800).name("Bruce WAYNE").build());
			add(Employee.builder().gender("M").age(33).salary(1500).name("Clark KENT").build());
			add(Employee.builder().gender("M").age(23).salary(4500).name("Barry ALLEN").build());
			add(Employee.builder().gender("M").age(19).salary(2400).name("Wally WEST").build());
			add(Employee.builder().gender("M").age(28).salary(8000).name("Hal JORDAN").build());
			add(Employee.builder().gender("M").age(35).salary(9500).name("Oliver QUEEN").build());
			add(Employee.builder().gender("M").age(42).salary(4700).name("Ray PALMER").build());
			add(Employee.builder().gender("M").age(22).salary(3600).name("Victor Stone").build());
			add(Employee.builder().gender("M").age(27).salary(1500).name("John CONSTANTINE").build());
			add(Employee.builder().gender("M").age(65).salary(2600).name("J'onn J'ONZZ").build());
			add(Employee.builder().gender("M").age(28).salary(1400).name("Arthur CURRY").build());
			add(Employee.builder().gender("M").age(25).salary(2500).name("Dick GRAYSON").build());
			add(Employee.builder().gender("F").age(65).salary(6000).name("Diana PRINCE").build());
			add(Employee.builder().gender("F").age(24).salary(2500).name("Barbara GORDON").build());
			add(Employee.builder().gender("F").age(36).salary(1800).name("Selina KYLE").build());
			add(Employee.builder().gender("F").age(30).salary(2400).name("Pamela ISLEY").build());
			add(Employee.builder().gender("F").age(28).salary(2400).name("Harleen QUINZEL").build());
			add(Employee.builder().gender("F").age(29).salary(2000).name("Zatanna ZATARA").build());
		}};
	}

	public static class Tuple {
		String e;

		public Tuple(String e) {
			this.e = e;
		}
	}

	public static List<Tuple> tupleInit(String... values) {
		List<Tuple> t = new ArrayList<>();
		for (String v : values) {
			t.add(new Tuple(v));
		}
		return t;
	}

	public static class Container<T1, T2, T3> {
		public T1 x;
		public T2 y;
		public T3 z;

		public Container() {
		}

		public Container(T1 x, T2 y, T3 z){
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public String toString() {
			return x + " " + y + " " + z;
		}
	}
}
