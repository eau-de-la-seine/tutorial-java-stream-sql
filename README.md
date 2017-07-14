# Tutorial for learning Java Streams with SQL

Nine exercises for learning Java Stream


## Exercise 1: Basics: WHERE + ORDER BY + LIMIT

* A: Top 3 richest men with Java Stream
* B: Alternative syntax without Java Stream


    SELECT *
    FROM employees
    WHERE gender = 'M'
    ORDER BY salary DESC
    LIMIT 3


## Exercise 2: ORDER BY multiple fields

    SELECT *
    FROM employees
    ORDER BY gender ASC, salary ASC, name ASC


## Exercise 3: Aggregate functions: `MIN` + `MAX` + `AVG` + `COUNT` + `SUM`

* A: normal version
* B: `reduce()` version

`MIN`: Youngest age

    SELECT MIN(age) AS youngestAge FROM employees;

`MAX`: Highest salary

    SELECT MAX(salary) AS highestSalary FROM employees;

`AVG`: Average age of employees

    SELECT AVG(age) AS averageAge FROM employees;

`COUNT`: Number of women

    SELECT COUNT(*) AS womenCount FROM employees WHERE gender = 'F';

`SUM`: Sum of salary with 21.7% taxes

    SELECT SUM(salary) * 1.217 AS salarySumWithTaxes FROM employees;


## Exercise 4: MAX + GROUP BY

* Richest man and richest woman


    SELECT gender, MAX(salary) AS salary
    FROM employees
    GROUP BY gender;


## Exercise 5: AVG + GROUP BY

* Average salary of men and women


    SELECT gender, AVG(salary) AS salary
    FROM employees
    GROUP BY gender;


## Exercise 6: COUNT + GROUP BY

* Number of men and women


    SELECT gender, COUNT(*) AS count
    FROM employees
    GROUP BY gender;


## Exercise 7: DISTINCT & GROUP BY

    SELECT DISTINCT salary FROM employees;
    -- Equivalent:
    SELECT salary FROM employees GROUP BY salary;


## Exercise 8: CROSS JOIN

* A: Cartesian product
* B: Alternative syntax with `InMemorySQL` API


    SELECT t1.e AS x, t2.e AS y, t3.e AS z FROM t1 CROSS JOIN t2 CROSS JOIN t3;
    -- Equivalent:
    SELECT t1.e AS x, t2.e AS y, t3.e AS z FROM t1, t2, t3;


## Exercise 9: INNER JOIN

* A: Inner join
* B: Alternative syntax with `InMemorySQL` API


    SELECT t1.e AS x, t2.e AS y, t3.e AS z
    FROM t1
    INNER JOIN t2
    ON t1.e = t2.e
    INNER JOIN t3
    ON t2.e = t3.e;

