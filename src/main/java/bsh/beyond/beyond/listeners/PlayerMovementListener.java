package bsh.beyond.beyond.listeners;
import org.bukkit.Sound;

import bsh.beyond.beyond.util.PlayerMovementUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerMovementListener implements Listener {

    private final HashMap<UUID, Boolean> monsterPlayers;
    private final HashMap<UUID, Boolean> movingPlayers;
    private final HashMap<UUID, Boolean> verbosePlayers; // New HashMap for verbose mode

    public PlayerMovementListener(HashMap<UUID, Boolean> monsterPlayers, HashMap<UUID, Boolean> movingPlayers, HashMap<UUID, Boolean> verbosePlayers) {
        this.monsterPlayers = monsterPlayers;
        this.movingPlayers = movingPlayers;
        this.verbosePlayers = verbosePlayers; // Initialize verbosePlayers
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        boolean isMoving = PlayerMovementUtil.isMoving(event);

        // Check if the player has actually moved from their block location
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            isMoving = false;
        }

        // Update movingPlayers HashMap
        movingPlayers.put(player.getUniqueId(), isMoving);

        handlePlayerVisibility(player, isMoving);
    }

    private void handlePlayerVisibility(Player player, boolean isMoving) {
        // Make the player visible or invisible to monsterPlayers
        for (Player monsterPlayer : Bukkit.getOnlinePlayers()) {
            if (!monsterPlayers.containsKey(monsterPlayer.getUniqueId())) {
                continue;
            }
    
            // Send verbose messages if enabled
            if (verbosePlayers.getOrDefault(monsterPlayer.getUniqueId(), false)) {
                String visibilityMessage = isMoving ?
                        "* " + player.getName() + " is now visible." :
                        player.getName() + " is no longer visible.";
                monsterPlayer.sendMessage(visibilityMessage);
            }
    
            // Update visibility
            if (isMoving) {
                monsterPlayer.showPlayer(player);
    
                // Play a sound at the player's location
                monsterPlayer.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 3.0F, 2.0F);
            } else {
                monsterPlayer.hidePlayer(player);
            }
        }
    }
}
