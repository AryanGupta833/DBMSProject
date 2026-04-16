/**
 * ANSI color constants for terminal output.
 * Usage:  System.out.println(Color.GREEN + "Success!" + Color.RESET);
 *
 * Colors are automatically disabled when output is not a terminal
 * (e.g. when redirected to a file), so logs stay clean.
 */
public class Color {
    private static final boolean ENABLED = System.console() != null;

    public static final String RESET   = ENABLED ? "\033[0m"     : "";
    public static final String BOLD    = ENABLED ? "\033[1m"     : "";
    public static final String DIM     = ENABLED ? "\033[2m"     : "";

    // Foreground
    public static final String BLACK   = ENABLED ? "\033[30m"    : "";
    public static final String RED     = ENABLED ? "\033[31m"    : "";
    public static final String GREEN   = ENABLED ? "\033[32m"    : "";
    public static final String YELLOW  = ENABLED ? "\033[33m"    : "";
    public static final String BLUE    = ENABLED ? "\033[34m"    : "";
    public static final String MAGENTA = ENABLED ? "\033[35m"    : "";
    public static final String CYAN    = ENABLED ? "\033[36m"    : "";
    public static final String WHITE   = ENABLED ? "\033[37m"    : "";

    // Bright foreground
    public static final String BRIGHT_RED    = ENABLED ? "\033[91m" : "";
    public static final String BRIGHT_GREEN  = ENABLED ? "\033[92m" : "";
    public static final String BRIGHT_YELLOW = ENABLED ? "\033[93m" : "";
    public static final String BRIGHT_BLUE   = ENABLED ? "\033[94m" : "";
    public static final String BRIGHT_CYAN   = ENABLED ? "\033[96m" : "";
    public static final String BRIGHT_WHITE  = ENABLED ? "\033[97m" : "";

    // Background
    public static final String BG_BLUE    = ENABLED ? "\033[44m"    : "";
    public static final String BG_GREEN   = ENABLED ? "\033[42m"    : "";
    public static final String BG_RED     = ENABLED ? "\033[41m"    : "";
    public static final String BG_CYAN    = ENABLED ? "\033[46m"    : "";

    private Color() {}
}