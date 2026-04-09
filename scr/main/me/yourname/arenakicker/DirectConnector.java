// DirectConnector.java
package me.yourname.arenakicker;

import java.io.DataOutputStream;
import java.net.Socket;

public class DirectConnector {
    
    // Протокол 1.21.1 = 767 (проверь при необходимости)
    private static final int PROTOCOL_VERSION = 767;
    
    public static void kickPlayer(String ip, int port, String fakeNick) throws Exception {
        try (Socket socket = new Socket(ip, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            
            // ===== HANDSHAKE PACKET (0x00) =====
            out.writeByte(0x00); // Packet ID
            
            // Protocol Version (VarInt)
            writeVarInt(out, PROTOCOL_VERSION);
            
            // Server Address (String)
            writeString(out, ip);
            
            // Port (unsigned short)
            out.writeShort(port & 0xFFFF);
            
            // Next state: 2 = Login
            writeVarInt(out, 2);
            
            // ===== LOGIN START PACKET (0x00 в состоянии Login) =====
            out.writeByte(0x00); // Packet ID
            writeString(out, fakeNick); // Имя игрока
            
            // Опционально: UUID (для 1.21.1 можно не слать, сервер сам сгенерит)
            // Если сервер требует UUID, расскомментируй:
            // writeUuid(out, UUID.randomUUID());
            
            out.flush();
            
            // Ждем ответ сервера (50мс достаточно)
            Thread.sleep(50);
            
            // Закрываем соединение
            socket.close();
        }
    }
    
    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========
    
    private static void writeVarInt(DataOutputStream out, int value) throws Exception {
        do {
            byte temp = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) temp |= 0x80;
            out.writeByte(temp);
        } while (value != 0);
    }
    
    private static void writeString(DataOutputStream out, String str) throws Exception {
        byte[] bytes = str.getBytes("UTF-8");
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }
    
    @SuppressWarnings("unused")
    private static void writeUuid(DataOutputStream out, java.util.UUID uuid) throws Exception {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }
}