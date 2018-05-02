package com.ansari.medicalpump.network.structs.mp;


import com.ansari.medicalpump.network.AbstractPacket;
import com.ansari.medicalpump.network.utils.PacketUtility;

import java.util.Arrays;

/**
 * Created by Eabasir on 8/31/2017.
 */

public class MPDeviceStatus extends AbstractPacket {

    private static int BATTRY_COUNTER = 1;
    private static int END_COUNTER = 1;
    private static int PRESS_COUNTER = 1;
    private static int PLACEMENT_COUNTER = 2;


    private float battry;
    private boolean end;
    private boolean press;
    private float placement;

    public final static int ADDRESS = 4;

    public float getBattry() {
        return battry;
    }

    public boolean getEnd() {
        return end;
    }

    public boolean getPress() {
        return press;
    }

    public float getPlacement() {
        return placement;
    }


    /**
     * device status is read only
     */
    public MPDeviceStatus() {

        this.cmd = AbstractPacket.READ_CMD;

    }


    @SuppressWarnings("Since15")
    @Override
    public MPDeviceStatus toObject(byte[] data) throws Exception {
        try {
            super.toObject(data);
            this.battry = ((data[cIndex] & 0xFF) / 250f) * 100;
            cIndex += BATTRY_COUNTER;
            this.end = (int) data[cIndex] == 1;
            cIndex += END_COUNTER;
            this.press = (int) data[cIndex] == 1;
            cIndex += PRESS_COUNTER;
            this.placement = ((data[cIndex] & 0xFF) / 250f) * 100;
            cIndex += PLACEMENT_COUNTER;

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


        byte[] address_bytes = PacketUtility.convertIntToTwoByte(MPDeviceStatus.ADDRESS);
        // LSB must be sent first => ---- LSB ---- MSB
        packet[cIndex] = address_bytes[1];
        packet[cIndex + 1] = address_bytes[0];

        cIndex += ADD_VAR_COUNTER;

        return super.toRaw();


    }


}
