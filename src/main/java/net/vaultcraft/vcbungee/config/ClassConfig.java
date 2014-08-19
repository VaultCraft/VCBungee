package net.vaultcraft.vcbungee.config;

import net.md_5.bungee.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * @author tacticalsk8er
 */
public class ClassConfig {

    /**
     * Use this to load a class with a specific config.
     *
     * @param configClass Class that contains fields with @Config annotations.
     * @param config      Config that is used to find values for the fields.
     */
    public static void loadConfig(Class<?> configClass, Configuration config) {
        for (Field f : configClass.getDeclaredFields()) {
            if (f.getAnnotation(Config.class) != null) {
                Config aConfig = f.getAnnotation(Config.class);
                try {
                    f.set(null, config.get(aConfig.path(), f.get(null)));
                } catch (IllegalAccessException | IllegalArgumentException ignored) {
                }
            }
        }
    }

    /**
     * Use this to update a config with the classes current field values.
     *
     * @param configClass Class that contains fields with @Config annotations.
     * @param config      Config that is used to updates values from the fields.
     */
    public static void updateConfig(Class<?> configClass, Configuration config) {
        for (Field f : configClass.getDeclaredFields()) {
            if (f.getAnnotation(Config.class) != null) {
                Config aConfig = f.getAnnotation(Config.class);
                try {
                    config.set(aConfig.path(), f.get(null));
                } catch (IllegalAccessException | IllegalArgumentException ignored) { }
            }
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Config {
        /**
         * @return Path for the config.
         */
        String path();
    }
}
