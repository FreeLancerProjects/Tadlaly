package com.semicolon.tadlaly.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.semicolon.tadlaly.R;

import me.anwarshahriar.calligrapher.Calligrapher;

public class SearchActivity extends AppCompatActivity {
    private ImageView back;
    private ProgressBar progressBar;
    private AutoCompleteTextView searchView;
    private TextView no_search_result;
    private LinearLayout no_result_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);

        initView();
    }

    private void initView() {
         back = findViewById(R.id.back);
         progressBar = findViewById(R.id.progBar);
         searchView = findViewById(R.id.searchView);
        no_result_container = findViewById(R.id.no_result_container);
         no_search_result = findViewById(R.id.no_search_result);
         searchView.setOnEditorActionListener((textView, i, keyEvent) -> {
             if (EditorInfo.IME_ACTION_SEARCH==i
                     || KeyEvent.ACTION_DOWN == keyEvent.getAction()
                     || KeyEvent.KEYCODE_ENTER == keyEvent.getAction()
                     )

             {
                 Search(searchView.getText().toString());
             }
             return false;
         });
         back.setOnClickListener(view -> finish());
    }

    private void Search(String query) {

        if (!TextUtils.isEmpty(query))
        {
            progressBar.setVisibility(View.VISIBLE);
            no_search_result.setVisibility(View.VISIBLE);
            no_result_container.setVisibility(View.VISIBLE);
        }
    }
}
