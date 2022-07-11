//package com.mis.eoffice.dbConfigs;
//
//import java.util.HashMap;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//		entityManagerFactoryRef = "db3EntityManagerFactory",
//		transactionManagerRef = "db3TransactionManager",
//		basePackages = {"com.mis.eoffice.keycloakRepo"})
//public class DB3Config {
//
//
//	private static final Logger logger = Logger.getLogger(DB3Config.class);
//
//	@Autowired
//	private Environment env;
//
//	@Bean(name = "db3DataSource")
//	public DataSource getdataSource() {
//		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//		dataSourceBuilder.driverClassName(env.getProperty("db3.datasource.driverClassName"));
//		dataSourceBuilder.url(env.getProperty("db3.datasource.url"));
//		dataSourceBuilder.username(env.getProperty("db3.datasource.username"));
//		dataSourceBuilder.password(env.getProperty("db3.datasource.password"));
//		return dataSourceBuilder.build();
//	}
//
//	@Bean(name = "db3EntityManagerFactory")
//	public LocalContainerEntityManagerFactoryBean db3EntityManagerFactory() 
//	{
//		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//		entityManagerFactoryBean.setDataSource(getdataSource());
//		entityManagerFactoryBean.setPackagesToScan("com.mis.eoffice.modelKeycloak");
//		entityManagerFactoryBean.setPersistenceUnitName("db3");
//		HibernateJpaVendorAdapter vendorAdapter
//		= new HibernateJpaVendorAdapter();
//		entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
//		HashMap<String, Object> properties = new HashMap<>();
//		properties.put("spring.jpa.hibernate.ddl-auto",
//				env.getProperty("db3.jpa.hibernate.ddl-auto"));
//		properties.put("spring.jpa.properties.hibernate.dialect","org.hibernate.dialect.PostgreSQL10Dialect");
//		entityManagerFactoryBean.setJpaPropertyMap(properties);
//		return entityManagerFactoryBean;
//	}
//
//	@Bean(name = "db3TransactionManager")
//	public PlatformTransactionManager db3TransactionManager() {
//		JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(db3EntityManagerFactory().getObject());
//		return transactionManager;
//	}
//}
