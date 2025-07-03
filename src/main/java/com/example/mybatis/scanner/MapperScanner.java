package com.example.mybatis.scanner;

import com.example.mybatis.annotation.*;
import com.example.mybatis.core.MyBatisConfiguration;
import com.example.mybatis.core.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.ResolvableType;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Mapper扫描器
 * 负责扫描带有@MyBatisMapper注解的接口并注册到Spring容器
 */
public class MapperScanner extends ClassPathBeanDefinitionScanner {

    private static final Logger logger = LoggerFactory.getLogger(MapperScanner.class);

    private final MyBatisConfiguration configuration;

    public MapperScanner(BeanDefinitionRegistry registry, MyBatisConfiguration configuration) {
        super(registry, false);
        this.configuration = configuration;
        logger.info("初始化MapperScanner，configuration: {}", configuration);
        // 添加注解过滤器，只扫描带有@MyBatisMapper注解的类
        addIncludeFilter(new AnnotationTypeFilter(MyBatisMapper.class));
    }

    /**
     * 扫描并注册Mapper
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        logger.info("开始扫描包: {}", java.util.Arrays.toString(basePackages));

        // 添加更详细的扫描日志
        for (String basePackage : basePackages) {
            logger.info("正在扫描包: {}", basePackage);
        }

        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        logger.info("实际扫描到的 Mapper Bean 定义: {}", beanDefinitions.size());

        if (beanDefinitions.isEmpty()) {
            logger.warn("在包 {} 中没有找到MyBatis Mapper", java.util.Arrays.toString(basePackages));
            // 添加更多调试信息
            logger.warn("请检查：1. 包路径是否正确 2. 接口是否添加了@MyBatisMapper注解 3. 类路径是否包含这些类");
        } else {
            logger.info("找到 {} 个Mapper接口", beanDefinitions.size());
            for (BeanDefinitionHolder holder : beanDefinitions) {
                logger.info("找到Mapper: {} -> {}", holder.getBeanName(), holder.getBeanDefinition().getBeanClassName());
            }
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    /**
     * 处理Bean定义
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();

            logger.info("处理Mapper: {}", beanClassName);

            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) {
                    cl = this.getResourceLoader().getClassLoader();
                }
                Class<?> mapperClass = Class.forName(beanClassName, false, cl);
                if (!mapperClass.isInterface()) {
                    logger.warn("跳过非接口类: {}", beanClassName);
                    return;
                }
                logger.debug("注册Mapper接口: {}", beanClassName);

                // 设置 FactoryBean
                definition.setBeanClass(com.example.mybatis.core.MapperProxyFactory.class);

                // 使用构造函数注入 mapperInterface 和 configuration，保证 getObjectType 能正确返回类型
                definition.getConstructorArgumentValues().clear();
                definition.getConstructorArgumentValues().addGenericArgumentValue(mapperClass);
                definition.getConstructorArgumentValues().addGenericArgumentValue(configuration);

                // 设置Bean角色为应用级，确保可参与自动注入
                definition.setRole(BeanDefinition.ROLE_APPLICATION);
                logger.info("应用级别");

                // 设置为自动装配候选
                definition.setAutowireCandidate(true);

                // 关闭延迟加载，保证启动时初始化
                definition.setLazyInit(false);

                // 重要：启用自动装配，这样sqlSession就能被正确注入
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

                // 关键：告诉Spring FactoryBean产生的Bean类型
                definition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, mapperClass);

                // 若 Spring 版本较低无法直接 setTargetType，可通过属性方式设置
                try {
                    // 反射调用以避免编译期依赖差异
                    Method m = definition.getClass().getMethod("setTargetType", ResolvableType.class);
                    m.invoke(definition, ResolvableType.forClass(mapperClass));
                } catch (Exception ignore) {
                    // 回退: 通过 ATTRIBUTE 保存
                    definition.setAttribute("targetType", mapperClass);
                }

                logger.info("BeanDefinition 配置完成: BeanClass={}, 构造参数数量={}, 属性数量={}, 自动装配模式={}",
                        definition.getBeanClassName(),
                        definition.getConstructorArgumentValues().getArgumentCount(),
                        definition.getPropertyValues().size(),
                        definition.getAutowireMode());

                // 注册到 configuration 中
                registerMapper(mapperClass);

            } catch (Exception e) {
                logger.error("处理Mapper定义失败: " + holder.getBeanName(), e);
            }
        }
    }


    /**
     * 注册Mapper到配置中
     */
    @SuppressWarnings("unchecked")
    private void registerMapper(Class<?> mapperClass) {
        if (!mapperClass.isInterface()) {
            logger.warn("Mapper类 {} 不是接口，跳过注册", mapperClass.getName());
            return;
        }

        logger.info("注册Mapper: {}", mapperClass.getName());

        // 只有当configuration不为null时才进行映射配置
        if (configuration != null) {
            // 添加到配置中
            configuration.addMapper((Class<Object>) mapperClass);

            // 解析Mapper接口中的方法并注册MappedStatement
            Method[] methods = mapperClass.getDeclaredMethods();
            logger.info("开始处理Mapper {} 的 {} 个方法", mapperClass.getName(), methods.length);

            for (Method method : methods) {
                registerMappedStatement(mapperClass, method);
            }

            logger.info("完成Mapper注册: {}", mapperClass.getName());
        } else {
            logger.error("Configuration为null，无法注册Mapper: {}", mapperClass.getName());
        }
    }

    /**
     * 注册映射语句
     */
    private void registerMappedStatement(Class<?> mapperClass, Method method) {
        String statementId = mapperClass.getName() + "." + method.getName();

        // 检查各种SQL注解
        if (method.isAnnotationPresent(Select.class)) {
            Select select = method.getAnnotation(Select.class);
            Class<?> returnType = getReturnType(method);
            MappedStatement statement = new MappedStatement(
                    statementId,
                    MappedStatement.SqlCommandType.SELECT,
                    select.value(),
                    returnType
            );
            configuration.addMappedStatement(statementId, statement);
            logger.debug("注册SELECT语句: {} -> {}", statementId, select.value());

        } else if (method.isAnnotationPresent(SelectIf.class)) {
            SelectIf selectIf = method.getAnnotation(SelectIf.class);
            Class<?> returnType = getReturnType(method);
            MappedStatement statement = new MappedStatement(
                    statementId,
                    MappedStatement.SqlCommandType.SELECT,
                    selectIf.value(),
                    returnType
            );
            configuration.addMappedStatement(statementId, statement);
            logger.debug("注册SelectIf语句: {} -> {}", statementId, selectIf.value());

        } else if (method.isAnnotationPresent(Insert.class)) {
            Insert insert = method.getAnnotation(Insert.class);
            MappedStatement statement = new MappedStatement(
                    statementId,
                    MappedStatement.SqlCommandType.INSERT,
                    insert.value(),
                    int.class
            );
            configuration.addMappedStatement(statementId, statement);
            logger.debug("注册INSERT语句: {} -> {}", statementId, insert.value());

        } else if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            MappedStatement statement = new MappedStatement(
                    statementId,
                    MappedStatement.SqlCommandType.UPDATE,
                    update.value(),
                    int.class
            );
            configuration.addMappedStatement(statementId, statement);
            logger.debug("注册UPDATE语句: {} -> {}", statementId, update.value());

        } else if (method.isAnnotationPresent(Delete.class)) {
            Delete delete = method.getAnnotation(Delete.class);
            MappedStatement statement = new MappedStatement(
                    statementId,
                    MappedStatement.SqlCommandType.DELETE,
                    delete.value(),
                    int.class
            );
            configuration.addMappedStatement(statementId, statement);
            logger.debug("注册DELETE语句: {} -> {}", statementId, delete.value());
        }
    }

    /**
     * 获取方法的返回类型
     * 如果是List<T>，返回T的类型
     */
    private Class<?> getReturnType(Method method) {
        Type returnType = method.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) returnType;
            Type[] actualTypes = paramType.getActualTypeArguments();
            if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                return (Class<?>) actualTypes[0];
            }
        }

        return method.getReturnType();
    }

    @Override
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        String className = metadataReader.getClassMetadata().getClassName();
        try {
            Class<?> clazz = Class.forName(className);
            boolean isInterface = clazz.isInterface();
            boolean hasAnnotation = clazz.isAnnotationPresent(MyBatisMapper.class);
            boolean isCandidate = isInterface && hasAnnotation;

            logger.debug("检查候选组件: {} -> isInterface={}, hasAnnotation={}, isCandidate={}",
                    className, isInterface, hasAnnotation, isCandidate);

            if (isCandidate) {
                logger.debug("找到候选Mapper组件(MetadataReader): {}", className);
            }
            return isCandidate;
        } catch (ClassNotFoundException e) {
            logger.debug("类未找到，跳过: {}", className);
            return false;
        }
    }

    @Override
    protected boolean isCandidateComponent(org.springframework.beans.factory.annotation.AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
} 