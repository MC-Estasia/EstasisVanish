package de.mcestasia.estasiavanish.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class VanishPlayer {

    private UUID uuid;
    private String inventory;
    private String armor;

    public VanishPlayer(UUID uuid, String inventory, String armor) {
        this.uuid = uuid;
        this.inventory = inventory;
        this.armor = armor;
    }
}
