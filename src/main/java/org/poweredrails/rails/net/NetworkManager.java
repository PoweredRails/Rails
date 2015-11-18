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
package org.poweredrails.rails.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.poweredrails.rails.net.channel.ServerChannelInitializer;
import org.poweredrails.rails.net.handler.HandlerRegistry;
import org.poweredrails.rails.net.packet.registry.PacketRegistry;
import org.poweredrails.rails.net.session.SessionManager;
import org.slf4j.Logger;

import java.net.SocketAddress;

public class NetworkManager {

    private final Logger logger;

    private final ServerBootstrap nettyBootstrap  = new ServerBootstrap();
    private final EventLoopGroup nettyBossGroup = new NioEventLoopGroup();
    private final EventLoopGroup nettyWorkerGroup = new NioEventLoopGroup();

    public NetworkManager(Logger logger) {
        this.logger = logger;

        PacketRegistry packetRegistry = new PacketRegistry();
        HandlerRegistry handlerRegistry = new HandlerRegistry();
        SessionManager sessionManager = new SessionManager();
        this.nettyBootstrap
                .group(this.nettyBossGroup, this.nettyWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ServerChannelInitializer(
                        this.logger, sessionManager, packetRegistry, handlerRegistry));
    }

    /**
     * Binds the channel to the provided address.
     * @param socketAddress address to bind to
     * @return result
     */
    public ChannelFuture bindTo(final SocketAddress socketAddress) {
        return this.nettyBootstrap.bind(socketAddress).addListener(f -> {
            if (f.isSuccess()) {
                onBindSuccess(socketAddress);
            } else {
                onBindFailure(socketAddress, f.cause());
            }
        });
    }

    /**
     * Shuts down the channel gracefully.
     */
    public void shutdown() {
        this.nettyWorkerGroup.shutdownGracefully();
        this.nettyBossGroup.shutdownGracefully();
    }

    private void onBindSuccess(SocketAddress address) {
        this.logger.info("Bound to address: " + address);
        // Call "BindServerEvent"
    }

    private void onBindFailure(SocketAddress address, Throwable throwable) {
        this.logger.info("Failed to bind to address: " + address);
        // Call "BindServerEvent"
    }

}
