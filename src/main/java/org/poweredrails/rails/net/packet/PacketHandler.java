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
package org.poweredrails.rails.net.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.poweredrails.rails.net.buffer.Buffer;
import org.poweredrails.rails.net.handler.HandlerRegistry;
import org.poweredrails.rails.net.packet.registry.PacketFactory;
import org.poweredrails.rails.net.packet.registry.PacketRegistry;
import org.poweredrails.rails.net.session.Session;
import org.poweredrails.rails.net.session.SessionStateEnum;
import org.slf4j.Logger;

// TODO: Dispose of session when channel goes inactive.
public class PacketHandler extends SimpleChannelInboundHandler<UnresolvedPacket> {

    private final Logger logger;

    private Session session;
    private PacketRegistry packetRegistry;
    private HandlerRegistry handlerRegistry;

    public PacketHandler(Logger logger, Session session, PacketRegistry packetRegistry,
                         HandlerRegistry handlerRegistry) {
        this.logger = logger;
        this.session = session;
        this.packetRegistry = packetRegistry;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UnresolvedPacket unresolvedPacket) throws Exception {
        SessionStateEnum state = this.session.getState();

        int id = unresolvedPacket.getId();
        Buffer buffer = unresolvedPacket.getBuffer();

        PacketFactory factory = this.packetRegistry.find(state, id);
        if (factory == null) {
            this.logger.debug("Failed to resolve: unrecognised packet...");
            return;
        }

        Packet<?> packet = factory.create();
        packet.setSender(this.session);
        packet.fromBuffer(buffer);

        this.handlerRegistry.doHandle(packet);
    }

}
