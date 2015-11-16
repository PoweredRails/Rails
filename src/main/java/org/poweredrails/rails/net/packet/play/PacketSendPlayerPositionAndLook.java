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

public class PacketSendPlayerPositionAndLook extends Packet<PlayPacketHandler> {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PacketSendPlayerPositionAndLook(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void toBuffer(Buffer buffer) {
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);
        // TODO: V
        // bit mask X/Y/Z/Y_ROT/X_ROT
        // if 1, relative. if 0, absolute.
        // Field    Bit
        // x        0x01
        // Y        0x02
        // Z        0x04
        // Y_ROT    0x08
        // X_ROT    0x10
        // buffer.writeByte((X_ROT << 0x10) & (Y_ROT << 0x08) & (Z << 0x04) & (Y << 0x02) & (X << 0x01));
        buffer.writeByte(0);
    }

    @Override
    public void fromBuffer(Buffer buffer) {}

    @Override
    public void handle(PlayPacketHandler handler) {}

}
