package com.example.sherifawad.hyperencryption.PIN;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

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

import com.example.sherifawad.hyperencryption.R;

public class CreatePIN extends DialogFragment  {

    public static String TAG = "CreatePIN";

    private String newPIN = "";
    private String confirmPIN = "";

    private TextView textView ;
    private TextView matchTextView ;
    private EditText newText;
    private EditText confirmText;
    private Button okButton;

    Callback pINCallback;

    public interface Callback {
        //Pass the PIN to the Activity
        void setData(String PIN);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            pINCallback= (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
//        passCallback= (Callback) context;
    }

    @Override
    public void onDetach() {
        pINCallback = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_pin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.ERROR_Text_View);
        matchTextView = view.findViewById(R.id.ERROR_MissMatch_Text_View);
        newText = view.findViewById(R.id.New_Text);
        confirmText = view.findViewById(R.id.Confirm_Text);

        // Listen to editText change
        newText.addTextChangedListener(TextWatcher);
        confirmText.addTextChangedListener(TextWatcher);

        okButton = view.findViewById(R.id.okButton);
        DialogFragmentButtonListener(okButton);

    }

    private final android.text.TextWatcher TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            newPIN = newText.getText().toString().trim();
            confirmPIN = confirmText.getText().toString().trim();

            okButton.setEnabled(!newPIN.isEmpty() || !confirmPIN.isEmpty());
            textView.setText("");
            matchTextView.setText("");



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


                if (newText.getText().length() < 6 ){
                    textView.setText(" Enter at least 6 characters");
                    return;
                }

                if (!newPIN.equals(confirmPIN)){
                    matchTextView.setText("PIN miss match");
                    return;
                }
                pINCallback.setData(newPIN);
                newPIN = confirmPIN = "";
                getDialog().dismiss();
            }
        });
    }
}

