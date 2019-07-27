package com.fih.aiovpoint.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    @Primary
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        return dataSource;
    }

    @Bean
    @Primary
    public DataSourceTransactionManager dataSourceTransactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        List<Resource> resources = new ArrayList<>();
        for(Resource r : new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/*.xml")) {
            resources.add(r);
        }
        sessionFactory.setMapperLocations(resources.toArray(new Resource[resources.size()]));
        return sessionFactory.getObject();
    }
}
