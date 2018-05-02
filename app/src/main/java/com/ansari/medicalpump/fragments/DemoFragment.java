package com.ansari.medicalpump.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ansari.medicalpump.R;
import com.ansari.medicalpump.activities.Demo;
import com.ansari.medicalpump.network.AbstractPacket;
import com.ansari.medicalpump.network.NetworkManager;
import com.ansari.medicalpump.network.OnDataReceivedListener;
import com.ansari.medicalpump.network.structs.mp.MPDeviceStatus;
import com.ansari.medicalpump.network.structs.mp.MPExecute;
import com.ansari.medicalpump.network.structs.mp.MPInjectionMode;
import com.ansari.medicalpump.network.structs.mp.MPInjectionSpeed;
import com.ansari.medicalpump.network.structs.mp.MPInjectionVolume;


public class DemoFragment extends Fragment implements OnDataReceivedListener, View.OnClickListener {


    View view;
    CoordinatorLayout coordinatorLayout;
    ToggleButton tglExecute;
    SeekBar seekSpeed;
    TextView txtSpeed;
    SeekBar seekVolume;
    TextView txtVolume;
    TextView txtBat;
    TextView txtEnd;
    TextView txtPress;
    TextView txtPlacement;
    TextView txtMode;

    NetworkManager networkManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        super.onCreateView(inflater, container, savedInstanceState);


        view = inflater.inflate(R.layout.fragment_demo, container, false);

        coordinatorLayout = ((Demo) getActivity()).coordinatorLayout;


        tglExecute = (ToggleButton) view.findViewById(R.id.tglExecute);
        tglExecute.setOnClickListener(this);

        txtSpeed = (TextView) view.findViewById(R.id.txtSpeed);

        seekSpeed = (SeekBar) view.findViewById(R.id.seekSpeed);

        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean byUser) {
                txtSpeed.setText(i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {

                    int i = seekBar.getProgress();

                    networkManager.send(new MPInjectionSpeed(i, AbstractPacket.WRITE_CMD).toRaw());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        txtVolume = (TextView) view.findViewById(R.id.txtVolume);

        seekVolume = (SeekBar) view.findViewById(R.id.seekVol);

        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean byUser) {
                txtVolume.setText(i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                try {

                    networkManager.send(new MPInjectionVolume(i, AbstractPacket.WRITE_CMD).toRaw());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        txtBat = (TextView) view.findViewById(R.id.txtBat);
        txtEnd = (TextView) view.findViewById(R.id.txtEnd);
        txtPress = (TextView) view.findViewById(R.id.txtPress);
        txtPlacement = (TextView) view.findViewById(R.id.txtPlacement);
        txtMode = (TextView) view.findViewById(R.id.txtMode);

        networkManager = NetworkManager.getInstance(DemoFragment.this);

        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        networkManager.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        networkManager.shutDown();
    }

    @Override
    public void onConnectionCreated() {


        Snackbar.make(coordinatorLayout, "اتصال برقرار شد", Snackbar.LENGTH_LONG).show();
        try {
            Log.v(DemoFragment.class.getName(), "Get Device Readable...");
            networkManager.send(new MPExecute(-1, AbstractPacket.READ_CMD).toRaw());
            networkManager.send(new MPInjectionSpeed(-1, AbstractPacket.READ_CMD).toRaw());
            networkManager.send(new MPInjectionVolume(-1, AbstractPacket.READ_CMD).toRaw());
            networkManager.send(new MPDeviceStatus().toRaw());
//            networkManager.send(new MPInjectionMode().toRaw());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onWriteMPExecute(MPExecute packet) {
        if (tglExecute.isChecked())
            Snackbar.make(coordinatorLayout, "دستگاه فعال است", Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(coordinatorLayout, "دستگاه غیر فعال است", Snackbar.LENGTH_LONG).show();


    }

    @Override
    public void onReadMPExecute(MPExecute packet) {
        if (packet.getExecute() == 0)
            tglExecute.setChecked(false);
        else
            tglExecute.setChecked(true);

    }

    @Override
    public void onWriteInjectionSpeed(MPInjectionSpeed packet) {
    }

    @Override
    public void onReadInjectionSpeed(MPInjectionSpeed packet) {

        int progress = packet.getIS();
        seekSpeed.setProgress(progress);
        txtSpeed.setText(progress + "");

    }

    @Override
    public void onWriteInjectionVolume(MPInjectionVolume packet) {

    }

    @Override
    public void onReadInjectionVolume(MPInjectionVolume packet) {

        int progress = packet.getIV();
        seekVolume.setProgress(progress);
        txtVolume.setText(progress + "");
    }

    @Override
    public void onReadDeviceStatus(MPDeviceStatus packet) {

        txtBat.setText(packet.getBattry() + " %");

        if (packet.getEnd())
            txtEnd.setTextColor(Color.rgb(0, 250, 0));
        else
            txtEnd.setTextColor(Color.rgb(250, 0, 0));

        if (packet.getPress())
            txtPress.setTextColor(Color.rgb(0, 250, 0));
        else
            txtPress.setTextColor(Color.rgb(250, 0, 0));

        txtPlacement.setText(packet.getPlacement() + " %");

    }

    @Override
    public void onReadInjectionMode(MPInjectionMode packet) {
        txtMode.setText(packet.getMode() + "");
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == tglExecute.getId()) {
            try {
                if (tglExecute.isChecked())
                    networkManager.send(new MPExecute(1, AbstractPacket.WRITE_CMD).toRaw());

                else
                    networkManager.send(new MPExecute(0, AbstractPacket.WRITE_CMD).toRaw());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
