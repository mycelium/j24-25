package ru.spbstu.lab;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Доступна в runtime через reflection
@Target(ElementType.TYPE) // Применяется только к классам
public @interface JsonDeserialize {
    /**
     * Класс кастомного десериализатора, который будет использоваться
     * для преобразования JSON в объект этого типа.
     */
    Class<? extends JsonDeserializer<?>> using();
}
