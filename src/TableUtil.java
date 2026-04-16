import java.util.List;

/**
 * TableUtil — pretty-prints data as a bordered Unicode table.
 *
 * Key fix: ANSI escape sequences are stripped when measuring column widths
 * so that colored cells don't push borders out of alignment.
 */
public class TableUtil {

    public static void printTable(List<String> headers, List<List<String>> rows) {
        int cols = headers.size();
        int[] colWidths = new int[cols];

        // Measure using VISIBLE length (no ANSI codes)
        for (int i = 0; i < cols; i++) {
            colWidths[i] = visibleLen(headers.get(i));
        }
        for (List<String> row : rows) {
            for (int i = 0; i < Math.min(row.size(), cols); i++) {
                String cell = row.get(i) == null ? "\u2014" : row.get(i);
                colWidths[i] = Math.max(colWidths[i], visibleLen(cell));
            }
        }

        printBorderTop(colWidths);
        printHeaderRow(headers, colWidths);
        printBorderMid(colWidths);

        for (int r = 0; r < rows.size(); r++) {
            List<String> row = rows.get(r);
            // Alternating row dim tint
            String tint  = (r % 2 != 0) ? Color.DIM  : "";
            String reset = (r % 2 != 0) ? Color.RESET : "";
            printDataRow(row, colWidths, cols, tint, reset);
        }

        printBorderBot(colWidths);
        System.out.println(Color.DIM + "  " + rows.size() + " row(s)" + Color.RESET);
    }

    // ── Rows ─────────────────────────────────────────────────────────

    private static void printHeaderRow(List<String> headers, int[] w) {
        System.out.print(Color.BRIGHT_CYAN + "\u2502" + Color.RESET);
        for (int i = 0; i < headers.size(); i++) {
            String cell = headers.get(i);
            int pad = w[i] - visibleLen(cell);
            System.out.print(Color.BOLD + Color.BRIGHT_CYAN
                    + " " + cell + spaces(pad) + " "
                    + Color.RESET
                    + Color.BRIGHT_CYAN + "\u2502" + Color.RESET);
        }
        System.out.println();
    }

    private static void printDataRow(List<String> row, int[] w, int cols,
                                     String tint, String reset) {
        System.out.print(Color.BRIGHT_CYAN + "\u2502" + Color.RESET);
        for (int i = 0; i < cols; i++) {
            String cell = (i < row.size() && row.get(i) != null) ? row.get(i) : "\u2014";
            int pad = w[i] - visibleLen(cell);
            System.out.print(tint + " " + cell + spaces(pad) + " " + reset
                    + Color.BRIGHT_CYAN + "\u2502" + Color.RESET);
        }
        System.out.println();
    }

    // ── Borders ──────────────────────────────────────────────────────

    private static void printBorderTop(int[] w) {
        System.out.print(Color.BRIGHT_CYAN + "\u250c");
        for (int i = 0; i < w.length; i++) {
            System.out.print("\u2500".repeat(w[i] + 2));
            System.out.print(i < w.length - 1 ? "\u252c" : "\u2510");
        }
        System.out.println(Color.RESET);
    }

    private static void printBorderMid(int[] w) {
        System.out.print(Color.BRIGHT_CYAN + "\u251c");
        for (int i = 0; i < w.length; i++) {
            System.out.print("\u2500".repeat(w[i] + 2));
            System.out.print(i < w.length - 1 ? "\u253c" : "\u2524");
        }
        System.out.println(Color.RESET);
    }

    private static void printBorderBot(int[] w) {
        System.out.print(Color.BRIGHT_CYAN + "\u2514");
        for (int i = 0; i < w.length; i++) {
            System.out.print("\u2500".repeat(w[i] + 2));
            System.out.print(i < w.length - 1 ? "\u2534" : "\u2518");
        }
        System.out.println(Color.RESET);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /** Strip ANSI escape codes then return string length. */
    private static int visibleLen(String s) {
        if (s == null) return 1;
        return s.replaceAll("\033\\[[;\\d]*m", "").length();
    }

    private static String spaces(int n) {
        return n > 0 ? " ".repeat(n) : "";
    }
}