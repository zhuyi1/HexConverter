package com.freegeek.jzzh;

import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.freegeek.jzzh.view.Keyboard;
import com.freegeek.jzzh.view.RadixEditText;

/**
 * @author Jack Fu <rtugeek@gmail.com>
 * @date 2017/11/15
 * @description
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AlertDialog radixPickerDialog;
    private TextView mTvLabelMisc;
    private RadixEditText mCurrentEditText;
    private Keyboard mKeyboard;
    private ClipboardManager clipboardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initView() {
        mTvLabelMisc = (TextView) findViewById(R.id.tv_label_misc);
        mKeyboard = (Keyboard) findViewById(R.id.keyboard);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        final String radix[] = {"3", "4", "5", "6", "7", "8", "9", "11", "12", "13", "14", "15"};
        radixPickerDialog = new AlertDialog.Builder(this).setItems(radix, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTvLabelMisc.setText(radix[i]);
                ((RadixEditText) findViewById(R.id.et_misc))
                        .setRadix(Integer.parseInt(radix[i]));
            }
        }).setNegativeButton(R.string.cancel, null).create();

        mTvLabelMisc.setOnClickListener(this);
        mKeyboard.setOnKeyboardListener(new Keyboard.OnKeyboardListener() {
            @Override
            public void onKeyboardClickListener(Button button, String value) {
                if("DEL".equalsIgnoreCase(value)){
                    String data =  mCurrentEditText.getText().toString();
                    if(data.trim().length() != 0){
                        mCurrentEditText.setText(data.substring(0, data.length() - 1));
                        mCurrentEditText.setSelection(mCurrentEditText.getText().length());
                    }
                }else if("COPY".equalsIgnoreCase(value)){
                    clipboardManager.setText(mCurrentEditText.getText().toString().replaceAll(" ", ""));
                    Toast.makeText(MainActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                }else if(".".equals(value)){
                    if(!mCurrentEditText.getText().toString().contains(".")){
                        mCurrentEditText.append(value);
                    }
                }else{
                    mCurrentEditText.append(value);
                }

            }

            @Override
            public void onKeyboardLongClickListener(Button button, String value) {
                if("DEL".equalsIgnoreCase(value)){
                    mCurrentEditText.setText("");
                }
            }
        });

        for (RadixEditText radixEditText : RadixEditText.getRadixEditTexts()) {
            radixEditText.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            RadixEditText radixEditText = (RadixEditText) v;
                            mKeyboard.setRadix(radixEditText.getRadix());
                            mKeyboard.show();
                            mCurrentEditText = radixEditText;
                            updateColor(radixEditText.getRadix());
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

        //hide the keyboard
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mKeyboard.hide();
                handler.removeCallbacks(this);
            }
        },500);
    }

    /**
     * Highlight focused edit text, edit text with 10 radix is special one.
     * @param radix which radix to highlight.
     */
    private void updateColor(int radix){
        int colorHint = getResources().getColor(R.color.textColorHint);
        int white = getResources().getColor(R.color.white);
        int colorPrimary = getResources().getColor(R.color.colorPrimary);
        for (RadixEditText radixEditText : RadixEditText.getRadixEditTexts()) {
            if(radixEditText.getRadix() == radix){
                int color = radix == 10 ? white : colorPrimary;
                radixEditText.setTextColor(color);
                radixEditText.setHintTextColor(color);
                continue;
            }
            if(radixEditText.getRadix() == 10){
                radixEditText.setHintTextColor(colorHint);
                radixEditText.setTextColor(colorHint);
                continue;
            }
            radixEditText.setHintTextColor(0xFFD4D4D4);
            radixEditText.setTextColor(0xFF616161);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_label_misc:
                radixPickerDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.menu_doc:
                startActivity(new Intent(this,DocActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 4 && mKeyboard.isShowing()){
            mKeyboard.hide();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
