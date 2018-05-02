package com.ansari.medicalpump.network.structs.mp;


import com.ansari.medicalpump.network.AbstractPacket;
import com.ansari.medicalpump.network.utils.PacketUtility;

import java.util.Arrays;

/**
 * Created by Eabasir on 8/31/2017.
 */

public class MPInjectionMode extends AbstractPacket {

    private static int MODE_COUNTER = 1;


    private int mode;

    public final static int ADDRESS = 5;

    /**
     * Injection Mode is read only
     */
    public MPInjectionMode() {

        this.cmd = AbstractPacket.READ_CMD;

    }

    public String getMode() {
        switch (mode) {

            case 1:
                return "Syringe Failed";
            case 2:
                return "Syringe Pressured";
            case 3:
                return "Syringe Normal";
            case 4:
                return "Syringe Manual";
            default:
                return "";
        }
    }


    @SuppressWarnings("Since15")
    @Override
    public MPInjectionMode toObject(byte[] data) throws Exception {
        try {
            super.toObject(data);
            this.mode = (int) data[cIndex];
            cIndex += MODE_COUNTER;

            return this;
        } catch (Exception e) {
            throw e;
        }

    }


    @Override
    public byte[] toRaw() throws Exception {

        packet = new byte[BASE_PACKET_LENGTH];
        cIndex = SRC_ADD_COUNTER + DEST_ADD_COUNTER;

        packet[cIndex] = this.cmd;
        cIndex += CMD_COUNTER;


        byte[] address_bytes = PacketUtility.convertIntToTwoByte(MPInjectionMode.ADDRESS);
        // LSB must be sent first => ---- LSB ---- MSB
        packet[cIndex] = address_bytes[1];
        packet[cIndex + 1] = address_bytes[0];

        cIndex += ADD_VAR_COUNTER;

        return super.toRaw();


    }


}
