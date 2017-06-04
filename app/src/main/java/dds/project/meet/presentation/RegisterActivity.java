package dds.project.meet.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dds.project.meet.R;
import dds.project.meet.persistence.Persistence;

public class RegisterActivity extends BaseActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email_register);
        mPasswordView = (EditText) findViewById(R.id.password_register);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm_register);
        Button mEmailSignInButton = (Button) findViewById(R.id.register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!password.equals(confirmPassword)) {
            mPasswordConfirmView.setError("Passwords are not equal");
            cancel = true;
            focusView = mPasswordConfirmView;
        }

        // Check for a valid password, if the user entered one.
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

        if (cancel) {
            focusView.requestFocus();
        } else {
            doRegister(email, password);
        }
    }

    private void doRegister(final String email, final String password) {
        showProgressDialog();
        Persistence.getAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginOK(email, password);
                } else {
                    String text =
                            isThePhoneConnected()
                                    ? "Email already registered"
                                    : "You are not connected";

                    hideProgressDialog();
                    Toast.makeText(RegisterActivity.this, text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
}

