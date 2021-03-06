package com.example.giovy.iumanji;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.giovy.iumanji.database.DbAdapter;
import com.example.giovy.iumanji.database.Locale;
import com.example.giovy.iumanji.database.Persona;
import com.example.giovy.iumanji.database.Pietanza;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiepilogoLocale extends AppCompatActivity {

    private ListView listview;
    private String nomeLocale;
    private String idLocale;
    private String immagineLocale;
    private ArrayList<Pietanza> pietanzaList = new ArrayList<>();
    private TextView nome;
    private DbAdapter helper;
    private ImageView imgLocale;
    private Cursor cursor;
    private ImageButton aggiungi_pietanza_button;
    private EditText nome_pietanza;
    private EditText prezzo_pietanza;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo_locale);

        bundle = getIntent().getExtras();
        nomeLocale = bundle.getString("nomeLocale");
        idLocale = bundle.getString("idLocale");
        immagineLocale = bundle.getString("immagineLocale");

        //nome = (TextView) findViewById(R.id.nomeLocale1);
        //nome.setText(nomeLocale);
        nome = (TextView) findViewById(R.id.nomeLocale2);
        nome.setText(nomeLocale);

        imgLocale = (ImageView) findViewById(R.id.imageView2);
        nome_pietanza = (EditText) findViewById(R.id.nome_pietanza);
        prezzo_pietanza = (EditText) findViewById(R.id.prezzo_pietanza);

        Bitmap bitmap = BitmapFactory.decodeFile("/storage/BF1A-1C16/Images/" + immagineLocale + ".jpg");
        imgLocale.setImageBitmap(BitmapFactory.decodeFile("/storage/BF1A-1C16/Images/" + immagineLocale + ".jpg"));

        listview = (ListView) findViewById(R.id.lista_pietanze_view);

        // String[] listContent = getPietanze();

       /* ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listContent);*/

        // listview.setAdapter(adapter);

        aggiungi_pietanza_button = (ImageButton) findViewById(R.id.aggiungi_pietanza_button);
        aggiungi_pietanza_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomePietanza = nome_pietanza.getText().toString();
                String prezzoPietanza = prezzo_pietanza.getText().toString();

                Context context = getApplicationContext();

                helper = DbAdapter.getInstance(context);
                helper.open();

                Integer i = (helper.fetchMaxIdPietanza()) + 1;

                if (check(nomePietanza, prezzoPietanza)) {
                    helper.createPietanza(i.toString(), nomePietanza, prezzoPietanza);
                    helper.createlocaliPietanze(idLocale, i.toString());
                    helper.close();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

                helper.close();
            }
        });


    /*public String[] getPietanze (){
        ArrayList<String> lista = new ArrayList<>();*/

        helper = DbAdapter.getInstance(this);
        helper.open();

        cursor = helper.fetchLocaliPietanzesByFilter(idLocale);

        while (cursor.moveToNext()) {
            Pietanza p = new Pietanza(cursor.getString(0),cursor.getDouble(1));
            pietanzaList.add(p);
        }

        RiepilogoLocaleAdapter adapter2 = new RiepilogoLocaleAdapter(this,pietanzaList);
        helper.close();
        cursor.close();

        listview.setAdapter(adapter2);
        //}
    }
    public Boolean check(String nome, String prezzo){
        if(nome.isEmpty()) nome_pietanza.setError("Campo obbligatorio");
        if(prezzo.isEmpty()) prezzo_pietanza.setError("Campo obbligatorio");

        return !(nome.isEmpty() || prezzo.isEmpty());
    }
}
