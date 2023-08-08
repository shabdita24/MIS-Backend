package com.mis.eoffice.dbConfigs;

import java.util.HashMap;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties
@EnableJpaRepositories(
		entityManagerFactoryRef = "db1EntityManagerFactory",
		transactionManagerRef = "db1TransactionManager",
		basePackages = {"com.mis.eoffice.db1Repo"})
public class DB1Config {

	private static final Logger logger = Logger.getLogger(DB1Config.class); 

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("jasyptStringEncryptor")
	private StringEncryptor encryptor;

	@Bean(name = "db1DataSource")
	public DataSource getdataSource() {
		try {
			DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
			dataSourceBuilder.driverClassName(env.getProperty("db1.datasource.driverClassName"));
			dataSourceBuilder.url(env.getProperty("db1.datasource.url"));
			String encryptedPassword = env.getProperty("db1.datasource.password");
			String decryptedPassword = encryptor.decrypt(encryptedPassword);
			dataSourceBuilder.password(decryptedPassword);
			String encryptedUsername = env.getProperty("db1.datasource.username");
			String decryptedUsername = encryptor.decrypt(encryptedUsername);
			dataSourceBuilder.username(decryptedUsername);
			return dataSourceBuilder.build();
		}
		catch(Exception ex)
		{
			logger.error("Unable To Connect to Database, Wrong Resources Mapped");
			return null;
		}
	}

	@Bean(name = "db1EntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean db1EntityManagerFactory() 
	{
		try {

			LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
			entityManagerFactoryBean.setDataSource(getdataSource());
			entityManagerFactoryBean.setPackagesToScan("com.mis.eoffice.db1Models");
			entityManagerFactoryBean.setPersistenceUnitName("db1");
			HibernateJpaVendorAdapter vendorAdapter
			= new HibernateJpaVendorAdapter();
			entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

			HashMap<String, Object> properties = new HashMap<>();
			properties.put("spring.jpa.hibernate.ddl-auto",
					env.getProperty("db1.jpa.hibernate.ddl-auto"));
			properties.put("spring.jpa.properties.hibernate.dialect",
					env.getProperty("db.jpa.properties.hibernate.dialect"));
			//			properties.put("spring.jpa.database-platform","org.hibernate.dialect.Oracle12cDialect");
			entityManagerFactoryBean.setJpaPropertyMap(properties);
			return entityManagerFactoryBean;
		}
		catch(Exception ex)
		{
			logger.error("Unable To Connect to Database");
			return null;
		}
	}

	@Bean(name = "db1TransactionManager")
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
