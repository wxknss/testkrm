// ArenaKicker.java
package me.yourname.arenakicker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArenaKicker implements ModInitializer {
    
    public static final String MOD_ID = "arenakicker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[ArenaKicker] Мод загружен для 1.21.1!");
        
        // Регистрируем команду
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            KickCommand.register(dispatcher);
        });
    }
}