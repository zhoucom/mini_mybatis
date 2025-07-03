package com.example.mybatis.config;

import com.example.mybatis.cache.CacheConfig;
import com.example.mybatis.core.DefaultSqlSessionFactory;
import com.example.mybatis.core.MyBatisConfiguration;
import com.example.mybatis.core.SqlSession;
import com.example.mybatis.core.SqlSessionFactory;
import com.example.mybatis.scanner.MapperScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * MyBatis自动配置类
 * 负责自动装配MyBatis相关组件
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(MyBatisProperties.class)
@Import(MyBatisAutoConfiguration.MapperScannerRegistrar.class)
public class MyBatisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public MyBatisConfiguration mybatisConfiguration(@Lazy DataSource dataSource, MyBatisProperties properties) {
        MyBatisConfiguration configuration = new MyBatisConfiguration();
        configuration.setDataSource(dataSource);

        CacheConfig cacheConfig = new CacheConfig(
                properties.isSecondLevelCacheEnabled(),
                properties.getCacheExpireTime(),
                properties.getMaxCacheSize()
        );
        cacheConfig.setFirstLevelCacheEnabled(properties.isFirstLevelCacheEnabled());
        configuration.setCacheConfig(cacheConfig);

        return configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(MyBatisConfiguration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSession sqlSession(SqlSessionFactory sqlSessionFactory) {
        return sqlSessionFactory.openSession();
    }

        // 创建SqlSession实例
        // SqlSession是MyBatis的核心接口
        // 负责执行SQL语句
        // SqlSession的实现类由SqlSessionFactory提供
    public static class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ApplicationContextAware {

        private static final Logger logger = LoggerFactory.getLogger(MapperScannerRegistrar.class);
        private BeanFactory beanFactory;
        private ApplicationContext applicationContext;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            // 1. 读取 application.properties 中的 mybatis.mapper-locations 配置
            //    该配置指定了需要被扫描的Mapper接口所在的包
            logger.info("开始注册 Mapper Bean 定义...");

            MyBatisProperties properties = beanFactory.getBean(MyBatisProperties.class);
            logger.info("获取到 MyBatisProperties: {}", properties);

            // 2. 获取 MyBatisConfiguration对象
            MyBatisConfiguration configuration = beanFactory.getBean(MyBatisConfiguration.class);
            logger.info("获取到 MyBatisConfiguration: {}", configuration);

            // 3. 读取需要被扫描的Mapper接口的包
            String locations = properties.getMapperLocations();
            if (!StringUtils.hasText(locations)) {
                locations = "com.example.mybatis.mapper";
            }

            // 4. 创建MapperScanner对象
            //    该对象负责扫描Mapper接口并将其注册到Spring容器中
            MapperScanner scanner = new MapperScanner(registry, configuration);
            scanner.setResourceLoader(applicationContext);

            // 5. 遍历需要被扫描的Mapper接口的包
            logger.info("扫描 Mapper 包: {}", locations);
            for (String basePackage : locations.split(",")) {
                basePackage = basePackage.trim();
                if (StringUtils.hasText(basePackage)) {
                    logger.info("开始扫描包: {}", basePackage);
                    // 6. 扫描Mapper接口并注册到Spring容器中
                    scanner.scan(basePackage);
                }
            }
            logger.info("Mapper 扫描完成");
        }
    }
}