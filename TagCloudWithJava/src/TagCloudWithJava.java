import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Program that reads an input file from the user and outputs it into a HTML
 * file displaying the number of times a distinct word occurred within the file
 * and then displaying that word using a unique font accordingly to the its
 * number of occurrences and this time instead of doing it with OSU components
 * using JAVA FRAMEWORK like FileReader/BufferedReader and FileWriter/
 * BufferedWriter/PrintWriter components for all the file input and output
 *
 *
 * @author Ephratah Meskel and Krutin Shukla
 *
 */
public final class TagCloudWithJava {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudWithJava() {
    }

    private static final int minFont = 11;

    private static final int maxFont = 48;

    private static final int valueDist = 38;

    private static class aSorting
            implements Serializable, Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> s1,
                Map.Entry<String, Integer> s2) {

            int check = -(s1.getKey().compareToIgnoreCase(s2.getKey()));
            //make sure its actually equal
            if (check == 0) {
                check = -(s1.getValue().compareTo(s2.getValue()));
            }
            return check;
        }
    }

    private static class intSorting
            implements Serializable, Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> s1,
                Map.Entry<String, Integer> s2) {

            int check = -(s1.getValue().compareTo(s2.getValue()));
            //make sure its actually equal
            if (check == 0) {
                check = -(s1.getKey().compareToIgnoreCase(s2.getKey()));
            }
            return check;
        }
    }

    /**
     * generates a map with all the words and their counts.
     *
     * @param input
     *            the input stream
     * @param m
     *            words --> amount of times they've appeared
     * @param separatorSet
     *            a set filled with characters that are considered separators
     * @param separatorStr
     *            a string filled with all the seperators
     * @throws IOException
     * @updates m
     * @requires input is a valid text file, |input| > 0
     * @ensures for m, k--> words, v--> counts
     */
    private static void tokenizer(BufferedReader input, Map<String, Integer> m,
            Set<Character> separatorSet, String separatorStr)
            throws IOException {

        String currentLine = input.readLine();
        while (currentLine != null) {
            int position = 0;

            while (position < currentLine.length()) {
                String token = nextWordOrSeparator(currentLine, position,
                        separatorStr);
                char c = token.charAt(0);
                if (!separatorSet.contains(c)) {
                    //changes all tokens to be lowercase
                    token = token.toLowerCase();
                    if (m.containsKey(token)) {
                        int num = m.get(token) + 1;
                        m.remove(token);
                        m.put(token, num);
                    } else {
                        m.put(token, 1);
                    }

                }
                position += token.length();
            }

            currentLine = input.readLine();
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            String separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        boolean sep = separators.indexOf(text.charAt(position)) >= 0;
        int temp = position;
        while (temp < text.length()
                && (separators.indexOf(text.charAt(temp)) >= 0) == sep) {
            temp++;
        }
        return text.substring(position, temp);
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    public static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        int size = str.length();
        for (int i = 0; i < size; i++) {
            char tempStr = str.charAt(i);
            if (!charSet.contains(tempStr)) {
                charSet.add(tempStr);
            }

        }
    }

    /**
     * Generates a header and writes it to the output.
     *
     * @param out
     *            where the files output to
     * @param n
     *            amount of words that are going to be displayed
     * @param inLocation
     *            the name of the input file
     * @replaces map
     * @ensures words and their counts are printed to output accordingly
     */
    private static void header(String inLocation, PrintWriter out, int n) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\'en\'>");
        out.println("<head>");
        out.println("<title>");
        out.println("Top " + n + " words in " + inLocation);
        out.println("</title>");
        out.println("<link href=\"http://web.cse.ohio-state.edu/software/2231/"
                + "web-sw2/assignments/projects/tag-cloud-generator/data/"
                + "tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">\r\n"
                + "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("<body>");
        out.println("<h2> Top " + n + " words in " + inLocation + "</h2>");
        out.println("<div class ='cdiv'>");
        out.println("<p class='cbox'>");

    }

    /**
     * Generates a footer and writes it to the output.
     *
     * @param out
     *            where the files output to
     */
    private static void footer(PrintWriter out) {
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");

    }

    /**
     * Finds a fontsize value depending on the max, min, and current entry
     * value.
     *
     * @param num
     *            the map entries current value
     * @param max
     *            the max out of the map
     * @param min
     *            the min out of the map
     * @return string with the correct "f" string value
     */
    private static String font(int num, int max, int min) {
        int result = valueDist;
        if (max > min) {
            result *= (num - min);
            result /= (max - min);
            result += minFont;
        } else {
            result = maxFont;
        }
        return "f" + result;
    }

    /**
     * sorts the given map numerically using a comparator.
     *
     * @param map
     *            word <-counts
     * @replaces map
     * @ensures order is from greatest num to lowest num
     * @return list with numerical ordering
     */
    private static List<Entry<String, Integer>> integerSort(
            Map<String, Integer> map) {

        List<Map.Entry<String, Integer>> iS;
        iS = new ArrayList<Map.Entry<String, Integer>>();
        Set<Map.Entry<String, Integer>> pairs = map.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = pairs.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> element = iterator.next();
            iterator.remove();
            iS.add(element);
        }

        Comparator<Map.Entry<String, Integer>> order = new intSorting();
        iS.sort(order);
        return iS;
    }

    /**
     * Generates a body writes it to the output.
     *
     * @param map
     *            word <-counts
     * @param n
     *            amount of words that are going to be displayed
     * @param output
     *            where the files output to
     * @replaces map
     * @ensures words and their counts are printed to output accordingly
     */
    private static void body(PrintWriter output, int n,
            Map<String, Integer> map) {

        List<Map.Entry<String, Integer>> ints = integerSort(map);
        List<Map.Entry<String, Integer>> sorted;
        sorted = new ArrayList<Map.Entry<String, Integer>>();
        while (ints.size() > 0 && sorted.size() < n) {
            Map.Entry<String, Integer> temp = ints.get(0);
            ints.remove(0);
            sorted.add(temp);
        }

        int min = 1;
        int max = 1;
        if (sorted.size() > 0) {
            min = sorted.get(sorted.size() - 1).getValue();
            max = sorted.get(0).getValue();
        }
        Comparator<Map.Entry<String, Integer>> order = new aSorting();
        sorted.sort(order);

        for (Map.Entry<String, Integer> pair : sorted) {
            String f = font(pair.getValue(), max, min);
            output.println("<span style='cursor:default' class=" + f
                    + "title='count: " + pair.getValue() + "'>" + pair.getKey()
                    + "</span>");
        }
    }

    /**
     * Generates a tagcloud based on the input file and writes it to the output.
     *
     * @param map
     *            word <-counts
     * @param n
     *            amount of words that are going to be displayed
     * @param input
     *            where the files read to
     * @param output
     *            where the files output to
     * @param inLocation
     *            location of the input
     * @replaces map
     * @ensures printed file output with tagcloud
     */
    private static void tagCloud(Map<String, Integer> map, int n,
            BufferedReader input, PrintWriter output, String inLocation) {
        header(inLocation, output, n);
        body(output, n, map);
        footer(output);

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) throws IOException {

        //IO using java files
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));

        BufferedReader input = null;
        PrintWriter output = null;
        String inLocation = "";
        String outLocation = "";
        int numOfWords = 0;
        //check the system input stream
        System.out.println("Enter a valid file in location: ");
        while (input == null) {
            try {
                inLocation = in.readLine();
                input = new BufferedReader(new FileReader(inLocation));
            } catch (IOException err) {
                System.err
                        .println("Error reading file name, try again: " + err);
                return;
            }
        }
        //check reading the file
        try {
            input = new BufferedReader(new FileReader(inLocation));
        } catch (IOException err) {
            System.err.println("Error reading the input file: " + err);
            return;
        }

        //check the output file
        System.out.println("Enter a valid file out location: ");
        try {
            outLocation = in.readLine();
            output = new PrintWriter(
                    new BufferedWriter(new FileWriter(outLocation)));
        } catch (IOException err) {
            System.err.println("Error reading the sys. input stream: " + err);
            return;
        }

        //check the num of words
        System.out.println(
                "Enter the number of words you would like to tag cloud: ");
        try {
            numOfWords = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            System.err.println("Error parsing input to int");

        }

        //make sure if its less than 0, its 0
        if (numOfWords < 0) {
            numOfWords = 0;
        }

        //get pairs of words from input
        String separatorStr = ". ,:;'{][}|/><?!`~1234567890\"\r\n"
                + "            + \"@#$%^&*()-_=+";
        Set<Character> separatorSet = new HashSet<Character>();
        Map<String, Integer> map = new HashMap<String, Integer>();

        generateElements(separatorStr, separatorSet);
        try {
            tokenizer(input, map, separatorSet, separatorStr);
        } catch (IOException err) {
            output.println("Error reading the stream from file input");
        }

        //make sure numOfWords isn't greater than the words we have
        if (numOfWords > map.size()) {
            numOfWords = map.size();
        }

        tagCloud(map, numOfWords, input, output, inLocation);

        try {
            input.close();
        } catch (IOException err) {
            System.out.println("Error closing sys. input stream");
        }
        output.close();
    }

}
