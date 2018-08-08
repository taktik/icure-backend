package org.taktik.icure.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.taktik.icure.applications.utils.ClassScanner;
import org.taktik.icure.dao.CalendarItemTypeDAO;
import org.taktik.icure.entities.serializer.annotations.ConfigurableSerializer;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Configuration
public class DeserializerConfiguration {

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void initializeDeserializer() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassScanner scanner = new ClassScanner("org.taktik.icure.entities.serializer", JsonDeserializer.class);
        while(scanner.hasNext()){
            Class clazz = scanner.next();
            ConfigurableSerializer annotation = (ConfigurableSerializer)clazz.getAnnotation(ConfigurableSerializer.class);
            if(annotation != null) {
                Class beanClass = annotation.using();
                Method method = clazz.getMethod("initialize", beanClass);
                method.invoke(null, context.getBean(beanClass));
            }
        }
    }

}
