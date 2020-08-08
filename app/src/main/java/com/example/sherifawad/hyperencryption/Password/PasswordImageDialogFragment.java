package com.example.sherifawad.hyperencryption.Password;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sherifawad.hyperencryption.R;

import java.math.BigInteger;


public class PasswordImageDialogFragment extends DialogFragment {
    Callback passCallback;
    private String pass = "";

    private int imageLayoutWidth;

    public static String TAG = "FullScreenDialog";

    public interface Callback {
        //Pass the password to the Activity
        void setData(String pass);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            passCallback= (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
//        passCallback= (Callback) context;
    }

    @Override
    public void onDetach() {
        passCallback = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return super.onCreateDialog(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();


        String image_path = null;
//        BigInteger sFirstText = null;
//        BigInteger sSecondText = null;

        int sFirstText = 0;
        int sSecondText = 0;
        String sOperation = null;
        char sCharText = 0;
        int sNextCharText = 0;

        // Check for thr arguments
        if (getArguments() != null) {



            try {

//                sFirstText = new BigInteger(getArguments().getString("sFirstText"));
//                sSecondText = new BigInteger(getArguments().getString("sSecondText"));

                sFirstText = getArguments().getInt("sFirstText");
                sSecondText = getArguments().getInt("sSecondText");
                sCharText = (getArguments().getString("sCharText")).charAt(0);
                sCharText = (getArguments().getString("sCharText")).charAt(0);
                sNextCharText =getArguments().getInt("sNextCharText");
//                sNextCharText = Integer.parseInt(getArguments().getString("sNextCharText"));
                image_path = getArguments().getString("sImagePath");
                sOperation = getArguments().getString("sOperation");


            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Toast message
                Toast.makeText(getActivity(), "Invalid Values",
                        Toast.LENGTH_LONG).show();
                // Dismiss the dialog
                DialogDismiss();

            }

        }


        DialogFragmentButtonListener((Button)view.findViewById(R.id.okButton));
        DialogFragmentButtonListener((Button) view.findViewById(R.id.cancelButton));

        // Get layout by id
        final RelativeLayout relativeLayout = view.findViewById(R.id.fragmentFrameLayout);

        // Get activity screen resolution width
        int displayWidth = displayMetrics(fragmentBelongActivity).widthPixels;

        // Set imageLayout width to activity screen resolution
        imageLayoutWidth = displayWidth;
        // Cell View desired width
        int cellWidth = imageLayoutWidth / 10;


        // Create imageLayout as linearLayout
        LinearLayout imageLayout = new LinearLayout(getActivity());
        // Set linearLayout orientation
        imageLayout.setOrientation(LinearLayout.VERTICAL);
        // Set layout width and height (width == height)
        imageLayout.setLayoutParams(new ViewGroup.LayoutParams(imageLayoutWidth, imageLayoutWidth));

        // Convert image to bitmapImage
        Bitmap bitmapImage = BitmapFactory.decodeFile(image_path);
        // Convert bitmapImage to drawable
        Drawable drawable = new BitmapDrawable(getResources(), bitmapImage);
        // Set the drawable as background for imageLayout
        imageLayout.setBackground(drawable);


        // Check the values of the variables
        try {
//            if (sFirstText != null && sSecondText != null && !sOperation.isEmpty() && sCharText != 0 && sNextCharText != 0) {
            if (sFirstText != 0 && sSecondText != 0 && !sOperation.isEmpty() && sCharText != 0 && sNextCharText != 0) {
                PasswordImageConstruction(imageLayout, fragmentBelongActivity,
                        sFirstText, sSecondText, sOperation, sCharText, sNextCharText, cellWidth, cellWidth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Add imageLayout to parent layout "relativeLayout"
        relativeLayout.addView(imageLayout);

    }

    @Override
    public void onResume() {

    // Store access variables for window and blank point
    Window window = getDialog().getWindow();

    // Set dialogFragment width and height
    window.setLayout(imageLayoutWidth, WindowManager.LayoutParams.WRAP_CONTENT);

    super.onResume();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Custom dialogFragment style
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    /**
     * Buttons click listener
     * @param button
     */
    public void DialogFragmentButtonListener(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                switch (button.getId()){
                    case R.id.okButton:
                        if (pass.isEmpty()){
                            return;
                        }
                        passCallback.setData(pass);
                        System.out.println("THe password of PasswordImageDialogFragment is " + pass);
                        pass = "";
                        DialogDismiss();
//                        try {
//                            //Client client = new Client("1236547896541596", "Sherif", pass.toCharArray(), "+201280412208");
//                            setPreference("client", Uty.serialize(new Client("1236547896541596", "Sherif", pass.toCharArray(), "+201280412208")));
//                            pass = "";
//                            Intent cryStegActivity = new Intent(getActivity(), CryStegActivity.class);
//                            startActivity(cryStegActivity);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("THe password is " + pass);

                        break;
                    case R.id.cancelButton:

                        // Dismiss DialogFragment
                        DialogDismiss();
                        break;
                }
            }
        });
    }

    /**
     * A method to dismiss  the dialogFragment
     */
    private void DialogDismiss() {
        getDialog().dismiss();
    }

    /**
     * Create custom grid layout
     * @param linearLayout The parent layout view
     * @param context Layout context
     * @param sFirstText BigInteger
     * @param sSecondText BigInteger
     * @param sOperation To specify the desired
     * @param sCharText  char
     * @param sNextCharText int
     * @param cellWidth Custom cell view width
     * @param cellHeight Custom cell view height
     */
    private void PasswordImageConstruction(LinearLayout linearLayout, final Context context,
                                           int sFirstText, int sSecondText,
                                           String sOperation, char sCharText, int sNextCharText, final int cellWidth, final int cellHeight) {

        int sResult = sNextCharText;

        // Loop of number of desired rows
        for (int i = 0; i < 10; i++) {
            // Create a linear layout for every row
            LinearLayout row = new LinearLayout(context);
            // Set layout parameters
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            // Loop for every cell in a row
            for (int j = 0; j < 10; j++) {

                // Assigned text for every cell, the calculation result and the char
                final String txt;
                txt = String.valueOf(sCharText) + String.valueOf(Math.abs(sResult));



                // Create a new cell layout
                final Cell cell = new Cell(context, cellWidth, cellHeight, imageLayoutWidth);
                // Listen to the cell click
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println(txt);
                        // Add the select cell assigned text to a password
                        pass += txt;

                        // Disable the cell reClicking
                        cell.setClickable(false);
                        // Change the cell color after clicking
                        cell.setBackground(cell.getGradientDrawable(ResourcesCompat.getColor(getResources(), R.color.light_red, null), imageLayoutWidth));
                    }
                });
                // Add every created cell to the row layout
                row.addView(cell);
                // change the char to the next according to the sNextCharText(int)
                sCharText += sNextCharText;
                // The calculation process according to

                if (sFirstText == 0)
                    sFirstText = sNextCharText;

                if (sResult == 0)
                    sResult = sNextCharText;

                sSecondText = sFirstText;
                sFirstText = sResult;

                sResult = CalculateInt(sOperation, sFirstText, sSecondText);

            }
            // Add every row the the parent layout
            linearLayout.addView(row);
        }
    }

    /**
     * Get the screen resolution  for specific activity
     * @param context Activity context
     * @return
     */
    private DisplayMetrics displayMetrics(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics;
    }

    /**
     * Measure the view layout dimensions
     * @param view The desired layout view
     * @return
     */
    private View ViewMetrics(final View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return view ;
    }

    /**
     * Calculation method with bigIntegers
     * @param sOperation The operation
     * @param sFirstText First bigInteger number
     * @param sSecondText Second bigInteger number
     * @return
     */
    private BigInteger Calculate(String sOperation, BigInteger sFirstText, BigInteger sSecondText) {
        // Switch with multiple cases according to the operation type
        switch (sOperation){
            case "Add":
                return sFirstText.add(sSecondText);
            case "Subtract":
                return sFirstText.subtract(sSecondText);
            case "Multiply":
                return sFirstText.multiply(sSecondText);
            default:
                return null;
        }
    }
    /**
     * Calculation method with int numbers
     * @param sOperation The operation
     * @param sFirstText First int number
     * @param sSecondText Second int number
     * @return
     */
    private int CalculateInt(String sOperation, int sFirstText, int sSecondText) {
        // Switch with multiple cases according to the operation type
        switch (sOperation){
            case "Add":
                return sFirstText + sSecondText;
            case "Subtract":
                return sFirstText - sSecondText;
            case "Multiply":
                return sFirstText * sSecondText;
            default:
                return 0;
        }
    }

}

