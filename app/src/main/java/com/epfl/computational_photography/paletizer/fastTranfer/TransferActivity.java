package com.epfl.computational_photography.paletizer.fastTranfer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.epfl.computational_photography.paletizer.FullScreenActivity;
import com.epfl.computational_photography.paletizer.PaletizerApplication;
import com.epfl.computational_photography.paletizer.PhotoManager;
import com.epfl.computational_photography.paletizer.R;
import com.epfl.computational_photography.paletizer.SlideMenu.SlideMenuActivity;

import java.io.ByteArrayOutputStream;

public class TransferActivity extends SlideMenuActivity {

    Bitmap bitSource;
    Bitmap bitTarget;
    private Bitmap bitmapResult;

    private ImageView focusImage;
    boolean isSource;
    static {
        System.loadLibrary("opencv_java3"); //load opencv_java lib
    }

    private SurfaceView surfaceView;
    private ImageView imRes;
    private LinearLayout llRight;
    private ImageView arrowImg;
    private Button computeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfert);
        focusImage = (ImageView) findViewById(R.id.transfertTargetImage);
        bitTarget = ((BitmapDrawable)focusImage.getDrawable()).getBitmap();
        focusImage = (ImageView) findViewById(R.id.transfertSourceImage);
        bitSource = ((BitmapDrawable)focusImage.getDrawable()).getBitmap();
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        imRes = (ImageView) (findViewById(R.id.transfertImageResult));
        llRight = (LinearLayout) findViewById(R.id.ll_container_img_right);
        arrowImg = (ImageView) findViewById(R.id.arrow_tranfert);
        computeButton = (Button) findViewById(R.id.computeTransfertFast);

        if(getIntent().getExtras() == null){
            statVisible();
        }else{
            boolean stat = getIntent().getExtras().getBoolean("static");
            if(stat){
                statVisible();

            }else{
                liveVisible();
            }
        }
    }

    private void liveVisible() {
        imRes.setVisibility(View.GONE);
        llRight.setVisibility(View.GONE);
        arrowImg.setVisibility(View.GONE);
        computeButton.setVisibility(View.INVISIBLE);
        focusImage.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
    }

    private void statVisible() {
        focusImage.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.GONE);
        imRes.setVisibility(View.VISIBLE);
        llRight.setVisibility(View.VISIBLE);
        arrowImg.setVisibility(View.VISIBLE);
        computeButton.setVisibility(View.VISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked from Library
            if (requestCode == PaletizerApplication.RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                Bitmap libraryBitmap = PhotoManager.getBitmapFromLibrary(this, requestCode, resultCode, data);
                if (libraryBitmap != null) {
                    // Set the Image in ImageView after decoding the String
                    focusImage.setImageBitmap(libraryBitmap);
                    if(isSource){
                        bitSource = libraryBitmap;
                    }
                    else{
                        bitTarget = libraryBitmap;
                    }
                }
            }

            // When an Image is taken by Camera
            if(requestCode == PaletizerApplication.TAKE_PHOTO_CODE && resultCode == RESULT_OK) {

                Bitmap cameraBitmap = PhotoManager.getBitmapFromCamera(this, requestCode, resultCode);
                if (cameraBitmap != null) {
                    focusImage.setImageBitmap(cameraBitmap);
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void computeTransfert(View view) {
        if(bitSource == null || bitTarget == null){
            Toast.makeText(this, "Chose 2 photos before", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        else{
            statVisible();
            FastTransfer ft = new FastTransfer();
            bitmapResult = ft.computeTransfert(bitSource,bitTarget);
            imRes.setImageBitmap(bitmapResult);
        }

    }




    public void goFullScreen(View view) {
        Intent newActivity = new Intent(this, FullScreenActivity.class);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmapResult.compress(Bitmap.CompressFormat.PNG, 100, bs);
        newActivity.putExtra("byteArray", bs.toByteArray());
        startActivity(newActivity);
    }

    public void addPhotoByCamLeftFt(View view) {
        focusImage = (ImageView) findViewById(R.id.transfertSourceImage);
        isSource = true;
        PhotoManager.takePhoto(this);


    }

    public void addPhotoByLibRightFt(View view) {
        focusImage = (ImageView) findViewById(R.id.transfertTargetImage);
        isSource = false;
        PhotoManager.choseFromLibrary(this);

    }

    public void addPhotoByCamRightFt(View view) {
        focusImage = (ImageView) findViewById(R.id.transfertTargetImage);
        isSource = false;
        PhotoManager.takePhoto(this);

    }

    public void addPhotoByLibLeftFt(View view) {
        focusImage = (ImageView) findViewById(R.id.transfertSourceImage);
        isSource = true;
        PhotoManager.choseFromLibrary(this);


    }
}
