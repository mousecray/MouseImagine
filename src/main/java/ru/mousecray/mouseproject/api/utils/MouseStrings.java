package ru.mousecray.mouseproject.api.utils;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mouseproject.api.anno.Fast;
import ru.mousecray.mouseproject.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mouseproject.api.anno.Slowly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class MouseStrings {
    @Nullable @Fast
    public static String trimWith(@Nullable String str, boolean trimSpace, char target) {
        if (str == null) return null;

        int    len = str.length(), st = 0;
        char[] val = str.toCharArray();

        while ((st < len) && ((trimSpace && val[st] <= ' ') || val[st] == target)) ++st;
        while ((st < len) && ((trimSpace && val[len - 1] <= ' ') || val[len - 1] == target)) len--;
        return ((st > 0) || (len < val.length)) ? str.substring(st, len) : str;
    }

    @Nullable @Slowly
    public static String trimWith(@Nullable String str, boolean trimSpace, char... targets) {
        if (str == null) return null;

        int    len = str.length(), st = 0;
        char[] val = str.toCharArray();

        while ((st < len) && ((trimSpace && val[st] <= ' ') || MouseCollections.hasAny(val[st], targets))) ++st;
        while ((st < len) && ((trimSpace && val[len - 1] <= ' ') || MouseCollections.hasAny(val[len - 1], targets))) len--;
        return ((st > 0) || (len < val.length)) ? str.substring(st, len) : str;
    }

    @Nullable
    public static String subAndTrimWithTabs(@Nullable String str, @Nullable String rangeStart, @Nullable String rangeEnd) {
        if (str == null) return null;

        String result = str;
        if (rangeStart != null && result.contains(rangeStart)) {
            int last            = result.lastIndexOf(rangeStart);
            int lengthThreshold = last + rangeStart.length();
            if (lengthThreshold <= result.length()) result = result.substring(lengthThreshold);
            else result = "";
        }
        if (rangeEnd != null && result.contains(rangeEnd)) {
            int first = result.indexOf(rangeEnd);
            result = result.substring(0, first);
        }
        return trimWith(result, true, '\t');
    }

    public static List<String> splitWordsBaseOnLength(String input, @Nullable String delimiter, int maxLength) {
        if (StringUtils.isEmpty(input)) return Collections.emptyList();
        if (delimiter == null) delimiter = " ";

        String[]          words = input.split(" ");
        ArrayList<String> lines = new ArrayList<>();
        lines.add("");
        for (String word : words) {
            int    lineIndex = lines.size() - 1;
            String line      = lines.get(lineIndex);
            if (line.isEmpty()) lines.set(lineIndex, word);
            else if (word.length() + 1 <= (maxLength - line.length())) lines.set(lineIndex, line + " " + word);
            else lines.add(delimiter + word);
        }
        return lines;
    }

    public static int compare(@Nullable String val1, @Nullable String val2) {
        return val1 == null ? val2 == null ? 0 : -1 : val2 == null ? 1 : val1.compareTo(val2);
    }
}