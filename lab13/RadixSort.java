/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra
 * @version 1.4 - April 14, 2016
 *
 **/
public class RadixSort
{

    /**
     * Does Radix sort on the passed in array with the following restrictions:
     *  The array can only have ASCII Strings (sequence of 1 byte characters)
     *  The sorting is stable and non-destructive
     *  The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     **/
    public static String[] sort(String[] asciis) {
        int maxLength = -1;
        String[] dummy = new String[asciis.length];
        System.arraycopy(asciis, 0, dummy, 0, asciis.length);
        for (String s : asciis) {
            if (s.length() > maxLength) {
                maxLength = s.length();
            }
        }
        maxLength -= 1;
        while (maxLength >= 0) {
            int[] counter = getCounts(dummy, maxLength);
            dummy = reBuild(dummy, counter, maxLength);
            maxLength -= 1;
        }

        return dummy;
    }

    private static int[] getCounts(String[] input, int index) {
        int[] counter = new int[256];
        int indo = 0;

        while (indo < input.length) {
            if (input[indo].length() == 0) {
                counter[0] += 1;
            } else if (input[indo].length() <= index) {
                counter[0] += 1;
            } else {
                int loc = (int) input[indo].charAt(index);
                counter[loc] += 1;
            }
            indo += 1;
        }

        int ind = 1;
        while (ind < 256) {
            counter[ind] += counter[ind - 1];
            ind += 1;
        }
        return counter;
    }

    private static String[] reBuild(String[] input, int[] counter, int index) {
        String[] sorted = new String[input.length];
        int reverseCounter = input.length - 1;
        while (reverseCounter >= 0) {
            int value;
            if (input[reverseCounter].length() == 0) {
                value = 0;
            } else if (input[reverseCounter].length() <= index) {
                value = 0;
            } else {
                value = (int) input[reverseCounter].charAt(index);
            }
            counter[value] -= 1;
            int putHere = counter[value];
            sorted[putHere] = new String(input[reverseCounter]);
            reverseCounter -= 1;
        }
        return sorted;
    }

    /**
     * Radix sort helper function that recursively calls itself to achieve the sorted array
     *  destructive method that changes the passed in array, asciis
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    //private static void sortHelper(String[] asciis, int start, int end, int index)
    //{
        //TODO use if you want to
    //}
}
