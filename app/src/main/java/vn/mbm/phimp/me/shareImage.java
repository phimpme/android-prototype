package vn.mbm.phimp.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class shareImage extends AppCompatActivity {

    ImageView mImageView;
    public String filePath;
    public String saveFilePath;
    ImageView mshareButton;
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String SAVE_FILE_PATH = "save_file_path";
    public static final String FILE_PATH = "file_path";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);

        try {
            initView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        buttonClick();
    }


    private void initView() throws FileNotFoundException {

        mImageView = (ImageView) findViewById(R.id.imageView);
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        Bitmap myBitmap = BitmapFactory.decodeFile(saveFilePath);
        mImageView.setImageBitmap(myBitmap);
    }

    private void buttonClick() {

        mshareButton = (ImageView) findViewById(R.id.shareButton);
        mshareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton();
            }
        });

    }

    private void shareButton(){

        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_image)));
    }
}
