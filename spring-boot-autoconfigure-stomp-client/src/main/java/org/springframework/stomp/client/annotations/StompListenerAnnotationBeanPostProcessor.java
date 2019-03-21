package org.springframework.stomp.client.annotations;

import asia.stampy.client.netty.ClientNettyMessageGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StompListenerAnnotationBeanPostProcessor implements MergedBeanDefinitionPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(StompListenerAnnotationBeanPostProcessor.class);

    private ClientNettyMessageGateway clientNettyMessageGateway;
    private BeanFactory beanFactory;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));


    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.nonAnnotatedClasses.clear();
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopInfrastructureBean || bean instanceof ClientNettyMessageGateway ||
                bean instanceof StompListenerEndpointRegistry) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)
                //&& AnnotationUtils.isCandidateClass(targetClass, StompListener.class)
        ) {
            Map<Method, Set<StompListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<Set<StompListener>>) method -> {
                        Set<StompListener> listenerMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(
                                method, StompListener.class, StompListeners.class);
                        return (!listenerMethods.isEmpty() ? listenerMethods : null);
                    });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("No @StompListener annotations found on bean type: " + targetClass);
                }
            }
            else {
                // Non-empty set of methods
                annotatedMethods.forEach((method, listeners) ->
                        listeners.forEach(listener -> processStompListener(listener, method, bean)));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(annotatedMethods.size() + " @StompListener methods processed on bean '" + beanName +
                            "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    private void processStompListener(StompListener listener, Method method, Object bean) {
        //TODO: register subscription for method on bean...

    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
