package ru.lab.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Аннотация для связывания параметров конструктора с ключами в Map.
 * <p>
 * <b>Применение:</b> Помечайте параметры конструктора, чтобы указать,
 * какое значение из JSON должно быть передано.
 * </p>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParamName {
    String value();
}
