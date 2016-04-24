# JK-DB API
Light Java database API (Plain JDBC and JPA) for simplifying database driven development with Java. It is straight forward approach with minimal required configurations and environment preparation.

## Features:
1. Plain JDBC support
2. ORM support based on JPA standards with hibernate as implementation.
3. Transparent connection pooling support (Based on Apache DBCP)
4. Support for any environment (Web or Desktop) 
5. Unified configurations for JDBC and ORM through unified simple config file (No need for persisitnce.xml or hibernate config)
6. straight forward API

## Usage : 
1- Create new maven project.  
2- Add JK-DB dependency to your `pom.xml` inside the dependencies sections 

		<dependency>
			<groupId>com.jalalkiswani</groupId>
			<artifactId>jk-db</artifactId>
			<version>0.0.4</version>
		</dependency>
    
3- Be sure to set the minimum JDK level in your pom file to 1.7 by adding the following sections inside `build-->plugins` section :

	<plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.3</version>
        <configuration>
          <source>1.7</source>
          <target>1.7</target>
        </configuration>
      </plugin>   	    
###Important for eclipse users: 
After you add the above section for Java version , it is important it refresh maven projects by `right click on the project-->Maven-->Update Project`
	
4- Create configurations named `jk-db.properties` in project working directory, with the following contents:
	
	db-driver-name=com.mysql.jdbc.Driver
	db-url=jdbc:mysql://localhost:3306/app
	db-user=root
	db-password=123456
	# The below used to for JPA entities packages for auto. scanning of entities
	# db-entities-packages=com.jk

__Note:  the above config file is optional and also the above configurations are already the defaults, so no need to set it up. :)__ 
Thats it , now you can start us the API , have a look at the example sections for more details on the API 	

## JK-DB in web-applications
 You can use JK-DB in web-applications as well , just place the `jk-db.properties` file inside `/src/main/webapp/WEB-INF/` folder.
#Examples  
## Plain JDBC Examples:
for full code of examples , please refer the source code and examples package

To be able to run the examples , create table in the database with the below structure:

	CREATE TABLE `employees` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `name` varchar(255) DEFAULT NULL,
	  `salary` double DEFAULT NULL,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;

### Dumping table contents as string
	
		JKPlainDataAccess dataAccess = JKDataSourceFactory.getPlainDataAccess();
		String rows = dataAccess.executeQueryAsString("SELECT * FROM employees");
		System.out.println(rows);
		
### Execute data manipulation statement (insert,update or delete)

		JKPlainDataAccess dataAccess = JKDataSourceFactory.getPlainDataAccess();
		dataAccess.execute("DELETE FROM employees");
		dataAccess.execute("INSERT INTO employees(id,name,salary) VALUES(?,?,?)", id, name, salary);		
		dataAccess.execute("UPDATE employees SET salary=? WHERE id=?", newSalary, id);

### Find row by id and populate in object

		String query = "SELECT * FROM employees WHERE id=? ";
		String instanceVariables = "id,name,salary";		
		Employee emp = dataAccess.executeQueryAsSingleObject(Employee.class, instanceVariables, query, id);

### Find rows list and populate in List of objects

		JKPlainDataAccess dataAccess = JKDataSourceFactory.getPlainDataAccess();
		String instanceVariables = "id,name,salary";
		String query = "SELECT * FROM employees ";		
		List<Employee> list = dataAccess.executeQueryAsObjectList(Employee.class, instanceVariables, query);		
	
### Execute single output query
	
		JKPlainDataAccess dataAccess = JKDataSourceFactory.getPlainDataAccess();
		Object max =  dataAccess.exeuteSingleOutputQuery("SELECT MAX(salary) FROM employees");
		System.out.println(max);
		
### Execute query and get the results as array:

		JKPlainDataAccess dataAccess = JKDataSourceFactory.getPlainDataAccess();
		Object[] allRows = dao.exeuteQueryAsArray("SELECT * FROM employees");
		for (Object record: allRows) {
			Object[] row=(Object[])record;
			System.out.println(String.format("Id: %s , Name: %s, Salary: %s",row[0],row[1],row[2]));
		}

### Get System date from database to insure integrity of the system

		JKPlainDataAccess dataAccess = JKDataSourceFactory.getPlainDataAccess();
		System.out.println(dao.getSystemDate());

### Advanced 
### Execute query and load object using JKFinder

		public Employee findEmployee(final int id) {
		return dataAccess.findRecord(new JKFinder() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}

			@Override
			public Employee populate(ResultSet rs) throws SQLException {
				Employee emp = new Employee();
				emp.setId(rs.getInt("id"));
				emp.setName(rs.getString("name"));
				emp.setSalary(rs.getDouble("salary"));
				return emp;
			}

			@Override
			public String getFinderSql() {
				return "SELECT * FROM employees WHERE id=?";
			}
		});
	}

### Execute query and load the results into List of objects using JKFinder

		public List getAllEmployees() {
		return dataAccess.getList(new JKFinder() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
			}

			@Override
			public Employee populate(ResultSet rs) throws SQLException{
				Employee emp = new Employee();
				emp.setId(rs.getInt("id"));
				emp.setName(rs.getString("name"));
				emp.setSalary(rs.getDouble("salary"));
				return emp;
			}

			@Override
			public String getFinderSql() {
				return "SELECT * FROM employees";
			}
		});
	}
	
### Execute data-manipulation using using Updater

		public  int insert(final Employee emp) {
		return dataAccess.executeUpdate(new JKUpdater() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
				int counter=1;
				ps.setString(counter++, emp.getName());
				ps.setDouble(counter++, emp.getSalary());
			}

			@Override
			public String getUpdateSql() {
				return "INSERT INTO employees (name,salary) VALUES(?,?)";
			}
		});
	}
	
## ORM Examples:
*Coming soon...*

##Class diagram
![alt tag](https://github.com/kiswanij/jk-db/blob/master/design/jk-db-class-diagram-jalal-kiswani.PNG)

Enjoy!  
Jalal   
http://www.jalalkiswani.com

 
