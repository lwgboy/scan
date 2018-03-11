package xxtt.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import xxtt.scan.view.AmountView;

public class SetNumberActivity extends Activity {

    private int position = -1;
    private int number = 0;

    AmountView amountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_number);

        amountView = (AmountView) findViewById(R.id.amount_view);
        amountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, int amount) {
                number = amount;
            }
        });

        Intent intent = this.getIntent();
        position = intent.getIntExtra("position", -1);
        number = intent.getIntExtra("number", 0);
        amountView.setAmount(number);

        if (position == -1 || number == 0) {
            Toast.makeText(this, "参数错误！", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            ((Button) this.findViewById(R.id.button_confirm)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("position", position);
                    intent.putExtra("number", number);
                    //设置返回数据
                    SetNumberActivity.this.setResult(RESULT_OK, intent);
                    //关闭Activity
                    SetNumberActivity.this.finish();
                }
            });
        }
    }
}