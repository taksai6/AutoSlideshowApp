package jp.techacademy.takumi.nakamura.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Timer mTimer;
    TextView mTimerText;
    double mTimerSec = 0.0;
    Cursor cursor = null;

    Handler mHandler = new Handler();

    Button mNextButton;
    Button mBackButton;
    Button mPlaybackButton;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private  void cursored() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);

        cursor.close();
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_button) {
            cursor.moveToNext();
            cursored();
        } else if (v.getId() == R.id.back_button) {
            cursor.moveToPrevious();
            cursored();
        } else if (v.getId() == R.id.playback_button) {

        }
        cursor.close();
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimerSec += 0.1;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTimerText.setText(String.format("%.1f", mTimerSec));
                        }
                    });
                }
            }, 100, 100);
        }
        mPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mTimerSec = 0.0;
                mTimerText.setText(String.format("%.1f", mTimerSec));

                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener((View.OnClickListener) this);

        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener((View.OnClickListener) this);

        mPlaybackButton = (Button) findViewById(R.id.playback_button);
        mPlaybackButton.setOnClickListener((View.OnClickListener) this);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }


    private void getContentsInfo() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null


        );

        if (cursor.moveToFirst()) {
            cursored();
        }

    }

}



