package com.example.imagetransfer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imagetransfer.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    public String message,Ip;
    static final int REQUEST_IMAGE_CAPTURE=101;
    private ImageView mimageView;
    private VideoView videoView;
    private View View;
    public EditText inputtext;
    public TextView textView1;
    public Socket s;
    public long diff=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputtext = (EditText)findViewById(R.id.editText3);
        final Button clickable = (Button)findViewById(R.id.button3);
        final Button clickable1 = (Button)findViewById(R.id.button);
        textView1= (TextView) findViewById(R.id.text);

        //final Button clickable2 = (Button)findViewById(R.id.button4);
        mimageView=(ImageView )findViewById(R.id.imageView);

        //clickable.setOnClickListener(new View.OnClickListener(){
        //   public void onClick(View v){
        //       send sendcode = new send();
        //       message = inputtext.getText().toString();
        //      sendcode.execute();
        //   }
        //});
    }
    /*
        static final int REQUEST_VIDEO_CAPTURE = 1;

        public void takeVideo(View view) {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    */
    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }*/

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            send sendcode = new send();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] arr = baos.toByteArray();
            String result = Base64.encodeToString(arr, Base64.DEFAULT);

            message = result;
            sendcode.execute();
            mimageView.setImageBitmap(imageBitmap);
        }
    }

    public void setIP(android.view.View view) {
        Ip=inputtext.getText().toString();
        inputtext.setFocusable(false);
        Toast.makeText(getApplicationContext(),"IP is set, Ready to send picture",Toast.LENGTH_SHORT).show();
    }
    public void setText(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView1.setText("Picture Transmission and respond time is : "+ diff +" milliseconds");
            }
        });
    }

    class send extends AsyncTask<Void,Void,Void> {

        PrintWriter pw;
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void...params){
            try {
                s = new Socket(Ip,7000);
                //s.setSoTimeout(100);
                long start = System.currentTimeMillis();
                pw = new PrintWriter(s.getOutputStream());
                pw.write(message);
                pw.flush();
                pw.close();
                s = new Socket(Ip,7000);
                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                final String message1 = input.readLine();
                long finish = System.currentTimeMillis();
                diff = finish - start;
                System.out.println(diff);
                setText();
                s.close();
            } catch (UnknownHostException e) {
                System.out.println("Fail");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Fail");
                e.printStackTrace();
            }
            return null;
        }
    }
}
