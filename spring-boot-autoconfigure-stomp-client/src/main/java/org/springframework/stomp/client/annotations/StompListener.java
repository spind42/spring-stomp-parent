package org.springframework.stomp.client.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Repeatable(StompListeners.class)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@MessageMapping
public @interface StompListener {

    @AliasFor("pattern")
    public String value();

    @AliasFor("value")
    public String pattern();

    //TODO: also make used stomp client configureable

}
