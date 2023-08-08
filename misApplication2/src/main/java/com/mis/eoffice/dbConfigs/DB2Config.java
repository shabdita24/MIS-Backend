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
		entityManagerFactoryRef = "db2EntityManagerFactory",
		transactionManagerRef = "db2TransactionManager",
		basePackages = {"com.mis.eoffice.db2Repo"})
public class DB2Config {


	private static final Logger logger = Logger.getLogger(DB2Config.class); 

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("jasyptStringEncryptor")
	private StringEncryptor encryptor;

	@Bean(name = "db2DataSource")
	public DataSource getdataSource() {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(env.getProperty("db2.datasource.driverClassName"));
		dataSourceBuilder.url(env.getProperty("db2.datasource.url"));
		String encryptedPassword = env.getProperty("db2.datasource.password");
		String decryptedPassword = encryptor.decrypt(encryptedPassword);
		dataSourceBuilder.password(decryptedPassword);
		String encryptedUsername = env.getProperty("db2.datasource.username");
		String decryptedUsername = encryptor.decrypt(encryptedUsername);
		dataSourceBuilder.username(decryptedUsername);
		return dataSourceBuilder.build();
	}

	@Bean(name = "db2EntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean db2EntityManagerFactory() 
	{
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(getdataSource());
		entityManagerFactoryBean.setPackagesToScan("com.mis.eoffice.db2Models");
		entityManagerFactoryBean.setPersistenceUnitName("db2");
		HibernateJpaVendorAdapter vendorAdapter
		= new HibernateJpaVendorAdapter();
		entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
		System.out.println(env.getProperty("db2.jpa.hibernate.ddl-auto"));
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("spring.jpa.hibernate.ddl-auto",
				env.getProperty("db2.jpa.hibernate.ddl-auto"));
		properties.put("spring.jpa.properties.hibernate.dialect",
				env.getProperty("db.jpa.properties.hibernate.dialect"));
		//		properties.put("spring.jpa.database-platform","org.hibernate.dialect.Oracle12cDialect");

		entityManagerFactoryBean.setJpaPropertyMap(properties);
		return entityManagerFactoryBean;
	}

	@Bean(name = "db2TransactionManager")
	public JpaTransactionManager springTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(db2EntityManagerFactory().getObject());
		return transactionManager;
	}
}
