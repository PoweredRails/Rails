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
package org.poweredrails.rails.net.packet.play;

import org.poweredrails.rails.net.buffer.Buffer;
import org.poweredrails.rails.net.handler.play.PlayPacketHandler;
import org.poweredrails.rails.net.packet.Packet;

import java.util.List;

public class PacketSendChunkBulk extends Packet<PlayPacketHandler> {

    private boolean skylight;
    private List<PacketSendChunk> chunks;

    public PacketSendChunkBulk(boolean skylight, List<PacketSendChunk> chunks) {
        this.skylight = skylight;
        this.chunks = chunks;
    }

    @Override
    public void toBuffer(Buffer buffer) {
        buffer.writeBoolean(this.skylight);
        buffer.writeVarInt(this.chunks.size(), 2);

        for (PacketSendChunk packet : this.chunks) {
            buffer.writeInt(packet.getX());
            buffer.writeInt(packet.getZ());
//            buffer.writeInt((byte) packet.getMask());
            buffer.writeShort(packet.getMask());
    }

        for (PacketSendChunk packet : this.chunks) {
            buffer.writeByteArray(packet.getData());
        }
    }

    @Override
    public void fromBuffer(Buffer buffer) {}

    @Override
    public void handle(PlayPacketHandler handler) {}

}
