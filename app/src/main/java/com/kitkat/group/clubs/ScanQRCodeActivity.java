package com.kitkat.group.clubs;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.group.clubs.clubs.ViewClubActivity;

public class ScanQRCodeActivity extends AppCompatActivity {

    private static final String TAG = "ScanQRCodeActivity";
    private DatabaseReference databaseRef;
    private String clubId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

                if (clubId != null) {
                    databaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.child("clubs-members").child(clubId).child(contents).exists()) {
                                    Toast.makeText(ScanQRCodeActivity.this, "User is a club member", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ScanQRCodeActivity.this, "User is NOT a club member", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(ScanQRCodeActivity.this, "User is NOT a club member", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ScanQRCodeActivity.this, "User is NOT a club member", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    Intent intent = new Intent(this, ViewClubActivity.class);
                    intent.putExtra("clubId", contents);
                    startActivity(intent);
                    finish();
                }
            }
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started " + TAG);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        clubId = getIntent().getStringExtra("clubId");

        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }
}
