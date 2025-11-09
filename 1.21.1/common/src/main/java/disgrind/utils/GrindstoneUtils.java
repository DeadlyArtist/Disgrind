package disgrind.utils;

import disgrind.ModConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GrindstoneScreenHandler;

import java.util.*;
import java.util.stream.Collectors;

public class GrindstoneUtils {

    public static int getLevelCost() {
        return ModConfig.get().levelsRequired;
    }

    public static boolean canAdditionallyInsertIntoStack1(ItemStack stack) {
        return false;
    }

    public static boolean canAdditionallyInsertIntoStack2(ItemStack stack) {
        return isAllowedBook(stack);
    }

    public static boolean isAllowedBook(ItemStack stack) {
        return stack.isOf(Items.BOOK) || stack.isOf(Items.WRITABLE_BOOK) || stack.isOf(Items.WRITTEN_BOOK);
    }

    public static boolean isSpecialTransfer(ItemStack slot1, ItemStack slot2) {
        return EnchantmentHelper.hasEnchantments(slot1) && slot1.getCount() == 1 && isAllowedBook(slot2);
    }

    public static boolean isSpecialTransfer(GrindstoneScreenHandler handler) {
        return isSpecialTransfer(handler.getSlot(0).getStack(), handler.getSlot(1).getStack());
    }

    public static List<Integer> getTargetEnchantmentIndices(ItemEnchantmentsComponent enchantments, ItemStack slot2) {
        int maxIndex = enchantments.getSize() - 1;

        if (slot2.isOf(Items.WRITABLE_BOOK) || slot2.isOf(Items.WRITTEN_BOOK)) {
            String pageText = BookUtils.getInitialText(slot2);
            return BookUtils.parseIndices(pageText, maxIndex)
                    .stream()
                    .distinct()
                    .filter(i -> i >= 0 && i <= maxIndex)
                    .sorted()
                    .collect(Collectors.toList());
        }

        return List.of(maxIndex);
    }

    public static ItemStack getOutput(ItemStack slot1, ItemStack slot2) {
        if (!isSpecialTransfer(slot1, slot2)) return ItemStack.EMPTY;

        var enchantments = EnchantmentHelper.getEnchantments(slot1);
        var enchantList = new ArrayList<>(enchantments.getEnchantmentEntries());
        var targetIndices = getTargetEnchantmentIndices(enchantments, slot2);

        if (targetIndices.isEmpty()) return ItemStack.EMPTY;

        var builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

        for (int index : targetIndices) {
            if (index >= 0 && index < enchantList.size()) {
                var entry = enchantList.get(index);
                builder.set(entry.getKey(), entry.getValue());
            }
        }

        var selected = builder.build();
        if (selected.isEmpty()) return ItemStack.EMPTY;

        var result = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.set(result, selected);
        return result;
    }

    public static ItemStack getUpdatedSlot1(ItemStack slot1, ItemStack slot2) {
        if (!isSpecialTransfer(slot1, slot2)) return ItemStack.EMPTY;

        var updatedStack = slot1.copy();
        var enchantments = EnchantmentHelper.getEnchantments(updatedStack);
        var enchantList = new ArrayList<>(enchantments.getEnchantmentEntries());

        var indicesToRemove = getTargetEnchantmentIndices(enchantments, slot2);

        // Remove duplicates and sort descending to avoid index shift issues during removal
        indicesToRemove = indicesToRemove.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        var builder = new ItemEnchantmentsComponent.Builder(enchantments);

        for (int index : indicesToRemove) {
            if (index >= 0 && index < enchantList.size()) {
                var enchantToRemove = enchantList.get(index).getKey();
                builder.remove(e -> e.equals(enchantToRemove));
            }
        }

        var updated = builder.build();
        if (updated.isEmpty()) return new ItemStack(Items.BOOK);

        EnchantmentHelper.set(updatedStack, updated);
        return updatedStack;
    }

    public static ItemStack getUpdatedSlot2(ItemStack slot1, ItemStack slot2) {
        if (!isSpecialTransfer(slot1, slot2)) return ItemStack.EMPTY;

        // Reduce count by 1
        var count = slot2.getCount();
        if (count <= 1) return ItemStack.EMPTY;
        slot2.setCount(count - 1);
        return slot2;
    }
}
