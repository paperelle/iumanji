package com.example.giovy.iumanji;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giovy.iumanji.database.DbAdapter;
import com.example.giovy.iumanji.database.Pietanza;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScegliPietanza extends AppCompatActivity {

    private DbAdapter helper;
    private Button conferma;
    private Bundle bundle;
    private Cursor cursor;
    private long timer = 3;
    private MyCustomAdapter dataAdapter = null;
    private Integer id = 3;
    private Integer idGruppo;
    private TextView tot;
    private TextView cronometro;
    private List<Pietanza> pietanzeleList = new ArrayList<Pietanza>();
    private ListView list;
    private String prezzoTotale = "Prezzo totale € ";
    private String nomeGruppo;

    Double prezzoTot = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scegli_pietanza);

        bundle = getIntent().getExtras();
        idGruppo = bundle.getInt("idGruppo");
        nomeGruppo = bundle.getString("nomeGruppo");

        list = (ListView) findViewById(R.id.grigliaPietanze);
        cronometro = (TextView) findViewById(R.id.chronometer3);
        MyCountDownTimer myCountDownTimer;
        myCountDownTimer = new MyCountDownTimer(timer * 60000, 1000);
        myCountDownTimer.start();


        helper = DbAdapter.getInstance(this);
        helper.open();

        cursor=helper.fetchLocaliPietanzesByFilter(id.toString());

        while (cursor.moveToNext()) {
            Pietanza l = new Pietanza(cursor.getString(0), cursor.getDouble(1));
            pietanzeleList.add(l);
        }

        helper.close();
        cursor.close();

        dataAdapter = new MyCustomAdapter(this,R.layout.activity_scegli_pietanza_adapter, pietanzeleList);
        list.setAdapter(dataAdapter);
        tot = (TextView) findViewById(R.id.textView7);

        tot.setText(prezzoTotale + prezzoTot.toString());
        conferma = (Button) findViewById(R.id.conferma_button);
        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(prezzoTot == 0.0){

                AlertDialog.Builder builder=new AlertDialog.Builder(ScegliPietanza.this);
                builder.setTitle("Attenzione!");
                builder.setMessage("Non hai selezionato nessuna pietanza!");
                builder.show();

            } else {
                Intent showSondaggio = new Intent(ScegliPietanza.this, RiepilogoActivity.class);
                showSondaggio.putExtra("listaPietanze", (Serializable) pietanzeleList);
                showSondaggio.putExtra("idGruppo",idGruppo);
                showSondaggio.putExtra("nomeGruppo", nomeGruppo);
                startActivity(showSondaggio);
                finish();
            }
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Pietanza> {

        private ArrayList<Pietanza> productList;

        public MyCustomAdapter(Context context, int textViewResourceId, List<Pietanza> productList) {
            super(context, textViewResourceId, productList);
            this.productList = new ArrayList<Pietanza>();
            this.productList.addAll(productList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            View currentFocus = ((Activity)this.getContext()).getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
            Pietanza product = productList.get(position);
            if (view == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.activity_scegli_pietanza_adapter, null);
                EditText quantity = (EditText) view.findViewById(R.id.quantita);
                quantity.addTextChangedListener(new MyTextWatcher(view));

            }

            EditText quantity = (EditText) view.findViewById(R.id.quantita);
            quantity.setTag(product);
            if(product.getQuantita() != 0){
                quantity.setText(String.valueOf(product.getQuantita()));
                quantity.setSelection(quantity.getText().length());
            }
            else {
                quantity.setText("");
            }

            TextView prezzo = (TextView) view.findViewById(R.id.prezzo);
            prezzo.setText(product.getPrezzo().toString() +"0 €");

            TextView description = (TextView) view.findViewById(R.id.NomePietanzaScelta);
            description.setText(product.getNome());

            return view;

        }

    }
    private class MyTextWatcher implements TextWatcher{

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //do nothing
        }
        public void afterTextChanged(Editable s) {

            String qtyString = s.toString().trim();
            int quantity = qtyString.equals("") ? 0:Integer.valueOf(qtyString);

            EditText qtyView = (EditText) view.findViewById(R.id.quantita);
            Pietanza product = (Pietanza) qtyView.getTag();

            if(product.getQuantita() != quantity){

                Double currPrice = product.getPrezzo();
                Double extPrice = quantity * currPrice;
                Double oldPrice = product.getQuantita() * product.getPrezzo();
                prezzoTot -= oldPrice;
                prezzoTot += extPrice;
                tot.setText(prezzoTotale + prezzoTot.toString());

                product.setQuantita(quantity);
                product.setTotale(extPrice);

                if(product.getQuantita() != 0){
                    qtyView.setText(String.valueOf(product.getQuantita()));
                    qtyView.setSelection(qtyView.getText().length());
                }
                else {
                    qtyView.setText("");
                }

            }

            return;
        }
    }


    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisInFuture) {
            long seconds = millisInFuture/1000;
            long remainingSeconds=seconds%(60);
            long minutes=seconds/60;
            Toast.makeText(ScegliPietanza.this, Thread.currentThread().getName()+"", Toast.LENGTH_SHORT);
            if(cronometro.getText().toString().equals("0 : 20")){
                AlertDialog.Builder builder=new AlertDialog.Builder(ScegliPietanza.this);
                builder.setTitle("Attenzione!");
                builder.setMessage("Hai solo 20 secondi per concludere il tuo ordine ");
                builder.show();
            }
            if(minutes<10) {
                if(seconds < 10 ){
                    cronometro.setText("0" + minutes + " : " + "0"+remainingSeconds);
                } else {
                    cronometro.setText("0" + minutes + " : " + remainingSeconds);
                }
            } else {
                if(seconds < 10 ){
                    cronometro.setText(minutes + " : " + "0"+remainingSeconds);
                } else {
                    cronometro.setText(minutes + " : " + remainingSeconds);
                }
            }
        }

        @Override
        public void onFinish() {
            cronometro.setText("00:00");
            //Intent showSondaggio = new Intent(ScegliPietanza.this, VisualizzaGruppoActivity.class);
            //startActivity(showSondaggio);
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}


