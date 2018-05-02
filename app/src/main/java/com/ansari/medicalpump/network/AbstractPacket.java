package com.ansari.medicalpump.network;

import com.ansari.medicalpump.network.utils.CRC;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Eabasir on 7/13/2017.
 */

public class AbstractPacket {

    public static String seperator_text = "Cortex-M3-SIM800-ME";


    protected static int SRC_ADD_COUNTER = 2;
    protected static int DEST_ADD_COUNTER = 2;
    protected static int CMD_COUNTER = 1;
    protected static int ADD_VAR_COUNTER = 2;
    protected static int CRC_COUNTER = 2;


    protected static int BASE_PACKET_LENGTH = SRC_ADD_COUNTER + DEST_ADD_COUNTER + CMD_COUNTER + ADD_VAR_COUNTER;

    public static byte READ_CMD = 0x52; //(R for manual read)
    public static byte WRITE_CMD = 0x57; //(W for write)

    protected int cIndex;


    // CMD => command can be one of READ_CMD, WRITE_CMD and AUTO_CMD
    protected byte cmd;
    protected byte[] add_var = new byte[ADD_VAR_COUNTER];
    protected byte[] crc = new byte[CRC_COUNTER];

    protected byte[] packet;

    @SuppressWarnings("Since15")
    protected AbstractPacket toObject(byte[] data) throws Exception {

        cIndex = SRC_ADD_COUNTER + DEST_ADD_COUNTER;

        this.cmd = Arrays.copyOfRange(data, cIndex, cIndex += CMD_COUNTER)[0];
        this.add_var = Arrays.copyOfRange(data, cIndex, cIndex += ADD_VAR_COUNTER);
        return this;
    }

    protected byte[] toRaw() throws Exception {
        addCRC();
        return packet;
    }

    private void addCRC() throws Exception {
        if (this.packet != null) {
            try {
                byte[] crc = CRC.crc16(this.packet);
                this.packet = ArrayUtils.addAll(this.packet,crc);
            } catch (Exception e) {
                throw e;
            }
        } else
            throw new Exception("null packet!");

    }


}
