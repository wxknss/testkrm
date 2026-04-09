// KickCommand.java
package me.yourname.arenakicker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

public class KickCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("kickarena")
            .then(argument("nick", StringArgumentType.string())
                .executes(context -> {
                    String targetNick = StringArgumentType.getString(context, "nick");
                    ServerCommandSource source = context.getSource();
                    
                    try {
                        String currentIP = source.getServer().getServerIp();
                        int currentPort = source.getServer().getServerPort();
                        
                        // Если IP не задан (0.0.0.0), пытаемся определить
                        if (currentIP.isEmpty() || currentIP.equals("0.0.0.0")) {
                            currentIP = source.getPlayer().getClient().getServer().getAddress().getHostName();
                        }
                        
                        final String ip = currentIP;
                        final int port = currentPort;
                        
                        // Отправляем кик в отдельном потоке
                        new Thread(() -> {
                            try {
                                DirectConnector.kickPlayer(ip, port, targetNick);
                                source.sendFeedback(() -> Text.literal("§a[+] Кик отправлен для §e" + targetNick), false);
                            } catch (Exception e) {
                                source.sendFeedback(() -> Text.literal("§c[-] Ошибка: " + e.getMessage()), false);
                                ArenaKicker.LOGGER.error("Ошибка кика: ", e);
                            }
                        }).start();
                        
                    } catch (Exception e) {
                        source.sendFeedback(() -> Text.literal("§c[-] Не удалось определить IP/порт сервера"), false);
                    }
                    
                    return 1;
                })
            )
        );
    }
}