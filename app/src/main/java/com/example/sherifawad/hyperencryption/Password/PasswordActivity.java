package com.example.sherifawad.hyperencryption.Password;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;

import com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity;

import com.example.sherifawad.hyperencryption.R;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.IOException;

import Util.Uty;
import parameters.Client;

import static com.example.sherifawad.hyperencryption.MainActivity.setPreference;

public class PasswordActivity extends AppCompatActivity implements PasswordImageDialogFragment.Callback{

    private static final String TAG = "Main FragmentActivity";

    private FilePickerDialog dialog ;
    private EditText editTextFirst_Text;
    private EditText editTextSecond_Text;
    private EditText editTextChar_Text;
    private EditText editTextNextChar_Text;
    private EditText editTextAccount_Text;
    private EditText editTextName_Text;
    private EditText editTextPhone_Text;

    private TextView errorView;


    private Button sBrowseButton;
    private Button sSubmitButton;
    private Spinner sSpinner;

    private String image_path = "";
    private int sFirstTextValue;
    private int sSecondTextValue;
    private String sCharText;
    private int sNextCharTextValue;
    private String sOperation;

    private String sAccountText;
    private String sNameText;
    private String sPhoneText;


    public static Context sContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainActivity Context
        sContext = this;
        // Get the view from activity_password.xml
        setContentView(R.layout.activity_password);

        // Get buttons By ids
        sBrowseButton = findViewById(R.id.Browse_Button);
        sSubmitButton = findViewById(R.id.Submit_Button);

        // Listen to buttons click
        buttonListener(sBrowseButton);
        buttonListener(sSubmitButton);

        // Get editTexts by ids
        editTextFirst_Text = (findViewById(R.id.First_Text));
        editTextSecond_Text= findViewById(R.id.Second_Text);
        editTextChar_Text = findViewById(R.id.Char_Text);
        editTextNextChar_Text = findViewById(R.id.NextChar_Text);

        editTextAccount_Text = findViewById(R.id.accountNumber);
        editTextName_Text = findViewById(R.id.userName);
        editTextPhone_Text = findViewById(R.id.phoneNumber);

        errorView = findViewById(R.id.errorText);


        // Listen to editText change
        editTextFirst_Text.addTextChangedListener(TextWatcher);
        editTextSecond_Text.addTextChangedListener(TextWatcher);
        editTextChar_Text.addTextChangedListener(TextWatcher);
        editTextNextChar_Text.addTextChangedListener(TextWatcher);

        editTextAccount_Text.addTextChangedListener(TextWatcher);
        editTextName_Text.addTextChangedListener(TextWatcher);
        editTextPhone_Text.addTextChangedListener(TextWatcher);

        // Get spinner by it's id
        sSpinner = findViewById(R.id.SIGN_spinner);
        // Listen to spinner item selection
        addListenerOnSpinnerItemSelection(sSpinner, R.array.Operations_Arrays);
    }

    /**
     * Add list to the spinner and Listen for item selection
     * @param spinner
     * @param stringArrays A string list which will be added to the spinner with specified ID
     */
    public void addListenerOnSpinnerItemSelection(Spinner spinner,  int stringArrays) {

        // Create adapter with list items
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(stringArrays));
        // Set dropdown style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Assign the adaptor to the spinner
        spinner.setAdapter(dataAdapter);
        // Listen to Item selection and do something
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Get the selection
                sOperation = parent.getItemAtPosition(pos).toString();
                sSubmitButton.setEnabled(!image_path.isEmpty() && !sOperation.isEmpty());

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Text change listener method
     */
    private final TextWatcher TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Get text in the fields and trim the spaces
            String sFirstText = editTextFirst_Text.getText().toString().trim();
            String sSecondText = editTextSecond_Text.getText().toString().trim();
            sCharText = editTextChar_Text.getText().toString().trim();
            String sNextCharText = editTextNextChar_Text.getText().toString().trim();

            sAccountText = editTextAccount_Text.getText().toString().trim();
            sNameText = editTextName_Text.getText().toString().trim();
            sPhoneText = editTextPhone_Text.getText().toString().trim();
//            sNextCharText = editTextNextChar_Text.getText().toString().trim();

            if (sFirstText.equals("0") || sFirstText.equals("1") || sFirstText.equals("01")
                    || sSecondText.equals("0") || sSecondText.equals("1") || sSecondText.equals("01")
                    || sNextCharText.equals("0") || sNextCharText.equals("1") || sNextCharText.equals("01")
                    ) {
                sBrowseButton.setEnabled(!sBrowseButton.isEnabled());
                errorView.setText("Value must not be '0' or '1' or '01'");

//                Toast.makeText(sContext, "Value must not be '0' or '1' or '01'", Toast.LENGTH_SHORT).show();

                return;
            }
            if (sFirstText.equalsIgnoreCase(sSecondText) || sFirstText.equalsIgnoreCase(sNextCharText)
                    || sSecondText.equalsIgnoreCase(sNextCharText)
            ) {
                sBrowseButton.setEnabled(!sBrowseButton.isEnabled());
                errorView.setText("Value must not be the same");

//                Toast.makeText(sContext, "Value must not be the same",
//                        Toast.LENGTH_SHORT).show();
                return;
            }

            errorView.setText("");
            try {
                if ( !sNextCharText.isEmpty() || !sNextCharText.isEmpty() || !sNextCharText.isEmpty()) {
                    sFirstTextValue = Integer.parseInt(sFirstText);
                    sSecondTextValue = Integer.parseInt(sSecondText);
                    sNextCharTextValue = Integer.parseInt(sNextCharText);
                }
            } catch (NumberFormatException e) {
//                Toast.makeText(sContext, "sNextCharText is not integer value use less numbers",
//                        Toast.LENGTH_LONG).show();
                sBrowseButton.setEnabled(!sBrowseButton.isEnabled());
            }
            // Enable browser button only if all text fields are not empty
            sBrowseButton.setEnabled(!sFirstText.isEmpty() && !sSecondText.isEmpty() && !sCharText.isEmpty() &&  !sNextCharText.isEmpty()
                    &&  !sAccountText.isEmpty() &&  !sNameText.isEmpty() &&  !sPhoneText.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

            // If the editable is charText field allow only one char and new char will replace old
            if (s == editTextChar_Text.getText()) {

                // Get the length
                int iLen = s.length();
//                if (iLen > 0 && !Character.isLetter((s.charAt(iLen - 1)))) {
                // If not empty and
                if (iLen > 0 &&
                        // Check if the entered value is digit
                        Character.isDigit((s.charAt(iLen - 1)))) {
                    // Delete the entered value
                    s.delete(iLen - 1, iLen);
                    return;
                }
                // If not empty
                if (iLen > 1) {
                    // Delete the existed
                    s.delete(0, 1);
                }

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(dialog!=null)
                    {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(sContext,"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Perform actions depend on the clicked button
     * @param button The clicked button
     */
    public void buttonListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Switch operation with cases according to the button
                switch (button.getId()){

                    // Browser button listener
                    case R.id.Browse_Button:
                        // Edit the browser dialog properties
                        DialogProperties dialogProperties = new DialogProperties();
                        // Allow only one selection
                        dialogProperties.selection_mode = DialogConfigs.SINGLE_MODE;
                        // Set the selection to files typed only
                        dialogProperties.selection_type = DialogConfigs.FILE_SELECT;
                        // Choose to starting browsing directory
                        dialogProperties.root = new File(DialogConfigs.DEFAULT_DIR);
                        // The the type of files to select
                        dialogProperties.extensions = new String[]{"png", "jpg", "jpeg"};
                        // Start the browser dialog
                        dialog = new FilePickerDialog(sContext, dialogProperties);
                        // Set the title
                        dialog.setTitle("Select an Image");
                        // Show the dialog
                        dialog.show();
                        // Listen to the selection
                        dialog.setDialogSelectionListener(new DialogSelectionListener() {

                            @Override
                            public void onSelectedFilePaths(String[] files) {
                                // Ger the first path in the array
                                image_path = files[0];

                                // Enable submit button only if the image path is not empty
                                sSubmitButton.setEnabled(!image_path.isEmpty() && !sOperation.isEmpty());
                            }
                        });
                        break;

                    // Submit button listener
                    case R.id.Submit_Button:

//                        System.out.println("sFirstText "+ sFirstText);
//                        System.out.println("sSecondText "+ sSecondText);
//                        System.out.println("sCharText "+ sCharText);
//                        System.out.println("sNextCharText "+ sNextCharTextValue);
//                        System.out.println("sImagePath "+ image_path);
//                        System.out.println("sOperation" + sOperation);



                        // New custom dialog fragment
                        PasswordImageDialogFragment dialog = new PasswordImageDialogFragment();

//                        progressDialog.DismissProgressDialog()

                        // Create bundle of variables to send to children fragments
                        Bundle bundle = new Bundle();
//                        bundle.putString("sFirstText", sFirstText);
//                        bundle.putString("sSecondText", sSecondText);
                        bundle.putInt("sFirstText", sFirstTextValue);
                        bundle.putInt("sSecondText", sSecondTextValue);
                        bundle.putString("sCharText", sCharText);
                        bundle.putInt("sNextCharText", sNextCharTextValue);
                        bundle.putString("sImagePath", image_path);
                        bundle.putString("sOperation", sOperation);

                        // Add the bundle to the custom dialog fragment
                        dialog.setArguments(bundle);

                        // Get supported fragment manager then perform a transaction
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        //Show the dialog fragment
                        dialog.show(ft, PasswordImageDialogFragment.TAG);

                        // Wait two seconds before execute inner commands
                        new Handler().postDelayed(new Runnable() {
                                                public void run() {

                                                    //Inner commands
                                                    editTextFirst_Text.getText().clear();
                                                    editTextSecond_Text.getText().clear();
                                                    editTextChar_Text.getText().clear();
                                                    editTextNextChar_Text.getText().clear();
                                                    image_path = "";
//                                sSubmitButton.setEnabled(false);
                                                    // Assuming the default position is 0.
                                                    sSpinner.setSelection(0);
                                                }
                                            }, 5
                        );
                        // Stop the handler
//                    handler.removeCallbacksAndMessages(null);

                        // send values to attached fragments
//                    getIntent().putExtra("sFirstText", sFirstText);
//                    getIntent().putExtra("sSecondText", sSecondText);
//                    getIntent().putExtra("sCharText", sCharText);
//                    getIntent().putExtra("sNextCharText", sNextCharText);
//                    getIntent().putExtra("sImagePath", image_path);


                        break;
                }
            }
        });
    }

    @Override
    public void setData(String pass) {
        try {
            //Client client = new Client("1236547896541596", "Sherif", pass.toCharArray(), "+201280412208");
            setPreference("client", Uty.serialize(new Client(sAccountText, sNameText, pass.toCharArray(), sPhoneText)));
//            setPreference("client", Uty.serialize(new Client(null, null, null, null, null)));
            System.out.println("THe password of PasswordActivity is " + pass);
//            System.out.println("password " + ((Client)(Uty.deserialize(getPreference("client")))).getPassword());
//            System.out.println("private " + ((Client)(Uty.deserialize(getPreference("client")))). getPrivateKey().getYCoordinate());
            pass = "";
//            Intent cryStegActivity = new Intent(sContext, CryStegActivity.class);
            Intent cryStegActivity = new Intent(getApplicationContext(), CryStegActivity.class);
            startActivity(cryStegActivity);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

