package com.ansari.medicalpump.network.structs.mp;


import com.ansari.medicalpump.network.AbstractPacket;
import com.ansari.medicalpump.network.utils.PacketUtility;

import java.util.Arrays;

/**
 * Created by Eabasir on 8/31/2017.
 */

public class MPExecute extends AbstractPacket {

    private static int EXECUTE_COUNTER = 1;


    private int execute;

    public final static  int ADDRESS = 1;

    public int getExecute() {
        return execute;
    }


    /**
     * this constructor is used when byte array needs to be made from object for write on device
     * @param execute : 0 or 1
     * @param CMD : Read 0x57 or Write 0x87
     */
    public MPExecute(int execute , byte CMD){

        this.cmd = CMD;
        this.execute = execute;

    }


    /**
     * his constructor is used when object needs to be made of byte array
     */
    public MPExecute(){

    }

    @SuppressWarnings("Since15")
    @Override
    public MPExecute toObject(byte[] data) throws Exception {
        try
        {
            super.toObject(data);
            if (cmd == READ_CMD ) {
                this.execute = (int) Arrays.copyOfRange(data, cIndex, cIndex += EXECUTE_COUNTER)[0];
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
            packet = new byte[BASE_PACKET_LENGTH+ EXECUTE_COUNTER];
        else
            packet = new byte[BASE_PACKET_LENGTH];

        cIndex = SRC_ADD_COUNTER + DEST_ADD_COUNTER;

        packet[cIndex] = this.cmd;
        cIndex += CMD_COUNTER;


        byte[] address_bytes = PacketUtility.convertIntToTwoByte(MPExecute.ADDRESS);
        // LSB must be sent first => ---- LSB ---- MSB
        packet[cIndex] = address_bytes[1];
        packet[cIndex +1 ] = address_bytes[0];

        cIndex += ADD_VAR_COUNTER;

        if (this.cmd == WRITE_CMD)
        {
            packet[cIndex] = (byte)this.execute;
            cIndex += EXECUTE_COUNTER;
        }

        return super.toRaw();


    }

}
