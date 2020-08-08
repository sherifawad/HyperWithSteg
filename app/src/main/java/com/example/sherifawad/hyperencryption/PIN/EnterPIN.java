package com.example.sherifawad.hyperencryption.PIN;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sherifawad.hyperencryption.CrySteg.CryStegActivity;
import com.example.sherifawad.hyperencryption.Password.PasswordActivity;
import com.example.sherifawad.hyperencryption.R;

import static com.example.sherifawad.hyperencryption.MainActivity.getPreference;


public class EnterPIN extends DialogFragment  {

    public static String TAG = "EnterPIN";

    private String PIN = "";

    private TextView textView ;
    private EditText editText;
    private Button okButton;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.enter_pin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.ERROR_Text_View);
        editText = view.findViewById(R.id.PIN_Text);

        // Listen to editText change
        editText.addTextChangedListener(TextWatcher);

        okButton = view.findViewById(R.id.okButton);
        DialogFragmentButtonListener(okButton);

    }

    private final TextWatcher TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            PIN = editText.getText().toString().trim();

            textView.setText("");
            okButton.setEnabled(!PIN.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onResume() {

        // Store access variables for window and blank point
        Window window = getDialog().getWindow();

        // Set dialogFragment width and height
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.PINDialogStyle);

    }

    /**
     * Buttons click listener
     * @param button
     */
    public void DialogFragmentButtonListener(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                String pinCode = preferences.getString("PIN", "");
                if (!preferences.getString("PIN", "").equals(PIN)){
                    textView.setText(" Wrong PIN");
                    return;
                }
                        // Create a new intent to pass data
                        // From current activity getActivity().getIntent()
                        // To the desired class CLassName.class
                if (getPreference("fullPrivateKey") != null) {
                    Intent intentCoreActivity = new Intent(getActivity(), CryStegActivity.class);
                    startActivity(intentCoreActivity);
                } else {
                    Intent intentCoreActivity = new Intent(getActivity(), PasswordActivity.class);
                    startActivity(intentCoreActivity);
                }
                getDialog().dismiss();
            }
        });
    }

}

