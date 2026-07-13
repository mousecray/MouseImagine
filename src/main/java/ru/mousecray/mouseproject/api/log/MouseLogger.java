package ru.mousecray.mouseproject.api.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class MouseLogger {
    private final Logger logger;
    private final String prefixPattern, prefixColors;

    public MouseLogger(Logger logger, String prefixPattern, ConsoleColor... prefixColors) {
        this.logger = logger;
        this.prefixPattern = prefixPattern;
        this.prefixColors = ConsoleColor.getColorString(prefixColors);
    }

    public void log(String text, Level level, ConsoleColor... colors) {
        logger.log(level, "{0}{1}{2}", ConsoleColor.getColorString(colors), text, ConsoleColor.RESET);
    }

    public void log(String text, String prefix, Level level, ConsoleColor... colors) {
        logger.log(level, MessageFormat.format(
                "{0}{1}{2} {3}{4}{5}", prefixColors, prefixPattern.replace("${value}", prefix),
                ConsoleColor.RESET, ConsoleColor.getColorString(colors), text, ConsoleColor.RESET
        ));
    }

    public void info(String text, ConsoleColor... colors) {
        logger.info("{0}{1}{2}", ConsoleColor.getColorString(colors), text, ConsoleColor.RESET);
    }

    public void info(String text, String prefix, ConsoleColor... colors) {
        logger.info(MessageFormat.format(
                "{0}{1}{2} {3}{4}{5}", prefixColors, prefixPattern.replace("${value}", prefix),
                ConsoleColor.RESET, ConsoleColor.getColorString(colors), text, ConsoleColor.RESET
        ));
    }

    public void warn(String text, ConsoleColor... colors) {
        logger.warn("{0}{1}{2}", ConsoleColor.getColorString(colors), text, ConsoleColor.RESET);
    }

    public void warn(String text, String prefix, ConsoleColor... colors) {
        logger.warn(MessageFormat.format(
                "{0}{1}{2} {3}{4}{5}", prefixColors, prefixPattern.replace("${value}", prefix),
                ConsoleColor.RESET, ConsoleColor.getColorString(colors), text, ConsoleColor.RESET
        ));
    }

    public void error(String text, ConsoleColor... colors) {
        logger.error("{0}{1}{2}", ConsoleColor.getColorString(colors), text, ConsoleColor.RESET);
    }

    public void error(String text, String prefix, ConsoleColor... colors) {
        logger.error(MessageFormat.format(
                "{0}{1}{2} {3}{4}{5}", prefixColors, prefixPattern.replace("${value}", prefix),
                ConsoleColor.RESET, ConsoleColor.getColorString(colors), text, ConsoleColor.RESET
        ));
    }

    public void fatal(String text, Exception e) {
        logger.fatal("{0}{1}{2}{4}Exception: {5}{6}Stack trace: {7}",
                ConsoleColor.getColorString(ConsoleColor.RED_BG), text,
                ConsoleColor.RESET, System.lineSeparator(), e.getLocalizedMessage(),
                System.lineSeparator(), e.getStackTrace());
    }

    public void fatal(String text, String prefix, Exception e) {
        logger.fatal("{0}{1}{2} {3}{4}{5}Exception: {6}{7}Stack trace: {8}",
                prefixColors, prefixPattern.replace("${value}", prefix),
                ConsoleColor.getColorString(ConsoleColor.RED_BG), text, ConsoleColor.RESET,
                System.lineSeparator(), e.getLocalizedMessage(),
                System.lineSeparator(), e.getStackTrace());
    }

    public void debug(String text) { logger.debug(text); }
}