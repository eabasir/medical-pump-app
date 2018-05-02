package com.ansari.medicalpump.network;

import com.ansari.medicalpump.network.structs.mp.MPDeviceStatus;
import com.ansari.medicalpump.network.structs.mp.MPExecute;
import com.ansari.medicalpump.network.structs.mp.MPInjectionMode;
import com.ansari.medicalpump.network.structs.mp.MPInjectionSpeed;
import com.ansari.medicalpump.network.structs.mp.MPInjectionVolume;

/**
 * Created by Eabasir on 8/31/2017.
 */

public interface OnDataReceivedListener {

    void onConnectionCreated();

    void onWriteMPExecute(MPExecute packet);
    void onReadMPExecute(MPExecute packet);
    void onWriteInjectionSpeed(MPInjectionSpeed packet);
    void onReadInjectionSpeed(MPInjectionSpeed packet);
    void onWriteInjectionVolume(MPInjectionVolume packet);
    void onReadInjectionVolume(MPInjectionVolume packet);
    void onReadDeviceStatus(MPDeviceStatus packet);
    void onReadInjectionMode(MPInjectionMode packet);

}
