package com.ansari.medicalpump.network.structs.mp;


import com.ansari.medicalpump.network.AbstractPacket;
import com.ansari.medicalpump.network.utils.PacketUtility;

import java.util.Arrays;

/**
 * Created by Eabasir on 8/31/2017.
 */

public class MPInjectionVolume extends AbstractPacket {

    private static int IV_COUNTER = 2;


    private int iv;

    public final static  int ADDRESS = 3;

    public int getIV() {
        return iv;
    }


    /**
     * this constructor is used when byte array needs to be made from object for write on device
     * @param iv : 2 bytes integer => 0 or 65535
     * @param CMD : Read 0x57 or Write 0x87
     */
    public MPInjectionVolume(int iv , byte CMD){

        this.cmd = CMD;
//        this.is = is * (65535 / 250);
        this.iv = iv;

    }


    /**
     * This constructor is used when object needs to be made of byte array
     */
    public MPInjectionVolume(){

    }

    @SuppressWarnings("Since15")
    @Override
    public MPInjectionVolume toObject(byte[] data) throws Exception {
        try
        {
            super.toObject(data);
            if (cmd == READ_CMD ) {
                this.iv = PacketUtility.unsignedIntFromByteArray(Arrays.copyOfRange(data, cIndex, cIndex += IV_COUNTER));
            }
            return this;
        }
        catch (Exception e)
        {
            throw e;
        }

    }


    @Override
    public byte[] toRaw() throws Exception {

        if (this.cmd == WRITE_CMD)
            packet = new byte[BASE_PACKET_LENGTH+ IV_COUNTER];
        else
            packet = new byte[BASE_PACKET_LENGTH];

        cIndex = SRC_ADD_COUNTER + DEST_ADD_COUNTER;

        packet[cIndex] = this.cmd;
        cIndex += CMD_COUNTER;


        byte[] address_bytes = PacketUtility.convertIntToTwoByte(MPInjectionVolume.ADDRESS);
        // LSB must be sent first => ---- LSB ---- MSB
        packet[cIndex] = address_bytes[1];
        packet[cIndex +1 ] = address_bytes[0];

        cIndex += ADD_VAR_COUNTER;

        if (this.cmd == WRITE_CMD)
        {

            byte[] is_bytes = PacketUtility.convertIntToTwoByte(iv);
            packet[cIndex] = is_bytes[0];
            packet[cIndex + 1] = is_bytes[1];
            cIndex += IV_COUNTER;
        }

        return super.toRaw();


    }

}
