import java.util.List;

public class TableUtil {


    public static void printTable(List<String> headers, List<List<String>> rows) {
        int[] colWidths = new int[headers.size()];

        // Calculate max width for each column
        for (int i = 0; i < headers.size(); i++) {
            colWidths[i] = headers.get(i).length();
        }

        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                colWidths[i] = Math.max(colWidths[i], row.get(i).length());
            }
        }

        // for printing headerss
        printRow(headers, colWidths);
        printSeparator(colWidths);

        // for rows printing
        for (List<String> row : rows) {
            printRow(row, colWidths);
        }
    }


    private static void printRow(List<String> row, int[] colWidths) {
        for (int i = 0; i < row.size(); i++) {
            System.out.print(padRight(row.get(i), colWidths[i]) + " | ");
        }
        System.out.println();
    }


    private static void printSeparator(int[] colWidths) {
        for (int width : colWidths) {
            System.out.print("-".repeat(width) + "-+-");
        }
        System.out.println();
    }


    private static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}