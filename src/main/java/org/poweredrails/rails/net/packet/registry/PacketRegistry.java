/*
 * This file is a part of the multiplayer platform Powered Rails, licensed under the MIT License (MIT).
 *
 * Copyright (c) Powered Rails
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.poweredrails.rails.net.packet.registry;

import static org.poweredrails.rails.net.session.SessionStateEnum.HANDSHAKE;
import static org.poweredrails.rails.net.session.SessionStateEnum.LOGIN;
import static org.poweredrails.rails.net.session.SessionStateEnum.PLAY;
import static org.poweredrails.rails.net.session.SessionStateEnum.STATUS;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.poweredrails.rails.net.packet.Packet;
import org.poweredrails.rails.net.packet.handshake.PacketReceiveHandshake;
import org.poweredrails.rails.net.packet.login.PacketReceiveEncryptResponse;
import org.poweredrails.rails.net.packet.login.PacketReceiveLoginStart;
import org.poweredrails.rails.net.packet.login.PacketSendDisconnect;
import org.poweredrails.rails.net.packet.login.PacketSendEncryptRequest;
import org.poweredrails.rails.net.packet.login.PacketSendLoginSuccess;
import org.poweredrails.rails.net.packet.play.PacketReceivePlayerPositionAndLook;
import org.poweredrails.rails.net.packet.play.PacketSendChunk;
import org.poweredrails.rails.net.packet.play.PacketSendChunkBulk;
import org.poweredrails.rails.net.packet.play.PacketSendJoinGame;
import org.poweredrails.rails.net.packet.play.PacketSendPlayerPositionAndLook;
import org.poweredrails.rails.net.packet.play.PacketSendSpawnPosition;
import org.poweredrails.rails.net.packet.status.PacketReceivePing;
import org.poweredrails.rails.net.packet.status.PacketReceiveStatusRequest;
import org.poweredrails.rails.net.packet.status.PacketSendPong;
import org.poweredrails.rails.net.packet.status.PacketSendStatusResponse;
import org.poweredrails.rails.net.session.SessionStateEnum;

import java.util.Map;

public class PacketRegistry {

    private Table<SessionStateEnum, Integer, Class<? extends Packet<?>>> tableIncoming = HashBasedTable.create();
    private Table<SessionStateEnum, Integer, Class<? extends Packet<?>>> tableOutgoing = HashBasedTable.create();

    /**
     * Register all packet classes to their ids in different session states.
     */
    public PacketRegistry() {
        this.tableIncoming.put(HANDSHAKE, 0x00, PacketReceiveHandshake.class);

        this.tableIncoming.put(STATUS, 0x00, PacketReceiveStatusRequest.class);
        this.tableOutgoing.put(STATUS, 0x00, PacketSendStatusResponse.class);
        this.tableIncoming.put(STATUS, 0x01, PacketReceivePing.class);
        this.tableOutgoing.put(STATUS, 0x01, PacketSendPong.class);

        this.tableIncoming.put(LOGIN, 0x00, PacketReceiveLoginStart.class);
        this.tableOutgoing.put(LOGIN, 0x00, PacketSendDisconnect.class);
        this.tableIncoming.put(LOGIN, 0x01, PacketReceiveEncryptResponse.class);
        this.tableOutgoing.put(LOGIN, 0x01, PacketSendEncryptRequest.class);
        this.tableOutgoing.put(LOGIN, 0x02, PacketSendLoginSuccess.class);

        this.tableOutgoing.put(PLAY, 0x01, PacketSendJoinGame.class);
        this.tableOutgoing.put(PLAY, 0x05, PacketSendSpawnPosition.class);
        this.tableIncoming.put(PLAY, 0x06, PacketReceivePlayerPositionAndLook.class);
        this.tableOutgoing.put(PLAY, 0x08, PacketSendPlayerPositionAndLook.class);
        this.tableOutgoing.put(PLAY, 0x21, PacketSendChunk.class);
        this.tableOutgoing.put(PLAY, 0x26, PacketSendChunkBulk.class);
    }

    /**
     * Find a packet by its id, and return its factory.
     * @param state session state
     * @param id packet id
     * @return PacketFactory - packet factory for packet
     */
    public PacketFactory find(SessionStateEnum state, int id) {
        Class<? extends Packet<?>> clazz = this.tableIncoming.get(state, id);

        if (clazz != null) {
            return new PacketFactory(clazz);
        }

        return null;
    }

    /**
     * Find a packet's id and return it.
     * @param state session state
     * @param packet packet
     * @return int - packet id
     */
    public int find(SessionStateEnum state, Packet<?> packet) {
        Class<?> packetClass = packet.getClass();
        Map<Integer, Class<? extends Packet<?>>> rowMap = this.tableOutgoing.row(state);

        for (Map.Entry<Integer, Class<? extends Packet<?>>> entry : rowMap.entrySet()) {
            if (entry.getValue().equals(packetClass)) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Packet " + packetClass.getName() + " isn't registered...!");
    }

}
