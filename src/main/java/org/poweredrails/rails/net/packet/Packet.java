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

import com.google.common.reflect.TypeToken;
import org.poweredrails.rails.net.buffer.Buffer;
import org.poweredrails.rails.net.session.Session;

public abstract class Packet<T> {

    private final TypeToken<T> token = new TypeToken<T>(getClass()) {
        private static final long serialVersionUID = 103948516358702773L;
    };

    protected Session sender;

    /**
     * Writes packet data to the buffer.
     * @param buffer packet buffer
     */
    public abstract void toBuffer(Buffer buffer);

    /**
     * Reads packet data from the buffer.
     * @param buffer packet buffer
     */
    public abstract void fromBuffer(Buffer buffer);

    /**
     * Handles this packet's read data.
     * @param handler packet handler
     */
    public abstract void handle(T handler);

    /**
     * Returns this packet's handler class.
     * @return packet handler class
     */
    @SuppressWarnings("unchecked")
    public Class<T> getHandlerClass() {
        return (Class<T>) this.token.getRawType();
    }

    /**
     * Sets the sender of this packet.
     * @param sender session
     */
    public void setSender(Session sender) {
        this.sender = sender;
    }

    /**
     * Returns the sender of this packet.
     * @return sender
     */
    public Session getSender() {
        return this.sender;
    }

}
