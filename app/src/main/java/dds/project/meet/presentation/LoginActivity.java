package dds.project.meet.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dds.project.meet.R;
import dds.project.meet.logic.entities.User;
import dds.project.meet.persistence.util.QueryCallback;


/**
 * Created by RaulCoroban on 24/04/2017.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.toString();
    private static final int REQUEST_CODE = 0;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button login;
    private Button register;

    private boolean setName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPersistence.userDAO.getCurrentFirebaseUser() != null) {
            userIsLoggedIn();
        } else {
            Log.d(TAG, "User is not logged in");

            setContentView(R.layout.activity_login);
            mEmailEditText = (EditText) findViewById(R.id.usernameEditText);
            mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
            login = (Button) findViewById(R.id.loginButton);
            register = (Button) findViewById(R.id.createButton);

            setListeners();
        }
    }

    private void userIsLoggedIn() {
        Log.d(TAG, "User is logged in");
        mPersistence.userDAO.setCurrentUser(new QueryCallback<User>() {
            @Override
            public void result(User data) {
                if (data.getName() == null) {
                    setName = true;
                }
                Log.d(TAG, "setName = " + setName);
                Log.d(TAG, data.toString());
                loginCompleted();
            }
        });
    }

    private void setListeners() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
           doSignIn(email, password);
        }
    }

    private void doSignIn(String email, String password) {
        showProgressDialog();

        mPersistence.userDAO.doLogin(email, password, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean success) {
                if (success) {
                    userIsLoggedIn();
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

    //Auxiliar Methods
    private boolean isEmailOK(String email) {
        return email.matches("[^\\s]+@[^\\s]+\\.\\w+");
    }

    private boolean isPasswordOK(String password) {
        return password.length() > 5;
    }

    public void loginCompleted() {
        if (setName) {
            Intent intent = new Intent(this, SetNameActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //Waiting for result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (intent != null && intent.getExtras() != null) {
                setName = true;
                String email = intent.getExtras().getString("email");
                String password = intent.getExtras().getString("password");

                doSignIn(email, password);
            }
        }
    }
}
