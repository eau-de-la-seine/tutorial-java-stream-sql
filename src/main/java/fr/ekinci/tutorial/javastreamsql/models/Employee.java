package fr.ekinci.tutorial.javastreamsql.models;

import lombok.Builder;
import lombok.Data;

/**
 * @author Gokan EKINCI
 */
@Data
@Builder
public class Employee {
	private String gender;
	private int age;
	private int salary;
	private String name;
}
