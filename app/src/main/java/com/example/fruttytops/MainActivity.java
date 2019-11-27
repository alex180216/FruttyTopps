package com.example.fruttytops;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText et_nombre;
    private ImageView iv_personaje;
    private TextView tv_bestscore;
    private MediaPlayer mp;

    int num_aleatorio = (int)(Math.random() * 10);//numero para randomizar al personaje

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        et_nombre = (EditText)findViewById(R.id.txt_nombre);
        iv_personaje= (ImageView) findViewById(R.id.imageView_personaje);
        tv_bestscore = (TextView)findViewById(R.id.tv_bestScore);


        //random personaje;///////////////////////////////////////////////////////////////
        int id;

        if(num_aleatorio == 0 || num_aleatorio == 10 || num_aleatorio ==6){
            id =getResources().getIdentifier("mango", "drawable", getPackageName());//busca a la imagen mango que esta
                 iv_personaje.setImageResource(id);                                               //dentro de drawable

        }else
            if(num_aleatorio == 1 || num_aleatorio == 9) {
                id = getResources().getIdentifier("fresa", "drawable", getPackageName());//busca a la imagen fresa que esta
                iv_personaje.setImageResource(id);                                                     //dentro de drawable
            } else
                if(num_aleatorio == 2 || num_aleatorio == 8){
                    id =getResources().getIdentifier("manzana", "drawable", getPackageName());//busca a la imagen manzana que esta
                                                                                                           // dentro de drawable
                    iv_personaje.setImageResource(id);
            }else
                if(num_aleatorio == 3 || num_aleatorio == 7){
                    id =getResources().getIdentifier("sandia", "drawable", getPackageName());
                    iv_personaje.setImageResource(id);
                }
                else
                if(num_aleatorio == 4 || num_aleatorio == 5 ){
                    id =getResources().getIdentifier("uva", "drawable", getPackageName());
                    iv_personaje.setImageResource(id);
                }

                //SQLITE ///////////////////////////////////////////////////////////////////////
        //conexion
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "db", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        //buscame el maximo puntaje de la tabla
        Cursor c = db.rawQuery("SELECT * FROM puntaje WHERE score = (SELECT MAX(score) FROM puntaje)", null);

        //que hacer con la base de datos

        if(c.moveToFirst()){
            String temp_nombre = c.getString(c.getColumnIndex("nombre"));
            String temp_score = c.getString(c.getColumnIndex("score"));
            tv_bestscore.setText("Record " + temp_score + " de " + temp_nombre);
            db.close();
        }else{ //si no encontraste nada... cierra la conexion a db
            db.close();

        }

                /////////////////////////////////////////////////////////////////////////////////

                //reproducir la pista de la app
        mp = MediaPlayer.create(this, R.raw.alphabet_song);
        mp.start();             //comienza a reproducir la pista
        mp.setLooping(true);    //repitela cada vez que termine
    }

    public void Jugar(View view){

        String nombre = et_nombre.getText().toString();

        if(!nombre.equals("")){//si el nombre es diferente a un espacio en blanco(osea que si se escribio un nombre...)

            mp.stop();//deten la musica de bienvenida
            mp.release();//libera recurso de la primera melodia

            Intent i = new Intent(MainActivity.this, Nivel1Activity.class);

            Bundle myB = new Bundle();

            myB.putString("jugador", nombre);

            i.putExtras(myB);
            startActivity(i);
            finish();


        }
        else{
            Toast.makeText(MainActivity.this, "Debes ingresar tu nombre", Toast.LENGTH_SHORT).show();

            et_nombre.requestFocus();        //enfocate en el edit text para que salga el teclado alli
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_nombre, InputMethodManager.SHOW_IMPLICIT); //salida del teclado
        }



    }

    //metodo par controlar la funcion de la flecha hacia atras de la app. (no permitir que el usuario de para atras)

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}

