package org.springframework.stomp.client.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StompListeners {

    StompListener[] value();

}
