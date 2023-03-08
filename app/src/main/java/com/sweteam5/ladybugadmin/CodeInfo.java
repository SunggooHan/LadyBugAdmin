package com.sweteam5.ladybugadmin;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CodeInfo extends LinearLayout {

    private MngInfoActivity mngInfoActivity;    // Parent activity caching
    private MngInfoActivity.CodeType codeType;  // Code type of this instance (driver code / bus code)
    public EditText codeEditText;               // EditText view of the code
    public ImageButton deleteCodeButton;        // Delete button of this layer group
    private TextView codeTextView;              // Title TextView of code group

    public CodeInfo(Context context, AttributeSet attrs, String title,
                    MngInfoActivity.CodeType codeType, MngInfoActivity mngInfoActivity) {
        super(context, attrs);

        this.codeType = codeType;
        this.mngInfoActivity = mngInfoActivity;

        init(context, title);
    }

    public CodeInfo(Context context, String title,
                    MngInfoActivity.CodeType codeType, MngInfoActivity mngInfoActivity) {
        super(context);

        this.codeType = codeType;
        this.mngInfoActivity = mngInfoActivity;

        init(context, title);
    }

    // Initialize the setting data
    private void init(Context context, String title){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.info_code_layout, this, true);

        codeTextView = findViewById(R.id.codeTextView);
        setTitle(title);
        codeEditText = findViewById(R.id.codeEditText);

        // Set delete button listener
        deleteCodeButton = findViewById(R.id.deleteCodeButton);
        deleteCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mngInfoActivity.deleteCode(codeType, CodeInfo.this);
            }
        });
    }

    // Set the title of code layout group
    public void setTitle(String title) {
        codeTextView.setText(title);
    }

    // Get Code from CodeEditText in string type
    public String getCodeOnEditText() {
        return codeEditText.getText().toString();
    }

    // Set code to CodeEditText and make it uneditable
    public void initCode(String code) {
        codeEditText.setText(code);
        codeEditText.setInputType(InputType.TYPE_NULL);
    }
}