# JK-DB API
Light JDBC API for simplifying  database driven development with Java. It is straight forward approach with minimal required configurations and environment prepaption.

## Configurations:
By default , create file named  (jk-db.properties) in the current working directory, with the following contents:
	
	db-driver-name=com.mysql.jdbc.Driver
	db-url=jdbc:mysql://localhost:3306/app
	db-user=root
	db-password=123456

Note : the above file are optional and the above configurations are already the defaults , so no need to set it up :)

## Examples:
for full code of examples , please refre the source code and examples package
### Dumping table contents as string
	
		JKDefaultDao dao=new JKDefaultDao();
		String rows=dao.executeOutputQuery("SELECT * FROM employees", ",", "\n");
		System.out.println(rows);
### Execute data manipulation statement (insert,update or delete)

		JKDefaultDao dao=new JKDefaultDao();
		dao.executeUpdate("UPDATE employees SET salary=salary+5");
		
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
	
### Execute with the protection from sql injection using Updater

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
 
