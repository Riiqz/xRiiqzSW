package net.items.store.skywars.loot;

import lombok.Getter;

public enum ChestType {

    NORMAL("Chest"),
    MIDDLE("MidChest");

    @Getter
    private String locationIdentifier;

    ChestType(String locationIdentifier){
        this.locationIdentifier = locationIdentifier;
    }
}
