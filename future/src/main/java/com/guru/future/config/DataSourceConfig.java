package com.guru.future.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 默认关系型数据库druid数据源连接池配置
 */
@Configuration
@MapperScan(basePackages = DataSourceConstants.MYBATIS_MAPPER_PACKAGE, sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceConfig {

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource(Environment env) {
        return AbstractDruidConfig
                .initDruidDataSource(env, "spring.datasource.");
    }

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("dataSource") DataSource dataSource, Environment env) {
        return AbstractDruidConfig
                .getSqlSessionFactory(dataSource, "classpath:mybatis/mapper/**/*.xml");
    }

    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
