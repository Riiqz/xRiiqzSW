package net.items.store.skywars.loot;

import lombok.Getter;
import net.items.store.minigames.api.item.ItemBuilder;

public class LootItem {

    @Getter
    private ItemBuilder itemBuilder;
    @Getter
    private int amount, randomAmount, minRandom;
    @Getter
    private boolean onlyMiddle;

    public LootItem(ItemBuilder itemBuilder, int amount, boolean onlyMiddle) {
        this.itemBuilder = itemBuilder;
        this.amount = amount;
        this.onlyMiddle = onlyMiddle;
    }

    public LootItem(ItemBuilder itemBuilder, int amount, int randomAmount, int minRandom, boolean onlyMiddle) {
        this(itemBuilder, amount, onlyMiddle);

        this.randomAmount = randomAmount;
        this.minRandom = minRandom;
    }
}
