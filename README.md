# JK-DB API
Light JDBC API for simplifying database driven development with Java. It is straight forward approach with minimal required configurations and environment preparation.

## Usage : 
1- Create new maven project.  
2- Add JK-DB dependency to your `pom.xml` inside the dependencies sections 

		<dependency>
			<groupId>com.jalalkiswani</groupId>
			<artifactId>jk-db</artifactId>
			<version>0.0.3</version>
		</dependency>
    
3- Be sure to set the minimum JDK level in your pom file to 1.7 by adding the following sections inside `build-->plugins` section :

	<plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.3</version>
        <configuration>
          <!-- http://maven.apache.org/plugins/maven-compiler-plugin/ -->
          <source>1.7</source>
          <target>1.7</target>
        </configuration>
      </plugin>   	    
	
4- Create configurations named `jk-db.properties` in project working directory, with the following contents:
	
	db-driver-name=com.mysql.jdbc.Driver
	db-url=jdbc:mysql://localhost:3306/app
	db-user=root
	db-password=123456

__Note:  the above config file is optional and also the above configurations are already the defaults, so no need to set it up. :)__ 
Thats it , now you can start us the API , have a look at the example sections for more details on the API 	

## JK-DB in web-applications
 You can use JK-DB in web-applications as well , just place the `jk-db.properties` file inside `/src/main/webapp/WEB-INF/` folder.
  
## Examples:
for full code of examples , please refer the source code and examples package

To be able to run the examples , create table in the database with the below structure:

	CREATE TABLE `employees` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `name` varchar(255) DEFAULT NULL,
	  `salary` double DEFAULT NULL,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;

### Dumping table contents as string
	
		JKDefaultDao dao=new JKDefaultDao();
		String rows=dao.executeOutputQuery("SELECT * FROM employees", ",", "\n");
		System.out.println(rows);
		
### Execute data manipulation statement (insert,update or delete)

		JKDefaultDao dao=new JKDefaultDao();
		dao.executeUpdate("UPDATE employees SET salary=?",1000);
		
### Execute single output query
	
		JKDefaultDao dao=new JKDefaultDao();
		Object max =  dao.exeuteSingleOutputQuery("SELECT MAX(salary) FROM employees");
		System.out.println(max);
		
### Exeute query and get the results as array:

		JKDefaultDao dao=new JKDefaultDao();
		Object[] allRows = dao.exeuteQueryAsArray("SELECT * FROM employees");
		for (Object record: allRows) {
			Object[] row=(Object[])record;
			System.out.println(String.format("Id: %s , Name: %s, Salary: %s",row[0],row[1],row[2]));
		}

### Get System date from database

		JKDefaultDao dao=new JKDefaultDao();
		System.out.println(dao.getSystemDate());

### Execute query and load object using Finder

		public Employee findEmployee(final int id) {
		return dao.findRecord(new Finder() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}

			@Override
			public Employee populate(ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException {
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
		return emp;
	}

## Execute query and load the results into List of objects using Finder

		public List getAllEmployees() {
		return dao.lstRecords(new Finder() {

			@Override
			public void setParamters(PreparedStatement ps) throws SQLException {
			}

			@Override
			public Employee populate(ResultSet rs) throws SQLException, JKRecordNotFoundException, JKDaoException {
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
		return dao.executeUpdate(new Updater() {

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
	
##Class diagram
![alt tag](https://github.com/kiswanij/jk-db/blob/master/design/jk-db-class-diagram-jalal-kiswani.PNG)

Enjoy!  
Jalal   
http://www.jalalkiswani.com

 
