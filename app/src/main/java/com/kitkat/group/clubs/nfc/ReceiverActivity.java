package com.kitkat.group.clubs.nfc;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.group.clubs.R;
import com.kitkat.group.clubs.data.Club;

import static java.sql.DriverManager.println;

public class ReceiverActivity extends AppCompatActivity {

    BroadcastReceiver BroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private TextView tvIncomingMessage, tvrClubName, tvrClubId, tvrUserName, tvrUserId,TextV7;
    private NfcAdapter nfcAdapter;
    private DatabaseReference db;
    final FirebaseUser fa = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth mAuth;
    private Club club;
    String inMessage, first, second, third, fourth, VerClubOwnerId, OwnerClubId, OwnerUserId;
    Button ReceiverButton;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        db = FirebaseDatabase.getInstance().getReference();

        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }


        BroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(getApplicationContext(),"DO nothing",Toast.LENGTH_LONG).show();
            }
        };

        initViews();

        registerReceiver(BroadcastReceiver, new IntentFilter());


        
        ReceiverButton.setOnClickListener(v -> {
            verification();
            //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
        });

    }

    // need to check NfcAdapter for nullability. Null means no NFC support on the device
    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

    private void initViews() {

        //this.tvIncomingMessage = findViewById(R.id.tv_in_message);
        this.tvrClubName = findViewById(R.id.tvr_clubName);
        this.tvrClubId = findViewById(R.id.tvr_clubId);
        this.tvrUserName = findViewById(R.id.tvr_userName);
        this.tvrUserId = findViewById(R.id.tvr_userId);
        this.ReceiverButton = findViewById(R.id.receiverButton);
    }

    public void verification() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child("clubs").child(second);
                club = ds.getValue(Club.class);

                if (dataSnapshot.child("members-clubs").child(fourth).child(second).exists() &&
                        club.getClubOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(ReceiverActivity.this, "YOU ARE ALLOWED", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ReceiverActivity.this, "YOU ARE NOT A MEMBER", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*
        assert fa != null;
        if(dataSnapshot.child("members-clubs").child(fourth).child(second).exists() ||
                dataSnapshot.child("clubs-members").child(second).child(fourth).exists()){
            Toast.makeText(this, "YOU ARE ALLOWED", Toast.LENGTH_LONG).show();
        }
        */
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // also reading NFC message from here in case this activity is already started in order
        // not to start another instance of this activity
        receiveMessageFromDevice(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // foreground dispatch should be enabled here, as onResume is the guaranteed place where app
        // is in the foreground
        enableForegroundDispatch(this, this.nfcAdapter);
        receiveMessageFromDevice(getIntent());
        registerReceiver(BroadcastReceiver, new IntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, this.nfcAdapter);
        unregisterReceiver(BroadcastReceiver);
    }

    private void receiveMessageFromDevice(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord ndefRecord_0 = inNdefRecords[0];

            inMessage = new String(ndefRecord_0.getPayload());
            display();


//
//            tvrClubName.setText(parts[0]);
//            tvrClubId.setText(parts[1]);
//            tvrUserName.setText(parts[2]);
//            tvrUserId.setText(parts[3]);
//            System.err.println(parts[0]);
//            System.err.println(parts[1]);
//            System.err.println(parts[2]);
//            System.err.println(parts[3]);

        }
    }
    public void display(){
        //System.out.println(inMessage);
        Log.d(inMessage,"HERE");
        //this.tvIncomingMessage.setText(inMessage);
        String[] parts = inMessage.split("    ");
        first=parts[0];
        second=parts[1];
        third=parts[2];
        fourth=parts[3];
        tvrClubName.setText(first);
        tvrClubId.setText(second);
        tvrUserName.setText(third);
        tvrUserId.setText(fourth);


//        Log.d(parts[0],"HERE");
//        Log.d(first,"HERE");
//        Log.d(parts[1],"HERE");
//        Log.d(second,"HERE");
//        Log.d(parts[2],"HERE");
//        Log.d(third,"HERE");
//        Log.d(parts[3],"HERE");
//        Log.d(fourth,"HERE");
    }

    // Foreground dispatch holds the highest priority for capturing NFC intents
    // then go activities with these intent filters:
    // 1) ACTION_NDEF_DISCOVERED
    // 2) ACTION_TECH_DISCOVERED
    // 3) ACTION_TAG_DISCOVERED

    // always try to match the one with the highest priority, cause ACTION_TAG_DISCOVERED is the most
    // general case and might be intercepted by some other apps installed on your device as well

    // When several apps can match the same intent Android OS will bring up an app chooser dialog
    // which is undesirable, because user will most likely have to move his device from the tag or another
    // NFC device thus breaking a connection, as it's a short range

    public void enableForegroundDispatch(AppCompatActivity activity, NfcAdapter adapter) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters


        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException ex) {
            throw new RuntimeException("Check your MIME type");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public void disableForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
