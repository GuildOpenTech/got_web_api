package org.got.web.gotweb.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour convertir automatiquement une chaîne de caractères en minuscules
 * avant la persistance ou la mise à jour dans la base de données.
 * Utilisation :
 * {@code @ToLowerCase
 *  private String email;}
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToLowerCase {
}