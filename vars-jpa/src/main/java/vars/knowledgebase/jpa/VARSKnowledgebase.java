package vars.knowledgebase.jpa;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, PARAMETER, METHOD, CONSTRUCTOR})
public @interface VARSKnowledgebase {
}