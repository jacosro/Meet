package dds.project.meet.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dds.project.meet.R;
import dds.project.meet.persistence.Persistence;


/**
 * Created by RaulCoroban on 24/04/2017.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.toString();
    private static final int REQUEST_CODE = 0;

    public static final boolean AUTOMATIC_LOGIN = false; // If you don't want to login
    public static final String DEFAULT_EMAIL = "dds@project.com";
    public static final String DEFAULT_PASSWORD = "ddsproject";

    public FirebaseAuth mFirebaseAuth;
    public EditText mEmailEditText;
    public EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = Persistence.getAuth();

        if (mFirebaseAuth.getCurrentUser() != null) {
            Log.d(TAG, "User is logged in! Launching MainActivity");
            loginCompleted(true);
        }
        Log.d(TAG, "User is not logged in");

        setContentView(R.layout.login);
        mEmailEditText = (EditText) findViewById(R.id.usernameEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        final Button login = (Button) findViewById(R.id.loginButton);
        Button register = (Button) findViewById(R.id.createButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AUTOMATIC_LOGIN) {
                    mEmailEditText.getText().clear();
                    mPasswordEditText.getText().clear();
                    mEmailEditText.getText().append(DEFAULT_EMAIL);
                    mPasswordEditText.getText().append(DEFAULT_PASSWORD);
                }
                checkLogin();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    private void checkLogin() {
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);

        boolean cancel = false;
        View failed = null;

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (!isPasswordOK(password)) {
            cancel = true;
            mPasswordEditText.setError("Password must be longer");
            failed = mPasswordEditText;
        }

        if (!isEmailOK(email)) {
            cancel = true;
            mEmailEditText.setError("Email is not valid");
            failed = mEmailEditText;
        }

        if (cancel) {
            hideProgressDialog();
            failed.requestFocus();
        } else {
            doSignIn(email, password);
        }
    }

    private void doSignIn(String email, String password) {
        showProgressDialog();
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loginCompleted(true);
                } else {
                    String text =
                            isThePhoneConnected()
                                    ? "Incorrect email or password"
                                    : "You are not connected";

                    Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
                    mEmailEditText.getText().clear();
                    mPasswordEditText.getText().clear();
                    hideProgressDialog();
                }

            }
        });
    }

    private boolean isEmailOK(String email) {
        return email.matches("[^\\s]+@[^\\s]+\\.\\w+");
    }

    private boolean isPasswordOK(String password) {
        return password.length() > 4;
    }

    public void loginCompleted(boolean finish) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (finish) {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (intent != null && intent.getExtras() != null) {
                String email = intent.getExtras().getString("email");
                String password = intent.getExtras().getString("password");

                doSignIn(email, password);
            }
        }
    }
}
