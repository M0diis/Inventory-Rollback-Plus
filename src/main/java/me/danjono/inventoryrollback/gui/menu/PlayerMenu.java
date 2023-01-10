package me.danjono.inventoryrollback.gui.menu;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;

public class PlayerMenu {

    private final Player staff;
    private final OfflinePlayer offlinePlayer;

    private final Buttons buttons;
    private Inventory inventory;

    public PlayerMenu(Player staff, OfflinePlayer player) {
        this.staff = staff;
        this.offlinePlayer = player;
        this.buttons = new Buttons(player.getUniqueId());

        createInventory();
    }

    public void createInventory() {
        inventory = Bukkit.createInventory(staff, InventoryName.PLAYER_MENU.getSize(), InventoryName.PLAYER_MENU.getName());
        
        inventory.setItem(2, buttons.createDeathLogButton(Material.getMaterial(MessageData.getDeathsLogButtonMaterial()), LogType.DEATH, null));
        inventory.setItem(3, buttons.createJoinLogButton(Material.getMaterial(MessageData.getJoinsLogButtonMaterial()), LogType.JOIN, null));
        inventory.setItem(4, buttons.createQuitLogButton(Material.getMaterial(MessageData.getQuitsLogButtonMaterial()), LogType.QUIT, null));
        inventory.setItem(5, buttons.createWorldChangeLogButton(Material.getMaterial(MessageData.getWorldChangesLogButtonMaterial()), LogType.WORLD_CHANGE, null));
        inventory.setItem(6, buttons.createForceSaveLogButton(Material.getMaterial(MessageData.getForceSavesLogButtonMaterial()), LogType.FORCE, null));
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void getPlayerMenu() {
        List<String> lore = new ArrayList<>();
        
        if (offlinePlayer.isOnline()) {
            lore.add(ChatColor.GREEN + "Online now");
        } else if (!offlinePlayer.hasPlayedBefore()) {
            lore.add(ChatColor.RED + "Never played on this server");
        } else {
            lore.add(ChatColor.RED + "Offline");
            
            String dateTime = "Unknown";
            if (offlinePlayer.getLastPlayed() != 0)
                dateTime = PlayerData.getTime(offlinePlayer.getLastPlayed());
            lore.add(ChatColor.RED + "Last online: " + dateTime);
        }
        
        inventory.setItem(0, buttons.playerHead(lore, true));
        UUID uuid = offlinePlayer.getUniqueId();

        PlayerData deathBackup = new PlayerData(uuid, LogType.DEATH, null);
        PlayerData joinBackup = new PlayerData(uuid, LogType.JOIN, null);
        PlayerData quitBackup = new PlayerData(uuid, LogType.QUIT, null);
        PlayerData worldChangeBackup = new PlayerData(uuid, LogType.WORLD_CHANGE, null);
        PlayerData forceSaveBackup = new PlayerData(uuid, LogType.FORCE, null);

        if (!joinBackup.doesBackupTypeExist()
                && !quitBackup.doesBackupTypeExist()
                && !deathBackup.doesBackupTypeExist()
                && !worldChangeBackup.doesBackupTypeExist()
                && !forceSaveBackup.doesBackupTypeExist()) {

            //No backups have been found for the player
            staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoBackupError(offlinePlayer.getName()));
        }

        List<String> deaths = MessageData.getDeathsLogButtonLore()
                        .stream()
                        .map(s -> s.replace("%backups%", String.valueOf(deathBackup.getAmountOfBackups()))
                                .replace("%player%", offlinePlayer.getName()))
                        .collect(Collectors.toList());

        Material deathMaterial = Material.getMaterial(MessageData.getDeathsLogButtonMaterial());

        inventory.setItem(2, buttons.createDeathLogButton(deathMaterial, LogType.DEATH, deaths));
        
        List<String> joins = MessageData.getJoinsLogButtonLore()
                        .stream()
                        .map(s -> s.replace("%backups%", String.valueOf(joinBackup.getAmountOfBackups()))
                                .replace("%player%", offlinePlayer.getName()))
                        .collect(Collectors.toList());

        Material joinsMaterial = Material.getMaterial(MessageData.getJoinsLogButtonMaterial());
        
        inventory.setItem(3, buttons.createJoinLogButton(joinsMaterial, LogType.JOIN, joins));
        
        List<String> quits = MessageData.getQuitsLogButtonLore()
                        .stream()
                        .map(s -> s.replace("%backups%", String.valueOf(quitBackup.getAmountOfBackups()))
                                .replace("%player%", offlinePlayer.getName()))
                        .collect(Collectors.toList());
        
        Material quitsMaterial = Material.getMaterial(MessageData.getQuitsLogButtonMaterial());
                
        inventory.setItem(4, buttons.createQuitLogButton(quitsMaterial, LogType.QUIT, quits));
        
        List<String> worldChange = MessageData.getWorldChangesLogButtonLore()
                        .stream()
                        .map(s -> s.replace("%backups%", String.valueOf(worldChangeBackup.getAmountOfBackups()))
                                .replace("%player%", offlinePlayer.getName()))
                        .collect(Collectors.toList());
        
        Material worldChangeMaterial = Material.getMaterial(MessageData.getWorldChangesLogButtonMaterial());
        
        inventory.setItem(5, buttons.createWorldChangeLogButton(worldChangeMaterial, LogType.WORLD_CHANGE, worldChange));
        
        List<String> forceSaves = MessageData.getForceSavesLogButtonLore()
                        .stream()
                        .map(s -> s.replace("%backups%", String.valueOf(forceSaveBackup.getAmountOfBackups()))
                                .replace("%player%", offlinePlayer.getName()))
                        .collect(Collectors.toList());
        
        Material forceSavesMaterial = Material.getMaterial(MessageData.getForceSavesLogButtonMaterial());
        
        inventory.setItem(6, buttons.createForceSaveLogButton(forceSavesMaterial, LogType.FORCE, forceSaves));
    }

}
