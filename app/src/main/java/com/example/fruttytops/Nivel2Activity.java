package com.example.fruttytops;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Nivel2Activity extends AppCompatActivity {

    private TextView tv_nombre, tv_score;
    private EditText et_respuesta;
    private ImageView iv_num1, iv_num2, iv_operador, iv_vidas;
    private MediaPlayer mp, mp_bien, mp_mal;
    private Button bt_comprobar;

    //VARIABLES
    int score, numAleatorio_uno, numAleatorio_dos, resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas ;

    //ARREGLO DE CADENAS DE CARATERES DE IMAGENES
    String numero[] = {"cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //CONEXION DE ELEMENTOS/////////////////////////////////////////////////////////
        tv_nombre = (TextView) findViewById(R.id.tv_nombre);
        tv_score = (TextView) findViewById(R.id.tv_score);
        et_respuesta = (EditText) findViewById(R.id.et_resultado);
        iv_num1 = (ImageView) findViewById(R.id.iv_fact1);
        iv_num2 = (ImageView) findViewById(R.id.iv_fact2);
        iv_operador = (ImageView) findViewById(R.id.iv_operador);
        iv_vidas = (ImageView) findViewById(R.id.iv_vidas);
        bt_comprobar = (Button)findViewById(R.id.bt_comprobar);

        Toast.makeText(Nivel2Activity.this, "Nivel 2: Sumas moderadas", Toast.LENGTH_SHORT).show();
        Bundle myB = this.getIntent().getExtras();


        //reproducir la pista del activity
        mp = MediaPlayer.create(this, R.raw.goats);
        mp.start();             //comienza a reproducir la pista
        mp.setLooping(true);    //repitela cada vez que termine

        //pista de "bien hecho" y "mal"
        mp_bien = MediaPlayer.create(this, R.raw.wonderful);
        mp_mal = MediaPlayer.create(this, R.raw.bad);






        if(myB != null){
            nombre_jugador = myB.getString("jugador");
            string_score = myB.getString("score");  //recupero puntaje
                score = Integer.parseInt(string_score);
            string_vidas = myB.getString("vidas");   //recupero vidas
                vidas = Integer.parseInt(string_vidas);


            tv_nombre.setText("Jugador: "+ nombre_jugador);
            tv_score.setText("Score: "+ score);

            if(vidas == 3){
                iv_vidas.setImageResource(R.drawable.tresvidas);
            }else if(vidas==2){
                iv_vidas.setImageResource(R.drawable.dosvidas);
            }else if(vidas == 1){
                iv_vidas.setImageResource(R.drawable.unavida);
            }

        }

        NumAleatorio();
    }

    //METODO PARA EL BOTON COMPARAR

    public void Compara(View view){

        String respuesta = et_respuesta.getText().toString();
        if (respuesta.equals("")){
            Toast.makeText(this, "Debes introducir tu respuesta", Toast.LENGTH_LONG).show();
        }else{

            int respuesta_jugador = Integer.parseInt(respuesta);

            if(resultado == respuesta_jugador ){//la respuesta es correcta
                mp_bien.start();
                score++;
                tv_score.setText("Score: "+ score); //incrementame el puntaje
                et_respuesta.setText("");
                BaseDeDatos(); //ACTUALIZAME LA BASE DE DATOS CON CADA JUGADA

            }else{//la respuesta es incorrecta
                mp_mal.start();
                et_respuesta.setText("");

                //disminuir las vidas
                vidas--;
                BaseDeDatos(); //ACTUALIZAME LA BASE DE DATOS CADA VEZ QUE SE TERMINE EL JUEGO

                switch (vidas){
                    case 3:
                        iv_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        iv_vidas.setImageResource(R.drawable.dosvidas);
                        Toast.makeText(this, "Te quedan 2 manzanas", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        iv_vidas.setImageResource(R.drawable.unavida);
                        Toast.makeText(this, "Te queda 1 manzana", Toast.LENGTH_SHORT).show();
                        break;
                    case 0: //SE QUEDO SIN VIDAS, FINALIZA EL JUEGO

                        Toast.makeText(this, "HAS PERDIDO TODAS TUS VIDAS", Toast.LENGTH_LONG).show();

                        //volvemos al activity de bienvenida
                        Intent i = new Intent(Nivel2Activity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        mp.stop();
                        mp.release();
                        et_respuesta.setText("");
                        break;
                }

            }

            NumAleatorio(); //ACTUALIZAME LA OPERACION

        }




    }



    public void NumAleatorio(){
        if(score<=19){//estamos en nivel 2

            numAleatorio_uno =(int)(Math.random()*10);
            numAleatorio_dos =(int)(Math.random()*10);

            resultado = numAleatorio_uno + numAleatorio_dos;

            //el maximo resultado a obtener es 20

            
                for(int i =0; i <numero.length; i++){
                    int id = getResources().getIdentifier(numero[i],"drawable",getPackageName());
                    if(numAleatorio_uno==i){
                        iv_num1.setImageResource(id);//ponme la imagen que consigas en id
                    }if(numAleatorio_dos == i){
                        iv_num2.setImageResource(id);
                    }
                }




        }else{//pasamos al activity nivel 2

            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);

            Intent i = new Intent(Nivel2Activity.this, Nivel3Activity.class);

            Bundle myB = new Bundle();

            myB.putString("jugador", nombre_jugador);
            myB.putString("score", string_score);
            myB.putString("vidas", string_vidas);

            i.putExtras(myB);
            startActivity(i);
            finish();
            mp.stop();//deten la musica de actvity
            mp.release();//libera recurso de la melodia



        }
    }

    //ACTUALIZACION DE SCORE en BASE DE DATOS

    public void BaseDeDatos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "db", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        //consultar que existan registros y
        //buscame el maximo puntaje de la tabla
        Cursor c = db.rawQuery("SELECT * FROM puntaje WHERE score = (SELECT MAX(score) FROM puntaje)", null);

        if(c.moveToFirst()){//si hay un primero
            String temp_nombre = c.getString(0);
            String temp_score = c.getString(1);

            int bestScore = Integer.parseInt(temp_score);  //GUARDAME EL MAYOR

            if(score > bestScore){
                ContentValues modificacion = new ContentValues();

                modificacion.put("nombre", nombre_jugador);
                modificacion.put("score", score);

                //indicar a la base de datos la modificacion
                db.update("puntaje", modificacion, "score = " + bestScore, null);

            }
            db.close();

        }else{ //si no hay registros de puntajes

            ContentValues insertar = new ContentValues();

            insertar.put("nombre", nombre_jugador);
            insertar.put("score", score);

            db.insert("puntaje", null, insertar);
            db.close();
        }

    }

    //metodo par controlar la funcion de la flecha hacia atras de la app. (no permitir que el usuario de para atras)
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
