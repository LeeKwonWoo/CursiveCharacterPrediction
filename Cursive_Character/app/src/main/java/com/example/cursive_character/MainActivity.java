package com.example.cursive_character;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cursive_character.Name;
import com.example.cursive_character.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.CircleIndicator3;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //    TextView responseText;
    Button connect_btn;
    Button select_btn;
    Button select2_btn;


    ImageView upload_image;
    ImageView result_image;
    TextView result_text;
    ImageView result_image2;
    TextView result_text2;
    ImageView result_image3;
    TextView result_text3;
    ImageView result_image4;
    TextView result_text4;
    ImageView result_image5;
    TextView result_text5;

    Name name = Name.getInstance();

    String[] postResult;
    String[] names;
    String[] accuracy;
    int result_cnt;
    TextView responseText;

    private static final int FROM_ALBUM = 2;    // onActivityResult 식별자
    private static final int FROM_CAMERA = 2;   // 카메라는 사용 안함

    //
    final String TAG = getClass().getSimpleName();
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");
    final int SELECT_MULTIPLE_IMAGES = 1;
    ArrayList<String> selectedImagesPaths; // Paths of the image(s) selected by the user.
    boolean imagesSelected = false; // Whether the user selected at least an image or not.

    boolean isSecond = false;

    static final int PERMISSIONS_REQUEST = 0x0000001;

    // Server
    public void connectServer(View v) throws InterruptedException, IOException {

//        TextView responseText = findViewById(R.id.responseText);
        if (imagesSelected == false) { // This means no image is selected and thus nothing to upload.
            responseText.setText("No Image Selected to Upload. Select Image(s) and Try Again.");
            return;
        }
//        responseText.setText("Sending the Files. Please Wait ...");

//        String ipv4Address = "192.168.0.136"; // server
        String ipv4Address = "192.168.0.197"; // my
//        String ipv4Address = "192.168.0.128"; // prince
        String portNumber = "5000";

        Matcher matcher = IP_ADDRESS.matcher(ipv4Address);
        if (!matcher.matches()) {
            responseText.setText("Invalid IPv4 Address. Please Check Your Inputs.");
            return;
        }

        String postUrl = "http://" + ipv4Address + ":" + portNumber + "/";


        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        MultipartBody.Builder multipartBodyBuilder2 = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < selectedImagesPaths.size(); i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                // Read BitMap by file path.
                System.out.println(stream+"))"+selectedImagesPaths.get(i));
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagesPaths.get(i), options);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, stream);

            }catch(Exception e){
                responseText.setText("Please Make Sure the Selected File is an Image.");
                e.printStackTrace();
                return;
            }
            byte[] byteArray = stream.toByteArray();

            multipartBodyBuilder.addFormDataPart("image" + i, "Android_Flask_" + i + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
//            multipartBodyBuilder2.addFormDataPart("image" + i, "text_Android_Flask_" + i + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));

        }

        RequestBody postBodyImage = multipartBodyBuilder.build();
//        RequestBody postBody = multipartBodyBuilder2.build();

        imagePostRequest(postUrl, postBodyImage);
        Thread.sleep(5000);
        String postUrl2 = "http://" + ipv4Address + ":" + portNumber + "/img/";
//        MediaType mediaType = MediaType.parse("text/plain");

//        String[] c = name.getCcName().split("@");

        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i]+"!**!");
//            RequestBody postBody = RequestBody.create(mediaType, c[i]);

            get(postUrl2 + names[i]);
            Thread.sleep(5000);
        }

    }



    void imagePostRequest(String postUrl, RequestBody postBody) throws IOException {

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result_p = response.body().string();
            String[] rps = result_p.split("%");
            names = new String[rps.length];
            accuracy = new String[rps.length];
            postResult = new String[rps.length];
            result_cnt = rps.length;
            for(int i = 0; i < rps.length; i++) {
                postResult[i] = rps[i];
                String[] rps2 = rps[i].split(":");
                names[i] = rps2[0];
                accuracy[i] = rps2[1];
            }
            String n = "";
            for (int i = 0; i < names.length; i++) {
                n += names[i] + "@";
            }

            name.setCcName(n);
            System.out.println(name.getCcName()+"===");
            if (rps.length == 1) {
                result_text.setVisibility(View.VISIBLE);
                result_text.setText(names[0] + " : " + accuracy[0] + "%");
            } else if(rps.length == 2) {
                result_text.setVisibility(View.VISIBLE);
                result_text2.setVisibility(View.VISIBLE);
                result_text.setText(names[0] + " : " + accuracy[0] + "%");
                result_text2.setText(names[1] + " : " + accuracy[1] + "%");
            } else if(rps.length == 3) {
                result_text.setVisibility(View.VISIBLE);
                result_text2.setVisibility(View.VISIBLE);
                result_text3.setVisibility(View.VISIBLE);

                result_text.setText(names[0] + " : " + accuracy[0] + "%");
                result_text2.setText(names[1] + " : " + accuracy[1] + "%");
                result_text3.setText(names[2] + " : " + accuracy[2] + "%");
            } else if(rps.length == 4) {
                result_text.setVisibility(View.VISIBLE);
                result_text2.setVisibility(View.VISIBLE);
                result_text3.setVisibility(View.VISIBLE);
                result_text4.setVisibility(View.VISIBLE);

                result_text.setText(names[0] + " : " + accuracy[0] + "%");
                result_text2.setText(names[1] + " : " + accuracy[1] + "%");
                result_text3.setText(names[2] + " : " + accuracy[2] + "%");
                result_text4.setText(names[3] + " : " + accuracy[3] + "%");
            } else if(rps.length == 5) {
                result_text.setVisibility(View.VISIBLE);
                result_text2.setVisibility(View.VISIBLE);
                result_text3.setVisibility(View.VISIBLE);
                result_text4.setVisibility(View.VISIBLE);
                result_text5.setVisibility(View.VISIBLE);

                result_text.setText(names[0] + " : " + accuracy[0] + "%");
                result_text2.setText(names[1] + " : " + accuracy[1] + "%");
                result_text3.setText(names[2] + " : " + accuracy[2] + "%");
                result_text4.setText(names[3] + " : " + accuracy[3] + "%");
                result_text5.setText(names[4] + " : " + accuracy[4] + "%");
            }
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }

        /*
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());
                e.printStackTrace();
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()

                try {
                    String result_p = response.body().string();
                    String[] rps = result_p.split("%");
                    names = new String[rps.length];
                    accuracy = new String[rps.length];
                    postResult = new String[rps.length];
                    result_cnt = rps.length;
                    for(int i = 0; i < rps.length; i++) {
                        postResult[i] = rps[i];
                        String[] rps2 = rps[i].split(":");
                        names[i] = rps2[0];
                        accuracy[i] = rps2[1];
                    }
                    String n = "";
                    for (int i = 0; i < names.length; i++) {
                        n += names[i] + "@";
                    }


                    name.setCcName(n);

                    if (rps.length == 1) {
                        result_text.setText(names[0] + " : " + accuracy[0] + "%");
                    } else if(rps.length == 2) {
                        result_text.setText(names[0] + " : " + accuracy[0] + "%");
                        result_text2.setText(names[1] + " : " + accuracy[1] + "%");
                    } else if(rps.length == 3) {
                        result_text.setText(names[0] + " : " + accuracy[0] + "%");
                        result_text2.setText(names[1] + " : " + accuracy[1] + "%");
                        result_text3.setText(names[2] + " : " + accuracy[2] + "%");
                    } else if(rps.length == 4) {
                        result_text.setText(names[0] + " : " + accuracy[0] + "%");
                        result_text2.setText(names[1] + " : " + accuracy[1] + "%");
                        result_text3.setText(names[2] + " : " + accuracy[2] + "%");
                        result_text4.setText(names[3] + " : " + accuracy[3] + "%");
                    } else if(rps.length == 5) {
                        result_text.setText(names[0] + " : " + accuracy[0] + "%");
                        result_text2.setText(names[1] + " : " + accuracy[1] + "%");
                        result_text3.setText(names[2] + " : " + accuracy[2] + "%");
                        result_text4.setText(names[3] + " : " + accuracy[3] + "%");
                        result_text5.setText(names[4] + " : " + accuracy[4] + "%");
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
////                        TextView responseText = findViewById(R.id.responseText);
////                        InputStream reFile = response.body().byteStream();
////                        Bitmap bitmap = BitmapFactory.decodeStream(reFile);
//
////                        result_text.setText(reFile.toString());
////                        result_image.setScaleType(ImageView.ScaleType.FIT_XY);
////                        result_image.setImageBitmap(bitmap);
////                        responseText.setText("Predict Image!");
//
//
//                    }

//                });
            }


        });*/
    }

    public int n = 0;
    public int z = 1;

    public void get(String requestURL) {
        try {
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(requestURL)
                    .build();
            Response response = client.newCall(request).execute();
            InputStream reFile = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(reFile);
            String[] strs = name.getCcName().split("@");
            n = strs.length;

            if (z==1 && n >= 1) {
                result_image.setVisibility(View.VISIBLE);
                result_image.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image.setImageBitmap(bitmap);
                System.out.println("First!!");
            } else if (z == 2 && n >= 2) {
                result_image2.setVisibility(View.VISIBLE);
                result_image2.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image2.setImageBitmap(bitmap);
                System.out.println("Second!!");
            } else if (z==3 && n >= 3) {
                result_image3.setVisibility(View.VISIBLE);
                result_image3.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image3.setImageBitmap(bitmap);
                System.out.println("third!!");
            } else if (z==4 && n >= 4) {
                result_image4.setVisibility(View.VISIBLE);
                result_image4.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image4.setImageBitmap(bitmap);
                System.out.println("fourth!!");
            } else if (z==5 && n >= 5) {
                result_image5.setVisibility(View.VISIBLE);
                result_image5.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image5.setImageBitmap(bitmap);
                System.out.println("fifth!!");
            }

            if(n == z) {
                responseText.setText("Predicted a cursive character!");
//                if(n == 1) {
//                    result_image2.setVisibility(View.GONE);
//                    result_image3.setVisibility(View.GONE);
//                    result_image4.setVisibility(View.GONE);
//                    result_image5.setVisibility(View.GONE);
//                } else if (n == 2) {
//                    result_image3.setVisibility(View.GONE);
//                    result_image4.setVisibility(View.GONE);
//                    result_image5.setVisibility(View.GONE);
//                } else if (n == 3) {
//                    result_image4.setVisibility(View.GONE);
//                    result_image5.setVisibility(View.GONE);
//                } else if (n == 4) {
//                    result_image5.setVisibility(View.GONE);
//                }

                connect_btn.setVisibility(View.GONE);
            } else {
                z++;
            }

        } catch (Exception eo) {

        }

    }

    void postRequest(String postUrl, RequestBody postBody) throws InterruptedException {
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            InputStream reFile = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(reFile);
            String[] strs = name.getCcName().split("@");
            n = strs.length;

//                        result_text.setText(reFile.toString());
//                        result_image.setScaleType(ImageView.ScaleType.FIT_XY);
//                        result_image.setImageBitmap(bitmap);
            if (z==1 && n >= 1) {
                result_image.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image.setImageBitmap(bitmap);
            } else if (z == 2 && n >= 2) {
                result_image2.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image2.setImageBitmap(bitmap);
            } else if (z==3 && n >= 3) {
                result_image3.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image3.setImageBitmap(bitmap);
            } else if (z==4 && n >= 4) {
                result_image4.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image4.setImageBitmap(bitmap);
            } else if (z==5 && n >= 5) {
                result_image5.setScaleType(ImageView.ScaleType.FIT_XY);
                result_image5.setImageBitmap(bitmap);
            }
        } catch (Exception eo) {

        }


//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                call.cancel();
//                Log.d("FAIL", e.getMessage());
//                e.printStackTrace();
//                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        TextView responseText = findViewById(R.id.responseText);
//
//                        responseText.setText("Failed to Connect to Server. Please Try Again.");
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, Response response) {
//                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        InputStream reFile = response.body().byteStream();
//                        Bitmap bitmap = BitmapFactory.decodeStream(reFile);
//                        String[] strs = name.getCcName().split("@");
//                        n = strs.length;
//
////                        result_text.setText(reFile.toString());
////                        result_image.setScaleType(ImageView.ScaleType.FIT_XY);
////                        result_image.setImageBitmap(bitmap);
//                        if (z==1 && n >= 1) {
//                            result_image.setScaleType(ImageView.ScaleType.FIT_XY);
//                            result_image.setImageBitmap(bitmap);
//                        } else if (z == 2 && n >= 2) {
//                            result_image2.setScaleType(ImageView.ScaleType.FIT_XY);
//                            result_image2.setImageBitmap(bitmap);
//                        } else if (z==3 && n >= 3) {
//                            result_image3.setScaleType(ImageView.ScaleType.FIT_XY);
//                            result_image3.setImageBitmap(bitmap);
//                        } else if (z==4 && n >= 4) {
//                            result_image4.setScaleType(ImageView.ScaleType.FIT_XY);
//                            result_image4.setImageBitmap(bitmap);
//                        } else if (z==5 && n >= 5) {
//                            result_image5.setScaleType(ImageView.ScaleType.FIT_XY);
//                            result_image5.setImageBitmap(bitmap);
//                        }
//
//
//
//
//
//                    }
//                });
////                TextView responseText = findViewById(R.id.responseText);
//
//
//            }
//
//        });
    }

    // 촬영한 사진을 이미지 파일로 저장하는 함수
    private File createImageFile() throws IOException {
        //Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd__HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        //Save a file : path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        System.out.println(mCurrentPhotoPath+"[createImageFile]");
        return image;
    }

    public void selectAlbumIntent(View v) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");                      // 이미지만
        startActivityForResult(intent, FROM_ALBUM);

        responseText.setText("Click the prediction button.");
    }

    //카메라 인텐트 실헹
    public void dispatchTakePictureIntent(View v) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println(takePictureIntent+"##takePictureIntent");
        System.out.println(takePictureIntent.resolveActivity(getPackageManager())+"##resolveActivity");
        // Ensure that there's a camera activity to handle the intent
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            System.out.println("!!");
            //create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch(IOException e) {

            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.cursivecharacter.fileprovider", photoFile);
                System.out.println(photoURI+"++photoURI");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent.createChooser(takePictureIntent,"Take Picture"), REQUEST_TAKE_PHOTO);
                System.out.println(photoURI+"@@photoURI");

                responseText.setText("Click the prediction button.");
            }

        }
    }

    //갤러리에 사진 추가
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        System.out.println(mCurrentPhotoPath+"[galleryAddPic]");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // 크기가 조정된 이미지 디코딩
//    private void setPic() {
//        // Get the dimensions of the View
//        int targetW = upload_image.getWidth();
//        int targetH = upload_image.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        upload_image.setImageBitmap(bitmap);
//    }

//    public void OnCheckPermission() {
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(this, "Please check allow",Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
//            }
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                Toast.makeText(this, "Please check allow",Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
//            }
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                Toast.makeText(this, "Please check allow",Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
//            }
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
//                Toast.makeText(this, "Please check allow",Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSIONS_REQUEST);
//            }
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy); }

        responseText = findViewById(R.id.responseText);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        select_btn = findViewById(R.id.select_btn);
        select2_btn = findViewById(R.id.select2_btn);
        upload_image = findViewById(R.id.upload_image);

        result_image = findViewById(R.id.result_image1);
        result_text = findViewById(R.id.result_text1);
        result_image2 = findViewById(R.id.result_image2);
        result_text2 = findViewById(R.id.result_text2);
        result_image3 = findViewById(R.id.result_image3);
        result_text3 = findViewById(R.id.result_text3);
        result_image4 = findViewById(R.id.result_image4);
        result_text4 = findViewById(R.id.result_text4);
        result_image5 = findViewById(R.id.result_image5);
        result_text5 = findViewById(R.id.result_text5);

        result_image.setVisibility(View.GONE);
        result_image2.setVisibility(View.GONE);
        result_image3.setVisibility(View.GONE);
        result_image4.setVisibility(View.GONE);
        result_image5.setVisibility(View.GONE);

        result_text.setVisibility(View.GONE);
        result_text2.setVisibility(View.GONE);
        result_text3.setVisibility(View.GONE);
        result_text4.setVisibility(View.GONE);
        result_text5.setVisibility(View.GONE);



        connect_btn = findViewById(R.id.connect_btn);
        connect_btn.setVisibility(View.GONE);
//        OnCheckPermission();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "권한 설정 완료");
//            } else {
//                Log.d(TAG, "권한 설정 요청");
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }

//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},1);
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 3);
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);


//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPer();
            }
        }, 4000);

    }

    private void checkPer() {
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.isAnyPermissionPermanentlyDenied()){
                    checkPer();
                } else if (report.areAllPermissionsGranted()){
                    // copy some things
                } else {
                    checkPer();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionResult");

        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "thank you",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Cancel the permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private String getRealPathFromUri(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        System.out.println(isKitKat+"//isKitKat");
        System.out.println(DocumentsContract.isDocumentUri(context, uri)+"//iskitkat2");
        System.out.println("Content:"+"content".equalsIgnoreCase(uri.getScheme()));
        System.out.println(uri.getAuthority()+"~Authority");
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            System.out.println(isExternalStorageDocument(uri)+"isUri!");
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                System.out.println("getPath1!");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    System.out.println("**!!getPath1-1!");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                System.out.println("getPath2!");
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                System.out.println("getPath3!");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                System.out.println(docId+"%%docId");
                Uri contentUri = null;
                if ("image".equals(type)) {
                    System.out.println("getPath3-1!");
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    System.out.println(contentUri+":::contentUri");
                } else if ("video".equals(type)) {
                    System.out.println("getPath3-2!");
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    System.out.println("getPath3-3!");
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                System.out.println(context+"-context");
                System.out.println(contentUri+"-contentURI");
                System.out.println(selection+"-selection");
                System.out.println(selectionArgs[0]+"-selectionArgs");

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            System.out.println("getPath4!");
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            System.out.println("getPath5!");
            return uri.getPath();
        }

        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String selection2 = MediaStore.Images.Media.DURATION +
                " >= ?";
        final String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DURATION,
                MediaStore.Images.Media.SIZE
        };

        try {
            System.out.println("check=="+uri.getAuthority());
            System.out.println("uri=="+uri);
            System.out.println("projection=="+projection[0]);
            System.out.println("selection=="+selection);
            System.out.println("selectionArgs=="+selectionArgs[0]);

            cursor = context.getContentResolver().query(uri, projection, selection2, selectionArgs,
                    null);
            System.out.println(cursor+"--cursor");
            System.out.println(cursor.moveToFirst()+"--moveToFirst");
            System.out.println(cursor.getCount()+"++count");

            if (cursor != null && cursor.moveToFirst()) {
                System.out.println("통");
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        System.out.println("isMdedia[]");
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public Bitmap rotateImage(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0,0, src.getWidth(), src.getHeight(), matrix, true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String currentImagePath;
        selectedImagesPaths = new ArrayList<>();
//        System.out.println(data.getData()+"!!getData");

        try {
            InputStream buf = null;
            Bitmap bitmap = null;

            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {

                    // 카메라로 촬영한 이미지 가져옴
                    if (resultCode == RESULT_OK) {
                        galleryAddPic();

                        currentImagePath = mCurrentPhotoPath;
                        System.out.println(mCurrentPhotoPath+"//onActivitymCurrent");
                        selectedImagesPaths.add(currentImagePath);
                        System.out.println(currentImagePath+"!!currentPath!!");
                        imagesSelected = true;
                        System.out.println("photo!");
                        File file = new File(mCurrentPhotoPath);
                        System.out.println("file:"+file);

                        //version 29이상
                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                            try {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 8;
                                bitmap = ImageDecoder.decodeBitmap(source);
                                if (bitmap != null) {
                                    System.out.println("!!");
                                    int x = bitmap.getWidth();
                                    int y = bitmap.getHeight();
//                                    if (x < y) {
//                                        bitmap = rotateImage(bitmap, (float)90);
//                                    } else if(x > y) {
//                                        bitmap = rotateImage(bitmap, (float)-90);
//                                    }
                                    upload_image.setImageBitmap(bitmap);
                                    select_btn.setVisibility(View.GONE);
                                    select2_btn.setVisibility(View.GONE);
                                    connect_btn.setVisibility(View.VISIBLE);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("@@");
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                if (bitmap != null) {
                                    System.out.println("##");
                                    int x = bitmap.getWidth();
                                    int y = bitmap.getHeight();
//                                    if (x < y) {
//                                        bitmap = rotateImage(bitmap, (float)90);
//                                    } else if(x > y) {
//                                        bitmap = rotateImage(bitmap, (float)-90);
//                                    }
                                    upload_image.setImageBitmap(bitmap);
                                    select_btn.setVisibility(View.GONE);
                                    select2_btn.setVisibility(View.GONE);
                                    connect_btn.setVisibility(View.VISIBLE);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("$$");
                            }
                        }
                        galleryAddPic();
                    }
                    break;
                }

                case FROM_ALBUM: {
                    if (resultCode == RESULT_OK) {
                        if (data.getData() != null) {
                            Uri uri = data.getData();
                            System.out.println(uri+"**!!");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                currentImagePath = getRealPathFromUri(uri);
                                selectedImagesPaths.add(currentImagePath);
                                System.out.println(currentImagePath+"!!currentPath!!");
                                imagesSelected = true;
                                System.out.println("album!");
                                buf = getContentResolver().openInputStream(data.getData());

                                bitmap = BitmapFactory.decodeStream(buf);
                                buf.close();
                                int x = bitmap.getWidth();
                                int y = bitmap.getHeight();
                                if (x > y) {
                                    bitmap = rotateImage(bitmap, (float)90);
                                } else if(x < y) {
                                    bitmap = rotateImage(bitmap, (float)-90);
                                }
                                //이미지 뷰에 선택한 사진 띄우기
                                upload_image.setScaleType(ImageView.ScaleType.FIT_XY);

//                       bitmap = rotateImage(bitmap, (float)90);
                                select_btn.setVisibility(View.GONE);
                                select2_btn.setVisibility(View.GONE);
                                connect_btn.setVisibility(View.VISIBLE);
                                upload_image.setImageBitmap(bitmap);
                            }



                        }


                    }
                    break;
                }
            }



        } catch (IOException e) {
            e.printStackTrace();System.out.println("%%");
        }

    }
}