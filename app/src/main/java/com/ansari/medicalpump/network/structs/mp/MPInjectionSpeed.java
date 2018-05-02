package com.ansari.medicalpump.network.structs.mp;


import com.ansari.medicalpump.network.AbstractPacket;
import com.ansari.medicalpump.network.utils.PacketUtility;
import com.ansari.medicalpump.utils.Tools;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * Created by Eabasir on 8/31/2017.
 */

public class MPInjectionSpeed extends AbstractPacket {

    private static int IS_COUNTER = 2;


    private int is;

    public final static  int ADDRESS = 2;

    public int getIS() {
        return is;
    }


    /**
     * this constructor is used when byte array needs to be made from object for write on device
     * @param is : 2 bytes integer => 0 or 65535
     * @param CMD : Read 0x57 or Write 0x87
     */
    public MPInjectionSpeed(int is , byte CMD){

        this.cmd = CMD;
//        this.is = is * (65535 / 250);
        this.is = is;

    }


    /**
     * This constructor is used when object needs to be made of byte array
     */
    public MPInjectionSpeed(){

    }

    @SuppressWarnings("Since15")
    @Override
    public MPInjectionSpeed toObject(byte[] data) throws Exception {
        try
        {
            super.toObject(data);
            if (cmd == READ_CMD ) {
                // invert LSB and MSB
                byte[] temp =Arrays.copyOfRange(data, cIndex, cIndex += IS_COUNTER);
                byte[] val = new byte[2];
                val[1] = temp[0];
                val[0] = temp[1];

                this.is = PacketUtility.unsignedIntFromByteArray(val);
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
            packet = new byte[BASE_PACKET_LENGTH+ IS_COUNTER];
        else
            packet = new byte[BASE_PACKET_LENGTH];

        cIndex = SRC_ADD_COUNTER + DEST_ADD_COUNTER;

        packet[cIndex] = this.cmd;
        cIndex += CMD_COUNTER;


        byte[] address_bytes = PacketUtility.convertIntToTwoByte(MPInjectionSpeed.ADDRESS);
        // LSB must be sent first => ---- LSB ---- MSB
        packet[cIndex] = address_bytes[1];
        packet[cIndex +1 ] = address_bytes[0];

        cIndex += ADD_VAR_COUNTER;

        if (this.cmd == WRITE_CMD)
        {

            byte[] is_bytes = PacketUtility.convertIntToTwoByte(is);
            // LSB must be sent first => ---- LSB ---- MSB
            packet[cIndex +1] = is_bytes[0];
            packet[cIndex ] = is_bytes[1];
            cIndex += IS_COUNTER;
        }

        return super.toRaw();


    }

}
