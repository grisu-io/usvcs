package io.grisu.usvcs.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MicroService {
   String serviceQueue();
}
