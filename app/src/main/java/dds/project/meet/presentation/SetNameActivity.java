package dds.project.meet.presentation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import dds.project.meet.R;
import dds.project.meet.logic.util.ProfileImage;
import dds.project.meet.persistence.Persistence;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetNameActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView mAvatar;
    private EditText mEditText;
    private FloatingActionButton mFab;
    private boolean avatarSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        mAvatar = (CircleImageView) findViewById(R.id.avatar);
        mEditText = ((TextInputLayout) findViewById(R.id.name)).getEditText();

        mFab = (FloatingActionButton) findViewById(R.id.name_done);
        mFab.hide();

        setListeners();
    }

    private void setListeners() {
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditText.getText().length() > 5) {
                    mFab.show();
                }
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistence.getInstance().userDAO.updateName(mEditText.getText().toString());

                if (avatarSet)
                    ProfileImage.getInstance(SetNameActivity.this).upload();

                Intent intent = new Intent(SetNameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void loadAvatar(Uri uri) {
        Glide.with(this)
                .load(uri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(mAvatar);
        avatarSet = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                if (!imageUri.getLastPathSegment().endsWith(".jpg")) {
                    Toast.makeText(this, "Invalid image type! Use jpg", Toast.LENGTH_SHORT).show();
                } else {
                    ProfileImage.getInstance(this).set(imageUri, false);
                    loadAvatar(imageUri);
                }
            }
        }
    }
}
