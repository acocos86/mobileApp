package com.example.heartbitmobile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewUserDataActivity extends AppCompatActivity {


    TextView bluetooth;
    TextView pulseVal;
    TextView ecgVal;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;

    BluetoothSocket mmSocket;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    volatile boolean stopWorker;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    String userName;
    Number userId;
    Number doctorId;
    Number patientId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_data);

        // variables

        bluetooth = (TextView)findViewById(R.id.label);
        pulseVal = (TextView)findViewById(R.id.pulse);
        ecgVal = (TextView)findViewById(R.id.ecg);
        Button recData = (Button)findViewById(R.id.recordingButton);
        Button stopRec = (Button)findViewById(R.id.stopRecordingButton);

        recData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
                {
                    try
                {
                   findBT();
                   openBT();
                }
                catch (IOException ex) { }
           }
        });

        //Close button
        stopRec.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    closeBT();
                }
                catch (IOException ex) { }
            }
        });

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String usernameGlobal = intent.getStringExtra(MainActivity.USERID);

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                .baseUrl("https://heartbitfis.azurewebsites.net")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        PacientService pacientService = retrofit.create(PacientService.class);
        Call<List<Pacient>> value = pacientService.getPacient(Long.parseLong(usernameGlobal));
        value.enqueue(new Callback<List<Pacient>>() {
            @Override
            public void onResponse(Call<List<Pacient>> call, Response<List<Pacient>> response) {
                userName = response.body().get(0).name;
                userId = response.body().get(0).id;
                doctorId = response.body().get(0).doctorId;
                patientId = response.body().get(0).patientId;
                // Capture the layout's TextView and set the string as its text
                TextView username = findViewById(R.id.username);
                username.setText(userName);
            }

            @Override
            public void onFailure(Call<List<Pacient>> call, Throwable t) {
                System.out.println(" stop ");
            }
        });







    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            bluetooth.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("ESP32"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        bluetooth.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        //mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket = createBluetoothSocket(mmDevice);

        mmSocket.connect();

        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();


        beginListenForData();

        bluetooth.setText("Bluetooth Opened");


    }
    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        bluetooth.setText("Bluetooth Closed");
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        //String address = "84:CC:A8:5C:39:E2";

        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            //Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }

        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }
    void sendRecData(int level, String parId) {
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                .baseUrl("https://heartbitfis.azurewebsites.net")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        Intent intent = getIntent();
        String userId = intent.getStringExtra(MainActivity.USERID);

        RecData recData = new RecData();
        recData.patientId = patientId.toString();
        recData.date = Calendar.getInstance().getTime();
        recData.parameterId = parId;  // 1 = Puls, 2 = ECG
        recData.level = level;
        RecDataSevice recDataService = retrofit.create(RecDataSevice.class);
        Call<ResponseBody> value = recDataService.updateRecData(recData);
            value.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });


    }
    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            RecData recData = new RecData();
                                            String [] formatData = data.split(";");
                                            float pulse = Float.parseFloat(formatData[1]);
                                            int pulseInt = (int) pulse;
                                            int ecgInt = Integer.parseInt(formatData[0]);
                                            pulseVal.setText(formatData[1]);
                                            ecgVal.setText(formatData[0]);


                                            sendRecData(pulseInt, "1");
                                            sendRecData(ecgInt, "2");
                                            //pulseVal.setText(data);
                                            //recData.setPulse(formatData[1]);
                                            //recData.setEcg(formatData[2]);
                                            //sendDataHttp(recData);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }





}