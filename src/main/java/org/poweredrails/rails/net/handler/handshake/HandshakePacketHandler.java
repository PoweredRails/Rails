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
package org.poweredrails.rails.net.handler.handshake;

import org.poweredrails.rails.net.packet.handshake.PacketReceiveHandshake;
import org.poweredrails.rails.net.packet.login.PacketSendDisconnect;
import org.poweredrails.rails.net.session.Session;
import org.poweredrails.rails.net.session.SessionStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandshakePacketHandler {

    private final Logger logger = LoggerFactory.getLogger("Rails");

    /**
     * Handles a handshake packet.
     * @param packet handshake packet
     */
    public void onHandshakePacket(PacketReceiveHandshake packet) {
        Session sender = packet.getSender();

        SessionStateEnum state = SessionStateEnum.fromId(packet.getState());
        sender.setState(state);

        String address = packet.getAddress();
        int port = packet.getPort();

        this.logger.info(String.format("Client [%s:%s] connecting...", address, port));

        int protocol = packet.getProtocol();

        if (protocol != 47) {
            sender.sendPacket( new PacketSendDisconnect("We don't support that protocol, sorry. (" + protocol + ")") );
            this.logger.info("Session " + sender + " was kicked - outdated protocol.");
        }
    }

}
