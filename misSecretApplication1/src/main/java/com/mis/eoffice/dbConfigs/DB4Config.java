package com.mis.eoffice.dbConfigs;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "db4EntityManagerFactory",
		transactionManagerRef = "db4TransactionManager",
		basePackages = {"com.mis.eoffice.db4Repo"})
public class DB4Config {
	private static final Logger logger = Logger.getLogger(DB4Config.class); 

@Autowired
private Environment env;

@Bean(name = "db4DataSource")
public DataSource getdataSource() {
	try {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(env.getProperty("db4.datasource.driverClassName"));
		dataSourceBuilder.url(env.getProperty("db4.datasource.url"));
		dataSourceBuilder.username(env.getProperty("db4.datasource.username"));
		dataSourceBuilder.password(env.getProperty("db4.datasource.password"));
		return dataSourceBuilder.build();
	}
	catch(Exception ex)
	{
		logger.error("Unable To Connect to Database, Wrong Resources Mapped");
		return null;
	}
}

@Bean(name = "db4EntityManagerFactory")
public LocalContainerEntityManagerFactoryBean db1EntityManagerFactory() 
{
	try {

		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(getdataSource());
		entityManagerFactoryBean.setPackagesToScan("com.mis.eoffice.db4Models");
		entityManagerFactoryBean.setPersistenceUnitName("db4");
		HibernateJpaVendorAdapter vendorAdapter
		= new HibernateJpaVendorAdapter();
		entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
		
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("spring.jpa.hibernate.ddl-auto",
				env.getProperty("db4.jpa.hibernate.ddl-auto"));
		properties.put("spring.jpa.properties.hibernate.dialect",
				env.getProperty("db.jpa.properties.hibernate.dialect"));
//		properties.put("spring.jpa.database-platform","org.hibernate.dialect.Oracle12cDialect");
		entityManagerFactoryBean.setJpaPropertyMap(properties);
		return entityManagerFactoryBean;
	}
	catch(Exception ex)
	{
		logger.error("Unable To Connect to Database");
		return null;
	}
}

@Bean(name = "db4TransactionManager")
public JpaTransactionManager springTransactionManager() {
	try 
	{

		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(db1EntityManagerFactory().getObject());
		return transactionManager;
	}
	catch(Exception ex)
	{
		logger.error("Unable To Connect to Database, Check Database Connections");
		return null;
	}
}
}
