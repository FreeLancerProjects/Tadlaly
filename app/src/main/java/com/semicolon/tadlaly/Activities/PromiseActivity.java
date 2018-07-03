package com.semicolon.tadlaly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.R;

public class PromiseActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView back;
    private Button agreeBtn1,agreeBtn2,agreeBtn3,disagreeBtn1,disagreeBtn2,disagreeBtn3,send_btn;
    private boolean brand1=true;
    private boolean brand2=true;
    private boolean brand3=true;
    private TextView txt_quran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise);
        initView();
       /* Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"JannaLT-Regular.ttf",true);
*/
    }

    private void initView() {
        back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
        txt_quran    = findViewById(R.id.txt_quran);
        txt_quran.setText("\"وَأَوْفُوا بِعَهْدِ اللَّهِ إِذَا عَاهَدْتُمْ وَلَا تَنْقُضُوا الْأَيْمَانَ بَعْدَ تَوْكِيدِهَا وَقَدْ جَعَلْتُمُ اللَّهَ عَلَيْكُمْ كَفِيلًا\"");

        send_btn     = findViewById(R.id.send_btn);
        agreeBtn1    = findViewById(R.id.agreeBtn1);
        agreeBtn2    = findViewById(R.id.agreeBtn2);
        agreeBtn3    = findViewById(R.id.agreeBtn3);
        disagreeBtn1 = findViewById(R.id.disagreeBtn1);
        disagreeBtn2 = findViewById(R.id.disagreeBtn2);
        disagreeBtn3 = findViewById(R.id.disagreeBtn3);


        agreeBtn1.setOnClickListener(this);
        agreeBtn2.setOnClickListener(this);
        agreeBtn3.setOnClickListener(this);
        disagreeBtn1.setOnClickListener(this);
        disagreeBtn2.setOnClickListener(this);
        disagreeBtn3.setOnClickListener(this);

        send_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.agreeBtn1:
                disagreeBtn1.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn1.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.white));
                disagreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));

                brand1=true;
                break;
            case R.id.agreeBtn2:
                disagreeBtn2.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn2.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.white));
                disagreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));

                brand2 = true;
                break;
            case R.id.agreeBtn3:
                disagreeBtn3.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn3.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.white));
                disagreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));

                brand3=true;
                break;
            case R.id.disagreeBtn1:
                disagreeBtn1.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn1.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                disagreeBtn1.setTextColor(ContextCompat.getColor(this,R.color.white));

                brand1 = false;
                break;
            case R.id.disagreeBtn2:
                disagreeBtn2.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn2.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                disagreeBtn2.setTextColor(ContextCompat.getColor(this,R.color.white));

                brand2 = false;
                break;
            case R.id.disagreeBtn3:
                disagreeBtn3.setBackgroundResource(R.drawable.btn_promise_agree);
                agreeBtn3.setBackgroundResource(R.drawable.btn_promise_disagree);
                agreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                disagreeBtn3.setTextColor(ContextCompat.getColor(this,R.color.white));

                brand3 = false;
                break;

            case R.id.send_btn:
                if (!brand1)
                {
                    Toast.makeText(this, "لايمكنك إضافة اعلان دون الموافقة علي التعهد الاول", Toast.LENGTH_LONG).show();
                }else if (!brand2)
                {
                    Toast.makeText(this, "لايمكنك إضافة اعلان دون الموافقة علي التعهد الثاني", Toast.LENGTH_LONG).show();

                }
                else if (!brand3)
                {
                    Toast.makeText(this, "لايمكنك إضافة اعلان دون الموافقة علي التعهد الثالث", Toast.LENGTH_LONG).show();

                }else
                    {
                        Intent intent = getIntent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                break;
        }
    }
}
