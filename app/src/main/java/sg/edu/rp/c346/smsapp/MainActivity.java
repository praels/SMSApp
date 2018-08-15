package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.os.Build.VERSION_CODES.KITKAT;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button btnSend;
    Button btnVia;
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.buttonSend);
        btnVia = findViewById(R.id.buttonMessage);

        checkPermission();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("com.example.broadcast.MY_BROADCAST");
        this.registerReceiver(br,filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] list = etTo.getText().toString().split(",");
                String message = etContent.getText().toString();
                for ( String item : list){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(item, null, message, null,
                            null);
                    etContent.getText().clear();
                    Toast toast = Toast.makeText(getApplicationContext(),"Message Sent",Toast
                            .LENGTH_LONG);
                    toast.show();
                }

            }
        });

        btnVia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etContent.getText().toString();
                String to = etTo.getText().toString();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage
                            (MainActivity.this);
                    Uri uri = Uri.parse("smsto:"+to);
                    Intent intents = new Intent(Intent.ACTION_SENDTO,uri);
                    intents.putExtra("sms_body",message);
                    Log.d("MainActivity",message);
                    startActivity(intents);

                    if(defaultSmsPackageName != null){
                        intents.setPackage(defaultSmsPackageName);
                    }
                    startActivity(intents);

                } else{

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + etTo.getText().toString()));
                    intent.putExtra("sms_body",message);
                    Log.d("MainActivity",message);
                    startActivity(intent);
                }
            }
        });
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        int permissionReadSMS = ContextCompat.checkSelfPermission(this,Manifest.permission
                .READ_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED &&
                permissionReadSMS!=PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
}