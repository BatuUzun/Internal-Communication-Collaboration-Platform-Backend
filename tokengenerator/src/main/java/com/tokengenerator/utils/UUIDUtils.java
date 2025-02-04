package com.tokengenerator.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {

    public static byte[] toBinary(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static UUID fromBinary(byte[] binary) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(binary);
        long mostSigBits = byteBuffer.getLong();
        long leastSigBits = byteBuffer.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }
}
