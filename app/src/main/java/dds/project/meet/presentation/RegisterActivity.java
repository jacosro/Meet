package dds.project.meet.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import dds.project.meet.R;
import dds.project.meet.logic.entities.User;
import dds.project.meet.persistence.util.QueryCallback;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivityLog";
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private EditText mUsernameView;
    private EditText mPhoneNumberView;
    private Button register;

    // All usernames list
    private Set<String> allUsernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the activity_login form.
        mEmailView = (EditText) findViewById(R.id.email_register);
        mPasswordView = (EditText) findViewById(R.id.password_register);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm_register);
        mUsernameView = (EditText) findViewById(R.id.username_register);
        mPhoneNumberView = (EditText) findViewById(R.id.telephone_register);
        register = (Button) findViewById(R.id.register_button);

        setListeners();

        allUsernames = new TreeSet<>();

    }

    private void setListeners() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the activity_login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual activity_login attempt is made.
     */
    private void attemptLogin() {
        showProgressDialog();

        // Get now all usernames
        mPersistence.userDAO.getAllUsernames(new QueryCallback<Collection<String>>() {
            @Override
            public void result(Collection<String> data) {
                allUsernames.addAll(data);

                mEmailView.setError(null);
                mPasswordView.setError(null);
                mPasswordConfirmView.setError(null);
                mPhoneNumberView.setError(null);
                mUsernameView.setError(null);

                // Store values at the time of the activity_login attempt.
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                String confirmPassword = mPasswordConfirmView.getText().toString();
                String phoneNumber = mPhoneNumberView.getText().toString();
                String username = mUsernameView.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (!password.equals(confirmPassword)) {
                    mPasswordConfirmView.setError("Passwords are not equal");
                    cancel = true;
                    focusView = mPasswordConfirmView;
                }

                // Check for a valid password, if the default_sidebar_user_icon entered one.
                if (!isPasswordOK(password)) {
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                    focusView = mPasswordView;
                    cancel = true;
                }

                // Check for a valid email address.
                if (!isEmailOK(email)) {
                    mEmailView.setError("This email is not valid");
                    focusView = mEmailView;
                    cancel = true;
                }

                if (!isPhoneNumberOK(phoneNumber)) {
                    mPhoneNumberView.setError("Please provide a valid phone number (Spain)");
                    focusView = mPhoneNumberView;
                    cancel = true;
                }

                if (!isUsernameOK(username)) {
                    mUsernameView.setError("Username already registered");
                    focusView = mUsernameView;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                    hideProgressDialog();
                } else {
                    doRegister(email, password, username, phoneNumber);
                }
            }
        });

    }

    private void doRegister(final String email, final String password, String userName, String phoneNumber) {
        User user = new User("Me", userName, phoneNumber, email);
        mPersistence.userDAO.createNewUser(user, password, new QueryCallback<Boolean>() {
            @Override
            public void result(Boolean success) {
                if (success) {
                    loginOK(email, password);
                } else {
                    String text =
                            isThePhoneConnected()
                                    ? "Unknown error :$"
                                    : "Connection error";

                    Toast.makeText(RegisterActivity.this, text, Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    //Auxiliar Methods
    private void loginOK(String email, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);

        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean isEmailOK(String email) {
        return email.matches("[^\\s]+@[^\\s]+\\.\\w+");
    }

    private boolean isPasswordOK(String password) {
        return password.length() > 4;
    }

    private boolean isUsernameOK(String username) {
        return !username.matches(".*\\s.*")
                && !allUsernames.contains(username);
    }

    private boolean isPhoneNumberOK(String phoneNumber) {
        return phoneNumber.length() == 9 && phoneNumber.matches("(6|7)[0-9]+");
    }
}

