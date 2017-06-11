package dds.project.meet.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import dds.project.meet.R;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.QueryCallback;


/**
 * Created by RaulCoroban on 24/04/2017.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.toString();
    private static final int REQUEST_CODE = 0;

    public static final boolean AUTOMATIC_LOGIN = false; // If you don't want to login
    public static final String DEFAULT_EMAIL = "dds@project.com";
    public static final String DEFAULT_PASSWORD = "ddsproject";

    public EditText mEmailEditText;
    public EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPersistence.userDAO.getCurrentUser() != null) {
            Log.d(TAG, "User is logged in! Launching MainActivity");
            loginCompleted();
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
        final String password = mPasswordEditText.getText().toString();

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
            mPersistence.userDAO.findUserByEmail(email, new QueryCallback<User>() {
                @Override
                public void result(User data) {
                    doSignIn(data, password);
                }
            });
        }
    }

    private void doSignIn(User user, String password) {
        showProgressDialog();

        mPersistence.userDAO.doLogin(user, password, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean success) {
                if (success) {
                    loginCompleted();
                } else {
                    String text =
                            isThePhoneConnected()
                                    ? "Incorrect email or password"
                                    : "You are not connected";

                    Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
                    mEmailEditText.getText().clear();
                    mPasswordEditText.getText().clear();
                }
                hideProgressDialog();
            }
        });
    }

    private boolean isEmailOK(String email) {
        return email.matches("[^\\s]+@[^\\s]+\\.\\w+");
    }

    private boolean isPasswordOK(String password) {
        return password.length() > 4;
    }

    public void loginCompleted() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (intent != null && intent.getExtras() != null) {
                String name = intent.getExtras().getString("name");
                String email = intent.getExtras().getString("email");
                String username = intent.getExtras().getString("username");
                String password = intent.getExtras().getString("password");
                String phone = intent.getExtras().getString("phone");

                User user = new User(name, username, phone, email);

                doSignIn(user, password);
            }
        }
    }
}
