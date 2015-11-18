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
package org.poweredrails.rails.net.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.poweredrails.rails.Main;
import org.poweredrails.rails.net.packet.EncryptionHandler;
import org.poweredrails.rails.net.packet.Packet;

import java.util.Random;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

public class Session {

    private final Random random = new Random();
    private final Logger logger = Logger.getLogger("Rails");

    private final Channel channel;
    private final String sessionId = Long.toString(this.random.nextLong(), 16).trim();

    private String verifyUsername;
    private byte[] verifyToken;

    private String address;
    private int port;

    private SessionStateEnum state = SessionStateEnum.HANDSHAKE;

    public Session(Channel channel) {
        this.channel = channel;
    }

    /**
     * Creates a new session around the channel handler context, represents the connection between a client
     * and the server.
     * @param ctx connection
     */
    @Deprecated
    public Session(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
    }

    /**
     * Returns the channel handler context for this session.
     * @return channel
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * Writes a packet to the handler context, to be sent to the client.
     * @param packet packet
     * @return channel future
     */
    public ChannelFuture sendPacket(Packet<?> packet) {
        if (!Main.getEventBus().firePacket(this, packet)) {
            return this.channel.writeAndFlush(packet);
        }

        return null;
    }

    /**
     * Enables encryption for this session's channel.
     * @param sharedSecret the shared secret to encrypt/decrypt with
     */
    public void enableEncryption(SecretKey sharedSecret) {
        this.channel.pipeline().replace("encryption", "encryption", new EncryptionHandler(sharedSecret));
    }

    /**
     * Returns the state for this session.
     * @return session state
     */
    public SessionStateEnum getState() {
        return this.state;
    }

    /**
     * Changes the state of this session.
     * @param state session state
     */
    public void setState(SessionStateEnum state) {
        this.state = state;
    }

    /**
     * Returns the session ID.
     * @return session id
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * Returns the verify username for this session.
     * @return the verify username
     */
    public String getVerifyUsername() {
        return this.verifyUsername;
    }

    /**
     * Sets the verify username for this session.
     * @param verifyUsername the verify username to set it to
     */
    public void setVerifyUsername(String verifyUsername) {
        this.verifyUsername = verifyUsername;
    }

    /**
     * Returns the verify token for this session.
     * @return the verify token
     */
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    /**
     * Sets the verify token for the session.
     * @param verifyToken the verify token to set it to
     */
    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public String toString() {
        return this.channel.toString();
    }

}
