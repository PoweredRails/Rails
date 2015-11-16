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

public class PacketSendJoinGame extends Packet<PlayPacketHandler> {

//    private int entityId;
//    private Gamemode gamemode;
//    private Dimension dimension;
//    private Difficulty difficulty;
//    private int maxPlayers;
//    private String levelType;
//    private boolean reducedDebugInfo;

    @Override
    public void toBuffer(Buffer buffer) {
//        buffer.writeInt(this.entityId);
//        buffer.writeByte(this.gamemode.getId());
//        buffer.writeByte(this.dimension.getId());
//        buffer.writeByte(this.difficulty.getId());
//        buffer.writeByte(this.maxPlayers);
//        buffer.writeString(this.levelType);
//        buffer.writeBoolean(this.reducedDebugInfo);

        buffer.writeInt(0xCAFEBABE);
        buffer.writeByte((byte) 1);    // gamemode    : creative
        buffer.writeByte(0);           // dimension   : overworld
        buffer.writeByte((byte) 0);    // difficulty  : peaceful
        buffer.writeByte((byte) 20);   // max players : 20
        buffer.writeString("default"); // level type        : normal
        buffer.writeBoolean(false);    // reduced debug info : false
    }

    @Override
    public void fromBuffer(Buffer buffer) {}

    @Override
    public void handle(PlayPacketHandler handler) {}

//    public void setEntityId(int entityId) {
//        this.entityId = entityId;
//    }

//    public void setGamemode(Gamemode gamemode) {
//        this.gamemode = gamemode;
//    }

//    public void setDimension(Dimension dimension) {
//        this.dimension = dimension;
//    }

//    public void setDifficulty(Difficulty difficulty) {
//        this.difficulty = difficulty;
//    }

//    public void setMaxPlayers(int maxPlayers) {
//        this.maxPlayers = maxPlayers;
//    }

//    public void setLevelType(String levelType) {
//        this.levelType = levelType;
//    }

//    public void setReducedDebugInfo(boolean reducedDebugInfo) {
//        this.reducedDebugInfo = reducedDebugInfo;
//    }

}
