package com.example.android.printtesttwo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;

public class DuplicateBill extends Activity implements Runnable {
    TextView lblaccount, lblcustomer, lbladdress, lblcurrent, lblmonth, lbltotal, lblarrears,lblvat,lblamount, myLabel;
    TextView textaccount, textcustomer, textaddress, textmonth, textcurrent, textarrears, texttotal,textvat,textamount,title;
    String z = "";







    //  Typeface font;
 //   Button btnviewall;
    Button btnview, btnprint;
   // ListView lstcountry;
    EditText edtid;

    //private Bitmap btMap = null;
    ImageView logo;

    OutputStream mmOutputStream;

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;





    /*********** CONNECTION DATABASE VARIABLES **************/

    String usernameS;
    String datets;
    String call="server ip", db="database name", un="database username", passwords="database password";
    Connection connect;
    ResultSet rs;
    @SuppressLint("NewApi")
    private Connection CONN(String _user, String _pass, String _DB,
                            String _server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://server ip/database name;user=database username;password=database password;useNTLMv2=true;integratedSecurity=true";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duplicate_bill);
        lblaccount = (TextView) findViewById(R.id.lbl_account_num);
        lblcustomer = (TextView) findViewById(R.id.lbl_customer_name);
        lbladdress = (TextView) findViewById(R.id.lbl_address);
        lblcurrent = (TextView) findViewById(R.id.lbl_current_charge);
        lblmonth = (TextView) findViewById(R.id.lbl_month);
        lbltotal = (TextView) findViewById(R.id.lbl_total);
        lblarrears = (TextView) findViewById(R.id.lbl_arrears);
        lblvat = (TextView) findViewById(R.id.lbl_vat);
        lblamount = (TextView) findViewById(R.id.lbl_amount);



        textaccount = (TextView) findViewById(R.id.text_account_num);
        textcustomer = (TextView) findViewById(R.id.text_customer_name);
        textaddress = (TextView) findViewById(R.id.text_address);
        textcurrent = (TextView) findViewById(R.id.text_current_charge);
        textmonth = (TextView) findViewById(R.id.text_month);
        textarrears = (TextView) findViewById(R.id.text_arrears);
        texttotal = (TextView) findViewById(R.id.text_total);
        textvat = (TextView) findViewById(R.id.text_vat);
        textamount = (TextView) findViewById(R.id.text_amount);
        myLabel = (TextView) findViewById(R.id.label);
        title = (TextView) findViewById(R.id.text_bill_title);

        logo = (ImageView) findViewById(R.id.phedlogoo);
       // logo.setImageBitmap(btMap);

       // lstcountry = (ListView) findViewById(R.id.lstcountry);
      // btnviewall = (Button) findViewById(R.id.btnviewall);

        btnview = (Button) findViewById(R.id.btnview);
      //  btnprint = (Button) findViewById(R.id.Print);

        edtid = (EditText) findViewById(R.id.edtid);


        /************* CONNECTION DATABASE VARIABLES ***************/

        connect = CONN(un, passwords, db, call);

        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(DuplicateBill.this, "Message1", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(DuplicateBill.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });


        // send data typed by the user to be printed
        /*
        mPrint = (Button)findViewById(R.id.Print);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData();
                } catch (IOException ex) {
                }
            }
        });*/

        mDisc = (Button) findViewById(R.id.close);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        });

        mPrint = (Button) findViewById(R.id.Print);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket.getOutputStream();

                            byte[] printformat = new byte[]{0x1B,0x21,0x03};
                            os.write(printformat);


                            // Print QR code




                            printPhoto(R.drawable.phedlogo);
                            String titl = "      PHED DUPLICATE BILL\n\n";
                            String acct = "Account Number:" + lblaccount.getText().toString();
                            acct += "\n\n";
                            String name = "Name:" + lblcustomer.getText().toString();
                            name += "\n\n";
                            String address = "Address:" + lbladdress.getText().toString();
                            address += "\n\n";
                            String year = "Year/Month:" + lblmonth.getText().toString();
                            year += "\n\n";
                            String currentcharge = "Current Charge(=N=):" + lblcurrent.getText().toString();
                            currentcharge += "\n\n";
                            String vat = "VAT(=N=):" + lblvat.getText().toString();
                            vat += "\n\n";
                            String arrears = "Arrears(=N=):" + lblarrears.getText().toString();
                            arrears += "\n\n";
                            String amount = "Amount(=N=):" + lblamount.getText().toString();
                            amount += "\n\n";
                            String total = "TOTAL DUE(=N=):" + lbltotal.getText().toString();
                            total += "\n\n";
                            String end = "--------------------------------\n";
                            end += "\n";
                            String slogan = "      NO PAY...NO SERVICE";
                            slogan += "\n";
                            String enda = "--------------------------------\n";
                            enda += "\n\n";


/*
                            BILL =  " Print a Sample File\n" +
                                    " \n" +
                                    "Acct Number: 81777777799\n " +
                                    "Customer's Name: John Bull \n"+
                                    "Address: John Bull's address   \n" +
                                    " \n" +
                                    "Bill Covering the Month of:JULY 2017      \n" +
                                    " \n" +
                                    "Current Charge:     N9,000.42 \n"  +
                                    " \n" +
                                    "Arrears:            N29,000.10 \n" +
                                    "Pay Total Due now:  N38,000.52 \n" +
                                    "  \n  " +
                                    "No Pay No Service";
                            BILL = BILL
                                    + "-----------------------------------------------\n";


                            /*BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "Item", "Qty", "Rate", "Totel");
                            BILL = BILL + "\n";
                            BILL = BILL
                                    + "-----------------------------------------------";
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-001", "5", "10", "50.00");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-002", "10", "5", "50.00");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-003", "20", "10", "200.00");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-004", "50", "10", "500.00");

                            BILL = BILL
                                    + "\n-----------------------------------------------";
                            BILL = BILL + "\n\n ";

                            BILL = BILL + "                   Total Qty:" + "      " + "85" + "\n";
                            BILL = BILL + "                   Total Value:" + "     " + "700.00" + "\n";

                            BILL = BILL
                                    + "-----------------------------------------\n";
                            BILL = BILL + "\n\n "; */


                            os.write(titl.getBytes());
                            os.write(acct.getBytes());
                            os.write(name.getBytes());
                            os.write(address.getBytes());
                            os.write(year.getBytes());
                            os.write(currentcharge.getBytes());
                            os.write(vat.getBytes());
                            os.write(arrears.getBytes());
                            os.write(amount.getBytes());
                            os.write(total.getBytes());
                            os.write(end.getBytes());
                            os.write(slogan.getBytes());
                            os.write(enda.getBytes());

                            //This is printer specific code you can comment ==== > Start

                            // Setting height
                            int gs = 29;
                            os.write(intToByteArray(gs));
                            int h = 104;
                            os.write(intToByteArray(h));
                            int n = 162;
                            os.write(intToByteArray(n));

                            // Setting Width
                            int gs_width = 29;
                            os.write(intToByteArray(gs_width));
                            int w = 119;
                            os.write(intToByteArray(w));
                            int n_width = 2;
                            os.write(intToByteArray(n_width));

                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                };
                t.start();
            }
        });

/*
        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        */


        btnview.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {


                if (edtid == null)
                  //  Toast.makeText(DuplicateBill.this, "Please Enter an Account number", Toast.LENGTH_SHORT).show();
                   z= "Please Enter an Account number";

                else {

                    try {

                        //PreparedStatement statement = connect.prepareStatement("EXEC viewCountry '"+edtid.getText().toString()+"'");
                        //final ArrayList list = new ArrayList();
                        String query = "select * from dbo.SpotBill where AccountNo = '" + edtid.getText() + "'";
                        Statement stmt = connect.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();

                        lblaccount.setText(rs.getString("AccountNo"));
                        lblcustomer.setText(rs.getString("Name"));
                        lbladdress.setText(rs.getString("Address"));
                        lblmonth.setText(rs.getString("YearMonth"));
                        lblcurrent.setText(rs.getString("CurrentCharge"));
                        lblvat.setText(rs.getString("VAT"));
                        lblarrears.setText(rs.getString("Arrears"));
                        lblamount.setText(rs.getString("Amount"));
                        lbltotal.setText(rs.getString("TotalDue"));


                    }

                    catch (SQLException z) {

                        Toast.makeText(DuplicateBill.this, z.getMessage().toString(),
                        Toast.LENGTH_LONG).show();

                    }
                }

            }

        });




    }


    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                OutputStream os = mBluetoothSocket.getOutputStream();
                os.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    //print text
    private void printText(String msg) {
        try {
            // Print normal text

            OutputStream os = mBluetoothSocket.getOutputStream();
            byte[] printformat = new byte[]{0x1B,0x21,0x03};
            os.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            OutputStream os = mBluetoothSocket.getOutputStream();
            byte[] printformat = new byte[]{0x1B,0x21,0x03};
            os.write(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData() throws IOException {
        try {

            // the text typed by the user
            String msg = lblaccount.getText().toString();
            msg += "\n";

            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            myLabel.setText("Data Sent");

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread( this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(DuplicateBill.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(DuplicateBill.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(DuplicateBill.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();

    }



}


