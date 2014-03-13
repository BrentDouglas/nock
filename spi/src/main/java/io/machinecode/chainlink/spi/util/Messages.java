package io.machinecode.chainlink.spi.util;

import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class Messages {

    private Messages(){}

    private static final ResourceBundle MESSAGES;

    static {
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("messages", Locale.getDefault(), Messages.class.getClassLoader());
        } catch (final Exception e) {
            bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH, Messages.class.getClassLoader());
        }
        MESSAGES = bundle;
    }

    public static String get(final String key) {
        return key.split("\\.")[0] + ": " + MESSAGES.getString(key);
    }

    public static String format(final String key, final Object... args) {
        return new Formatter().format(get(key), args).toString();
    }

    public static String raw(final String key) {
        return MESSAGES.getString(key);
    }
}