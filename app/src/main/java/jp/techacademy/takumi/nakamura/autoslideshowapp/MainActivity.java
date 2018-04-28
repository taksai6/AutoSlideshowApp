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

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Button mNextButton;
    Button mBackButton;
    Button mPlaybackButton;
    Cursor cursor;
    Timer mTimer;
    double mTimerSec = 0.0;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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


    private void cursored() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);

    }

    private void getContentsInfo() {


        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );


        if (cursor.moveToFirst()) {
            cursored();
        }
        ;

        mNextButton = (Button) findViewById(R.id.next_button);
        mBackButton = (Button) findViewById(R.id.back_button);
        mPlaybackButton = (Button) findViewById(R.id.playback_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToNext()) {
                    cursored();
                } else {
                    cursor.moveToFirst();
                    cursored();
                }

            }

            ;
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPrevious()) {
                    cursored();
                } else {
                    cursor.moveToLast();
                    cursored();
                }
                ;

            }

        });

        mPlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer == null) {

                    mTimer = new Timer();

                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mTimerSec += 0.1;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (cursor.moveToNext()) {
                                        cursored();
                                    } else {
                                        cursor.moveToFirst();
                                        cursored();
                                    }

                                }

                            });

                        }

                    }, 2000, 2000);
                    mPlaybackButton.setText("停止");
                    mNextButton.setEnabled(false);
                    mBackButton.setEnabled(false);

                } else {
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;

                    }
                    mPlaybackButton.setText("再生");
                    mNextButton.setEnabled(true);
                    mBackButton.setEnabled(true);

                }
                ;


            }

            ;


        });


    }


}