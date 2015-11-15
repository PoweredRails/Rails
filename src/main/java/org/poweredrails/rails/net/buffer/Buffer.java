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
package org.poweredrails.rails.net.buffer;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class Buffer {

    private ByteBuf buffer;

    /**
     * Constructs a wrapper around the byte buffer provided.
     * @param buffer The byte buffer to wrap around.
     */
    public Buffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    /**
     * Returns the byte buffer this class wraps.
     * @return ByteBuf the byte buf
     */
    public ByteBuf getByteBuffer() {
        return this.buffer;
    }

    /**
     * Returns the var int byte count.
     * @param value byte value
     * @return validated byte count
     */
    private static int validateVarIntCount(int value) {
        return (value & 0xFFFFFF80) == 0 ? 1 : ((value & 0xFFFFC000) == 0 ? 2 : ((value & 0xFFE00000) == 0 ? 3
                : ((value & 0xF0000000) == 0 ? 4 : 5)));
    }

    /**
     * Writes an integer to the byte buffer.
     * @param value integer
     */
    public void writeInt(int value) {
        this.buffer.writeInt(value);
    }

    /**
     * Writes a long to the byte buffer.
     * @param value long
     */
    public void writeLong(long value) {
        this.buffer.writeLong(value);
    }

    /**
     * Writes a short to the byte buffer.
     * @param value short
     */
    public void writeShort(short value) {
        this.buffer.writeShort(value);
    }

    /**
     * Writes a byte to the byte buffer.
     * @param value byte
     */
    public void writeByte(byte value) {
        this.buffer.writeByte(value);
    }

    /**
     * Writes a byte to the byte buffer.
     * @param value integer
     */
    public void writeByte(int value) {
        this.buffer.writeByte(value);
    }

    /**
     * Writes a string to the byte buffer.
     * @param value string
     */
    public void writeString(String value) {
        byte[] bytes = value.getBytes(Charsets.UTF_8);
        if (validateVarIntCount(bytes.length) < 3) {
            writeVarInt(bytes.length, 2);
            this.buffer.writeBytes(bytes);
        } else {
            throw new RuntimeException("String is too long to be encoded.");
        }
    }

    /**
     * Writes a var int to the byte buffer.
     * @param value integer
     * @param weight value length
     */
    public void writeVarInt(int value, int weight) {
        if (validateVarIntCount(value) <= weight) {
            while ((value & -128) != 0) {
                this.buffer.writeByte(value & 127 | 128);
                value >>>= 7;
            }

            this.buffer.writeByte(value);
        } else {
            throw new RuntimeException("Failed to write varint: " + weight + " > " + validateVarIntCount(value));
        }
    }

    /**
     * Writes a byte array to the byte buffer.
     * @param array byte array
     */
    public void writeByteArray(byte[] array) {
        this.writeVarInt(array.length, 2);
        this.buffer.writeBytes(array);
    }

    /**
     * Writes a double to the byte buffer.
     * @param value double
     */
    public void writeDouble(double value) {
        this.buffer.writeDouble(value);
    }

    /**
     * Writes a float to the byte buffer.
     * @param value float
     */
    public void writeFloat(float value) {
        this.buffer.writeFloat(value);
    }

    /**
     * Writes a boolean to the byte array.
     * @param value boolean
     */
    public void writeBoolean(boolean value) {
        this.buffer.writeBoolean(value);
    }

    /**
     * Reads an integer from the byte buffer.
     * @return integer
     */
    public int readInt() {
        return this.buffer.readInt();
    }

    /**
     * Reads a long from the byte buffer.
     * @return long
     */
    public long readLong() {
        return this.buffer.readLong();
    }

    /**
     * Reads a short from the byte buffer.
     * @return short
     */
    public short readShort() {
        return this.buffer.readShort();
    }

    /**
     * Reads an unsigned short from the byte buffer.
     * @return unsigned short
     */
    public int readUnsignedShort() {
        return this.buffer.readUnsignedShort();
    }

    /**
     * Reads a byte from the byte buffer.
     * @return byte
     */
    public byte readByte() {
        return this.buffer.readByte();
    }

    /**
     * Reads an unsigned byte from the byte buffer.
     * @return unsigned byte
     */
    public short readUnsignedByte() {
        return this.buffer.readUnsignedByte();
    }

    /**
     * Reads a string from the byte buffer.
     * @return string
     */
    public String readString() {
        int length = readVarInt(2);
        String string = this.buffer.toString(this.buffer.readerIndex(), length, Charsets.UTF_8);
        this.buffer.readerIndex(this.buffer.readerIndex() + length);
        return string;
    }

    /**
     * Reads a var int from the byte buffer.
     * @param weight value length
     * @return var int
     */
    public int readVarInt(int weight) {
        if (weight > 0 && weight < 6) {
            int result = 0;
            int count = 0;
            byte byte0;

            do {
                byte0 = this.buffer.readByte();
                result |= (byte0 & 0x7f) << count++ * 7;

                if (count > weight) {
                    throw new RuntimeException("VarInt was too big. Must be less than " + String.valueOf(weight));
                }
            } while ((byte0 & 0x80) == 0x80);

            return result;
        } else {
            throw new RuntimeException("VarInt weight must be more than 0 and less than 6, not " + String.valueOf(weight));
        }
    }

    /**
     * Reads a byte array from the byte buffer.
     * @return byte array
     */
    public byte[] readByteArray() {
        int length = this.readVarInt(2);
        return this.buffer.readBytes(length).array();
    }

    /**
     * Reads a double from the byte buffer.
     * @return double
     */
    public double readDouble() {
        return this.buffer.readDouble();
    }

    /**
     * Reads a float from the byte buffer.
     * @return float
     */
    public float readFloat() {
        return this.buffer.readFloat();
    }

    /**
     * Reads a boolean from the byte buffer.
     * @return boolean
     */
    public boolean readBoolean() {
        return this.buffer.readBoolean();
    }

    /**
     * Returns the amount of readable bytes in the byte buffer.
     * @return amount of bytes
     */
    public int readableBytes() {
        return this.buffer.readableBytes();
    }

    /**
     * Marks the reader index.
     */
    public void markReaderIndex() {
        this.buffer.markReaderIndex();
    }

    /**
     * Resets the reader index.
     */
    public void resetReaderIndex() {
        this.buffer.resetReaderIndex();
    }

    /**
     * Clears the buffer.
     */
    public void clearBuffer() {
        this.buffer.clear();
    }

    public void writeUuid(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUuid() {
        long mostSignificantBits = readLong();
        long leastSignificantBits = readLong();

        return new UUID(
                mostSignificantBits,
                leastSignificantBits
        );
    }

}
