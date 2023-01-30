package net.items.store.skywars.loot;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.items.store.minigames.api.MiniGame;
import net.items.store.minigames.api.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LootManager {

    private static RandomCollection randomCollection, middleCollection;

    private static Random random;

    static {
        prepareItems();
        prepareMiddle();
    }

    public static void prepareChest(Inventory inventory, ChestType chestType){
        MiniGame.getExecutorService().submit(() ->{
            int itemAmount = getRandomItemAmount();

            while (itemAmount > 0){
                itemAmount -= 1;

                int randomSlot = getRandomSlot(inventory);

                Map.Entry<LootItem, ItemStack> randomItem = getRandomItem(inventory, chestType);
                LootItem lootItem = randomItem.getKey();
                ItemStack randomStack = randomItem.getValue().clone();

                if (lootItem.getAmount() == 0) {
                    int randomAmount;

                    if (lootItem.getMinRandom() == 0) {
                        randomAmount = random.nextInt(lootItem.getRandomAmount());
                    } else {
                        randomAmount = random.nextInt(lootItem.getRandomAmount() - lootItem.getMinRandom() + 1) + lootItem.getMinRandom();
                    }

                    randomStack.setAmount(randomAmount);
                }

                if (randomStack.getAmount() > 0){
                    inventory.setItem(randomSlot, randomStack);
                }
            }
        });
    }

    private static Map.Entry<LootItem, ItemStack> getRandomItem(Inventory inventory, ChestType chestType){
        LootItem lootItem = null;
        ItemStack randomStack = null;

        while (randomStack == null || inventory.contains(randomStack)){
            lootItem = (LootItem) (chestType == ChestType.NORMAL ? randomCollection.next() : middleCollection.next());
            randomStack = lootItem.getItemBuilder().buildItem();
        }

        return new AbstractMap.SimpleEntry(lootItem, randomStack);
    }

    private static int getRandomSlot(Inventory inventory){
        int randomSlot = (int) (Math.random() * 26);
        int trys = 10;

        while (inventory.getItem(randomSlot) != null){
            randomSlot = (int) (Math.random() * 26);
            trys -= 1;

            if (trys <= 0) {
                break;
            }
        }

        return randomSlot;
    }

    private static int getRandomItemAmount(){
        int itemAmount = 0;

        while (itemAmount < 8){
            itemAmount = (int) (Math.random() * 16);
        }

        return itemAmount;
    }

    public static void prepareItems() {
        random = new Random();
        randomCollection = new RandomCollection<LootItem>();

        randomCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        randomCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        randomCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        randomCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1,  false ) );
        randomCollection.add( 0.5, new LootItem(ItemBuilder.modify().setMaterial(Material.ENDER_PEARL), 1, false ) );
        randomCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_AXE).addEnchant(Enchantment.DIG_SPEED, 1), 1,  false ) );
        randomCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 1), 1, false ) );

        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND), 0, 4, 1, false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1,  false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1,  false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1,  false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_AXE).addEnchant(Enchantment.DIG_SPEED, 2), 1, false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 2), 1, false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 2), 1, false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.SPLASH_POTION).setPotionEffectType(PotionEffectType.HEAL).setPotionDuration(5).setPotionAmplifier(0), 0, 4, 1, false ) );
        randomCollection.add( 2, new LootItem(ItemBuilder.modify().setMaterial(Material.SPLASH_POTION).setPotionEffectType(PotionEffectType.HEAL).setPotionDuration(5).setPotionAmplifier(1), 0, 4, 1, false ) );

        randomCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_INGOT), 0, 10, 1, false ) );
        randomCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.TNT), 0, 10, 2, false ) );
        randomCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.WATER_BUCKET), 1, false ) );
        randomCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.LAVA_BUCKET), 1, false ) );
        randomCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.EXPERIENCE_BOTTLE), 0, 15, 3, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        randomCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_APPLE), 1, false ) );

        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.COMPASS), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.COOKED_BEEF), 0, 10, 5, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.COOKED_BEEF), 0, 10, 5, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.FLINT), 0, 5, 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.STONE), 0, 64, 30, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.BRICKS), 0, 64, 30, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GLASS), 0, 64, 20, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.OAK_PLANKS), 0, 64, 30, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.STONE), 0, 64, 30, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.BRICKS), 0, 64, 30, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GLASS), 0, 64, 20, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.OAK_PLANKS), 0, 64, 30, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.COBWEB), 0, 5, 2, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.PUMPKIN_PIE), 0, 4, 2, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.PUMPKIN_PIE), 0, 4, 2, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CAKE), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.BEEF), 0, 10, 5, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.STICK), 0, 5, 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.WOODEN_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 2), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 2), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.STONE_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 2), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_HELMET), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.FLINT_AND_STEEL), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_CHESTPLATE), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_LEGGINGS), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_BOOTS), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_HELMET), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_CHESTPLATE), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_LEGGINGS), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_BOOTS), 1, false ) );
    }

    public static void prepareMiddle() {
        middleCollection = new RandomCollection<LootItem>();

        middleCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        middleCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        middleCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        middleCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        middleCollection.add( 1.5, new LootItem(ItemBuilder.modify().setMaterial(Material.ENDER_PEARL), 1, false ) );
        middleCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_AXE).addEnchant(Enchantment.DIG_SPEED, 1), 1, false ) );
        middleCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 1), 1, false ) );

        middleCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.DIAMOND), 0, 4, 1, false ) );
        middleCollection.add( 5, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        middleCollection.add( 5, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        middleCollection.add( 5, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        middleCollection.add( 5, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        randomCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.SPLASH_POTION).setPotionEffectType(PotionEffectType.HEAL).setPotionDuration(5).setPotionAmplifier(0), 0, 4, 1, false ) );
        randomCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.SPLASH_POTION).setPotionEffectType(PotionEffectType.HEAL).setPotionDuration(5).setPotionAmplifier(1), 0, 4, 1, false ) );

        middleCollection.add( 3, new LootItem(ItemBuilder.modify().setMaterial(Material.IRON_INGOT), 0, 10, 1, false ) );
        middleCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.TNT), 0, 10, 2, false ) );
        middleCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.WATER_BUCKET), 1, false ) );
        middleCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.LAVA_BUCKET), 1, false ) );
        middleCollection.add( 5, new LootItem(ItemBuilder.modify().setMaterial(Material.EXPERIENCE_BOTTLE), 0, 15, 3, false ) );
        middleCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        middleCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1), 1, false ) );
        middleCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        middleCollection.add( 1, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2), 1, false ) );
        middleCollection.add( 4, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_APPLE), 1, false ) );


        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.COMPASS), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.COOKED_BEEF), 0, 10, 5, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.FLINT), 0, 5, 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.STONE), 0, 64, 30,  false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.BRICKS), 0, 64, 30, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GLASS), 0, 64, 20, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.OAK_PLANKS), 0, 64, 30, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.COBWEB), 0, 5, 2, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.PUMPKIN_PIE), 0, 4, 2, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CAKE), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.BEEF), 0, 10, 5, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.STICK), 0, 5, 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.WOODEN_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 2), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.GOLDEN_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 2), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.STONE_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 2), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_HELMET), 1, false ) );
        randomCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.FLINT_AND_STEEL), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_CHESTPLATE), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_LEGGINGS), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.LEATHER_BOOTS), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_HELMET), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_CHESTPLATE), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_LEGGINGS), 1, false ) );
        middleCollection.add( 6, new LootItem(ItemBuilder.modify().setMaterial(Material.CHAINMAIL_BOOTS), 1, false ) );
    }
}
