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
package org.poweredrails.rails.net.handler.status;

import org.json.JSONException;
import org.poweredrails.rails.net.packet.status.PacketReceivePing;
import org.poweredrails.rails.net.packet.status.PacketReceiveStatusRequest;
import org.poweredrails.rails.net.packet.status.PacketSendPong;
import org.poweredrails.rails.net.packet.status.PacketSendStatusResponse;
import org.poweredrails.rails.net.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusPacketHandler {

    private final Logger logger = LoggerFactory.getLogger("Rails");

    /**
     * Handles a status request packet.
     * @param packet status request packet
     * @throws JSONException if the response JSON failed to construct.
     */
    public void onStatusRequestPacket(PacketReceiveStatusRequest packet) throws JSONException {
        Session sender = packet.getSender();

        PacketSendStatusResponse response = new PacketSendStatusResponse();
        sender.sendPacket(response);

        this.logger.info("Responded to a status request.");
    }

    /**
     * Handles a ping packet.
     * @param packet ping packet
     */
    public void onPingPacket(PacketReceivePing packet) {
        Session sender = packet.getSender();

        PacketSendPong response = new PacketSendPong(packet.getTime());
        sender.sendPacket(response);
    }

}
