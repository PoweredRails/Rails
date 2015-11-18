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
package org.poweredrails.rails.net.handler.login;

import io.netty.channel.ChannelFuture;
import org.json.JSONException;
import org.json.JSONObject;
import org.poweredrails.rails.net.packet.login.PacketReceiveEncryptResponse;
import org.poweredrails.rails.net.packet.login.PacketReceiveLoginStart;
import org.poweredrails.rails.net.packet.login.PacketSendEncryptRequest;
import org.poweredrails.rails.net.packet.login.PacketSendLoginSuccess;
import org.poweredrails.rails.net.packet.play.PacketSendJoinGame;
import org.poweredrails.rails.net.packet.play.PacketSendPlayerPositionAndLook;
import org.poweredrails.rails.net.packet.play.PacketSendSpawnPosition;
import org.poweredrails.rails.net.session.Session;
import org.poweredrails.rails.net.session.SessionStateEnum;
import org.poweredrails.rails.util.UUIDUtil;
import org.poweredrails.rails.util.auth.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class LoginPacketHandler {

    private final Logger logger = LoggerFactory.getLogger("Rails");

    // TODO: Move this to a move sensible place.
    private final KeyPair keyPair;
    private final byte[] publicKey;
    private final PrivateKey privateKey;

    public LoginPacketHandler() {
        this.keyPair    = Encryption.generateKeyPair();
        this.publicKey  = Encryption.toX509(this.keyPair.getPublic()).getEncoded();
        this.privateKey = this.keyPair.getPrivate();
    }

    /**
     * Handles a login start packet.
     * @param packet login start packet
     */
    public void onLoginStart(PacketReceiveLoginStart packet) {
        this.logger.info("User [" + packet.getName() + "] logging in...");

        final Session sender = packet.getSender();

        String sessionId = sender.getSessionId();
        byte[] publicKey = Encryption.toX509(this.keyPair.getPublic()).getEncoded();
        byte[] verifyKey = Encryption.generateToken(4);

        sender.setVerifyUsername(packet.getName());
        sender.setVerifyToken(verifyKey);

        PacketSendEncryptRequest response = new PacketSendEncryptRequest(sessionId, publicKey, verifyKey);
        sender.sendPacket(response);
    }

    /**
     * Handles an encrypt response packet.
     * @param packet encrypt response packet
     */
    public void onEncryptResponse(PacketReceiveEncryptResponse packet) {
        final Session sender = packet.getSender();

        try {
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            final SecretKey sharedSecret = new SecretKeySpec(cipher.doFinal(packet.getSharedSecret()), "AES");

            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
            final byte[] verifyToken = cipher.doFinal(packet.getVerifyToken());

            if (!Arrays.equals(verifyToken, sender.getVerifyToken())) {
                // TODO: Send disconnect packet
                // PacketSendDisconnect response = new PacketSendDisconnect("...");
                // sender.sendPacket(response);
                return;
            }

            sender.enableEncryption(sharedSecret);

            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(sender.getSessionId().getBytes());
            digest.update(sharedSecret.getEncoded());
            digest.update(this.publicKey);
            String hash = new BigInteger(digest.digest()).toString(16);

            new Thread(() -> {
                JSONObject response = this.callAPI(sender.getVerifyUsername(), hash);

                String name = null;
                String id   = null;
                try {
                    name = response.getString("name");
                    id   = response.getString("id");
                } catch (JSONException e) {
                    throw new RuntimeException("Failed to parse Mojang JSON response!", e);
                }

                UUID uuid = UUIDUtil.fromFlatString(id);
                this.logger.info("Successfully authenticated Player [" + name + ", " + uuid + "].");

                ChannelFuture future = sender.sendPacket(new PacketSendLoginSuccess(uuid, name));

                if (future != null) {
                    future.addListener((result) -> {
                        sender.setState(SessionStateEnum.PLAY);

                        PacketSendJoinGame joinPacket = new PacketSendJoinGame();
                        //... SET DATA
                        sender.sendPacket(joinPacket);

//                        List<PacketSendChunk> chunks = new ArrayList<>();
//                        sender.sendPacket(new PacketSendChunkBulk(true, chunks));
                        sender.sendPacket(new PacketSendSpawnPosition(10, 11, 10));
                        sender.sendPacket(new PacketSendPlayerPositionAndLook(10, 11, 10, 0, 0));
                    });
                }

                /*
                 * x1. LoginSuccess
                 * ..
                 * x2. JoinGame
                 * <enable encryption> NOT IF LOCALHOST
                 * 3. SetCompression NOT REQUIRED
                 * ..
                 * 4. WindowItems (inventory) NOT REQUIRED
                 * x5. ChunkBulk (world) NOT REQUIRED
                 * ..
                 * x6. SpawnPosition
                 * x7. PositionAndLook (we load into the world here)
                 */
            }).start();
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while validating a login!", e);
        }
    }

    private JSONObject callAPI(String username, String hash) {
        final String baseUrl = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";
        try {
            URLConnection connection = new URL(String.format(baseUrl, username, hash)).openConnection();

            final InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line).append('\n');
            }

            return new JSONObject(builder.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify username!", e);
        }
    }

}
