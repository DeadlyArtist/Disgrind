package disgrind.utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;

import java.util.*;

public class BookUtils {
    public static String getInitialText(ItemStack book) {
        WrittenBookContentComponent writtenBookContentComponent = book.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        WritableBookContentComponent writableBookContentComponent = book.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);

        // Get text from first page
        String text;
        if (writtenBookContentComponent != null)  {
            var pages = writtenBookContentComponent.pages();
            if (pages.isEmpty()) return "";
            text = pages.get(0).raw().getString();
        } else if (writableBookContentComponent != null) {
            var pages = writableBookContentComponent.pages();
            if (pages.isEmpty()) return "";
            text = pages.get(0).raw();
        } else {
            return "";
        }

        return text;
    }

    public static Optional<Integer> parseIndex(String text, int maxIndex) {
        try {
            if (text.startsWith("^")) {
                // Inverse index handling
                String number = text.substring(1).replaceAll("[^0-9]", "");
                if (number.isEmpty()) return Optional.empty();

                int inverse = Integer.parseInt(number);
                int index = maxIndex - (inverse - 1);

                if (index < 0 || index > maxIndex) return Optional.empty();
                return Optional.of(index);
            } else {
                // Normal index from 1-based user input
                String sanitized = text.replaceAll("[^0-9]", "");
                if (sanitized.isEmpty()) return Optional.empty();

                int parsed = Integer.parseInt(sanitized) - 1; // Convert to zero-based index
                if (parsed < 0 || parsed > maxIndex) return Optional.empty();
                return Optional.of(parsed);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static List<Integer> parseIndices(String text, int maxIndex) {
        List<Integer> indices = new ArrayList<>();
        String[] parts = text.split(",");
        String first = parts[0].trim();

        // Wildcard mode with exclusions
        if (first.equals("*")) {
            // Fill HashSet with excluded indices
            Set<Integer> excluded = new HashSet<>();
            for (int i = 1; i < parts.length; i++) {
                var mayExclude = parseIndex(parts[i].trim(), maxIndex);
                if (mayExclude.isEmpty()) continue;
                var exclude = mayExclude.get();
                excluded.add(exclude);
            }

            // Add all allowed indices except exclusions
            for (int i = 0; i <= maxIndex; i++) {
                if (!excluded.contains(i)) {
                    indices.add(i);
                }
            }

        } else {
            // Manual index selection
            Set<Integer> added = new HashSet<>();

            for (String part : parts) {
                var mayIndex = parseIndex(part.trim(), maxIndex);
                if (mayIndex.isEmpty()) continue;
                var index = mayIndex.get();
                if (added.add(index)) indices.add(index);
            }
        }
        if (indices.isEmpty()) indices.add(maxIndex);

        return indices;
    }
}
