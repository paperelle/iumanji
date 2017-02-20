package com.example.giovy.iumanji;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.giovy.iumanji.database.DbAdapter;
import com.example.giovy.iumanji.database.Persona;
import com.example.giovy.iumanji.database.Pietanza;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RipilogoCronologia extends AppCompatActivity {

    private DbAdapter helper;
    private Cursor cursor;
    private List<Pietanza> pietanzaList = new ArrayList<>();
    private ListView listview_nome;
    private Double somma =0.00;
    private TextView textviewSomma;
    private TextView nomeGruppoText;
    private Button tornaHome;
    private Bundle bundle;
    private Integer idGruppo;
    private String nomeGruppo;
    private Integer idLocale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo);

        bundle = getIntent().getExtras();
        idLocale = bundle.getInt("idLocale");
        System.out.println(idLocale);
        nomeGruppo = bundle.getString("nomeGruppo");


        tornaHome = (Button) findViewById(R.id.torna_home_button) ;
        nomeGruppoText = (TextView) findViewById(R.id.NomeLocaleRiepilogo) ;
        listview_nome = (ListView) findViewById(R.id.nome_pietanza_listview);
        textviewSomma =(TextView)  findViewById(R.id.somma_ordine);
        ArrayList<Pietanza> pietanzeScelte = (ArrayList<Pietanza>) getIntent().getSerializableExtra("listaPietanze");

        nomeGruppoText.setText(nomeGruppo);

        tornaHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showHome = new Intent(RipilogoCronologia.this, VisualizzaGruppoActivity.class);
                showHome.putExtra("idGruppo",idGruppo);
                showHome.putExtra("nomeGruppo", nomeGruppo);
                startActivity(showHome);
            }
        });

        helper = DbAdapter.getInstance(this);
        helper.open();

        cursor=helper.fetchLocaliPietanzesByFilter(idLocale.toString());
        System.out.println(idLocale + " Dio hai rotto il cazzo");
        Boolean isPresent;
        while (cursor.moveToNext()) {
            Pietanza p = new Pietanza(cursor.getString(0), cursor.getDouble(1));
            p.setQuantita(1);
            pietanzaList.add(p);

            somma = somma + (new BigDecimal(p.getPrezzo()).setScale(2 , BigDecimal.ROUND_UP).doubleValue());
            System.out.println(p.getNome());
            System.out.println(somma);
        }
        RiepilogoAdapter adapter2 = new RiepilogoAdapter(this,pietanzaList);
        helper.close();
        cursor.close();

        listview_nome.setAdapter(adapter2);

        textviewSomma.setText("Costo totale: " + somma + " €");

        listview_nome.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
