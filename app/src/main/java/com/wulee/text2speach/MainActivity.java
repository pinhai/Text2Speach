package com.wulee.text2speach;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.liangmayong.text2speech.OnText2SpeechListener;
import com.liangmayong.text2speech.Text2Speech;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tvContent;
    private ImageView ivPlay, iv_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        tvContent = (TextView)findViewById(R.id.tv);
        final EditText editText = (EditText) findViewById(R.id.et);
        ivPlay = (ImageView)findViewById(R.id.iv_play_msg_content);
        iv_share = (ImageView) findViewById(R.id.iv_share);


        final ScaleAnimation scaleanimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleanimation.setDuration(500);
        scaleanimation.setRepeatCount(ValueAnimator.INFINITE);
        scaleanimation.setRepeatMode(ValueAnimator.INFINITE);
        scaleanimation.setInterpolator(new AccelerateInterpolator());

        Text2Speech.setOnText2SpeechListener(new OnText2SpeechListener() {
            @Override
            public void onCompletion() {
                Log.i("speak","onCompletion");
                scaleanimation.cancel();
                ivPlay.setImageResource(R.mipmap.icon_play);
            }
            @Override
            public void onPrepared() {
                Log.i("speak","onPrepared");
                ivPlay.setImageResource(R.mipmap.icon_voice);
                ivPlay.startAnimation(scaleanimation);
            }
            @Override
            public void onError(Exception e, String s) {
                Log.i("speak","onError");
                scaleanimation.cancel();
                Toast.makeText(MainActivity.this, "出现错误", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStart() {
                Log.i("speak","onStart");
            }
            @Override
            public void onLoadProgress(int i, int i1) {
                Log.i("speak","onLoadProgress");
            }
            @Override
            public void onPlayProgress(int i, int i1) {
                Log.i("speak","onPlayProgress---->"+ i);
            }
        });


        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Text2Speech.isSpeeching()){
                    Text2Speech.pause(MainActivity.this);
                    ivPlay.setImageResource(R.mipmap.icon_pause);
                    scaleanimation.cancel();
                }else{
                    Text2Speech.speech(MainActivity.this,editText.getText().toString(),false);
//                    Text2Speech.speech(MainActivity.this,tvContent.getText().toString(),true);
                }
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileBrowser(UPLOAD_FILE);
            }
        });
    }

    private static final int UPLOAD_FILE = 113;	//上传文件
    private void openFileBrowser(int requestCode){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
//        Intent intent = new Intent(Intent.ACTION_PICK);
        File file = new File(getExternalCacheDir() + Text2Speech.TEXT2SPEECH_SAVE_DIR);
        if(!file.exists()){
            file.mkdirs();
        }
//        intent.setDataAndType(FileUtils.getFileUri(this, file), "*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setData(FileUtils.getFileUri(this, file));
        try{
            startActivityForResult(intent, requestCode);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "未安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPLOAD_FILE && data != null){
            //上传
            Uri uri = data.getData();
            Log.v(TAG, "分享uri：" + uri.toString());
            shareFile(uri);
        }
    }

    //分享文件
    private void shareFile(Uri uri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Text2Speech.isSpeeching())
            Text2Speech.pause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Text2Speech.isSpeeching())
            Text2Speech.shutUp(this);
    }
}
