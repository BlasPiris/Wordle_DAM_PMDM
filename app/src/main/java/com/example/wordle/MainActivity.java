package com.example.wordle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    //VARIABLES FINALES PARA EL NUMERO DE FILAS Y COLUMNAS DE LOS BOTONES
    final int NUM_COL_LET=5;
    final int NUM_FIL_LET=6;
    final int NUM_BUT=NUM_COL_LET*NUM_FIL_LET;
    final int NUM_COL_KEY=10;
    final int NUM_FIL_KEY=4;
    final int BOR_PIXEL=5;
    final String[] letrasTeclado={"Q","W","E","R","T","Y","U","I","O","P","A",
    "S","D","F","G","H","J","K","L","Ñ","Z","X","C","V","B","N","M"};

    //VARIABLES GLOBALES DEL JUEGO
    int posLetter=0;
    int completeLetterOfWord=0;
    String wordGame="";
    ArrayList<String> charsGame = new ArrayList<>();
    ArrayList<String> charPlayer=new ArrayList<>();
    ArrayList<Integer>  idKeyWordPlayer= new ArrayList<Integer>();
    String wordPlayer="";
    Thread thread;
    int comprobaciones=0;
    int puntuacion=0;
    boolean blockKeyboard=false;


    //ELEMENTOS DE LAYOUT
    GridLayout gridLayoutLetras;
    GridLayout gridLayoutTeclado;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //INSTANCIA DE ELEMENTOS DE LAYOUT
        gridLayoutLetras=findViewById(R.id.gridLayoutLetras);
        gridLayoutTeclado= findViewById(R.id.gridLayoutTeclado);
        linearLayout= findViewById(R.id.linearLayout);

        //RUNNABLE PARA INICIAR EL JUEGO
        linearLayout.post(new Runnable() {
            @Override
            public void run() {
                newGame();
            }
        });
    }

    //METODO PARA CREAR PANEL DE JUEGO
    private void createPanel(){
        int width=gridLayoutLetras.getWidth();
        int height=gridLayoutLetras.getHeight();

        //CREAMOS LOS BOTONES DE LAS LETRAS CON UN TAMAÑO AJUSTADO A LA PANTALLA
        for (int i=0;i<NUM_BUT;i++){
            Button btn=new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (width/NUM_COL_LET)-(BOR_PIXEL*2),
                    (width/NUM_FIL_LET)-(BOR_PIXEL*2)
            );
            params.setMargins(BOR_PIXEL, BOR_PIXEL, BOR_PIXEL, BOR_PIXEL);


            btn.setId(i);
             btn.setBackgroundColor(Color.rgb(220,220,220));
            btn.setClickable(false);

            btn.setLayoutParams(params);
            gridLayoutLetras.addView(btn);
        }
    }

    //METODO PARA CREAR EL TECLADO
    private void createKeyboard(){
        int width=gridLayoutTeclado.getWidth();
        int height=gridLayoutTeclado.getHeight();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (width/NUM_COL_KEY)-(BOR_PIXEL*2)
                ,(width/NUM_COL_KEY)-(BOR_PIXEL*2));
        params.setMargins(BOR_PIXEL, BOR_PIXEL, BOR_PIXEL, BOR_PIXEL);
        //CREAMOS EL TECLADO CON LOS CARACTERES DE LA 'A' A LA 'Z'
        for(int i=0;i<letrasTeclado.length;i++){
            Button btn=new Button(this);
            btn.setText(letrasTeclado[i]);
            btn.setId(i+NUM_BUT);
            btn.setLayoutParams(params);
            btn.setBackgroundColor(Color.rgb(220,220,220));
            String finalCar = letrasTeclado[i];

            //AÑADIMOS EL EVENTO CLICK, PARA QUE CADA VEZ QUE PULSE, SE ESCRIBA LA LETRA EN EL PANEL
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   setLetterInPanel(finalCar,btn.getId());
                }
            });

            gridLayoutTeclado.addView(btn);
      }

        //CREAMOS EL BOTON DE BORRADO DE LETRA
        ImageButton ib=new ImageButton(this);
        ib.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24);
        ib.setLayoutParams(params);
        ib.setBackgroundColor(Color.rgb(220,220,220));
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLetterInPanel();
            }
        });
        gridLayoutTeclado.addView(ib);

        //CREAMOS EL BOTON DE ENTER PARA COMPROBAR UNA LINEA
        ib=new ImageButton(this);
        ib.setImageResource(R.drawable.ic_baseline_subdirectory_arrow_right_24);
        ib.setLayoutParams(params);
        ib.setBackgroundColor(Color.rgb(220,220,220));
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterWordInPanel();
            }
        });
        gridLayoutTeclado.addView(ib);
    }

    //METODO QUE ESCRIBE LA LETRA EN SU BOTON
    private void setLetterInPanel(String finalCar,int letterID) {

        if(!blockKeyboard){
            if(posLetter<NUM_BUT){
                if(!(posLetter % NUM_COL_LET == 0) || posLetter==0){
                    Button btn=findViewById(posLetter);
                    btn.setText(finalCar);
                    posLetter++;
                    completeLetterOfWord++;
                    wordPlayer+=finalCar;
                    idKeyWordPlayer.add(letterID);
                }else if(completeLetterOfWord!=NUM_COL_LET){
                    Button btn=findViewById(posLetter);
                    btn.setText(finalCar);
                    posLetter++;
                    completeLetterOfWord++;
                    wordPlayer+=finalCar;
                    idKeyWordPlayer.add(letterID);
                }
        }

        }
    }

    //METODO QUE BORRA UNA LETRA DE SU BOTON
    private void deleteLetterInPanel(){
            if (completeLetterOfWord != 0) {
                Button btn = findViewById(posLetter - 1);
                btn.setText(" ");
                posLetter--;
                completeLetterOfWord--;
                wordPlayer = wordPlayer.substring(0, wordPlayer.length()-1);
                idKeyWordPlayer.remove(idKeyWordPlayer.size()-1);
            }

    }

    //METODO QUE COMPRUEBA LA PALABRA INTRODUCIDA
    private void enterWordInPanel(){

        if(posLetter<=NUM_BUT && completeLetterOfWord == NUM_COL_LET) {
            checkWord();
            completeLetterOfWord = 0;
        }
    }

    private void checkWord() {
        int writeButtons=posLetter-1;
        int writeButtons2=posLetter-NUM_COL_LET;

        for(int i=0;i<NUM_COL_LET;i++){
            int resID = getResources().getIdentifier(String.valueOf(writeButtons), "id", getPackageName());
            Button btn=findViewById(resID);
            charPlayer.add(0, (String) btn.getText());
            writeButtons--;
        }
        System.out.println(charsGame+" "+charPlayer);

        if(charPlayer.equals(charsGame)){
            for(int i=0;i<NUM_COL_LET;i++){
                int resID = getResources().getIdentifier(String.valueOf(writeButtons2), "id", getPackageName());
                Button btn=findViewById(resID);
                btn.setBackgroundColor(Color.GREEN);
                blockKeyboard=true;
                writeButtons2++;
            }

            puntuacion++;
            Toast.makeText(this,"ACERTASTE. PALABRAS ACERTADAS: "+Integer.toString(puntuacion),Toast.LENGTH_LONG).show();
            restartGame();
        }else{
            comprobaciones++;
            for(int i=0;i<NUM_COL_LET;i++){
                CharSequence letter=charPlayer.get(i);

                    Button btn;
                    if(charsGame.get(i).equals(letter)){
                        int resID = getResources().getIdentifier(String.valueOf(writeButtons2), "id", getPackageName());
                        btn=findViewById(resID);
                        btn.setBackgroundColor(Color.GREEN);
                        writeButtons2++;

                    }else if(charsGame.contains(letter) && !charsGame.get(i).equals(letter)){

                        int resID = getResources().getIdentifier(String.valueOf(writeButtons2), "id", getPackageName());
                        btn=findViewById(resID);
                        btn.setBackgroundColor(Color.YELLOW);
                        writeButtons2++;

                    }
                    else{

                    int idLetter=idKeyWordPlayer.get(i);
                    idLetter = getResources().getIdentifier(String.valueOf(idLetter), "id", getPackageName());
                     btn= findViewById(idLetter);
                    btn.setBackgroundColor(Color.GRAY);
                    int resID = getResources().getIdentifier(String.valueOf(writeButtons2), "id", getPackageName());
                    btn=findViewById(resID);

                    btn.setBackgroundColor(Color.GRAY);
                    writeButtons2++;

                }
            }
            if(comprobaciones==NUM_FIL_LET){
                Toast.makeText(this, "FALLASTE, LA PALABRA ERA "+wordGame,Toast.LENGTH_LONG).show();
                restartGame();
            }
            charPlayer.clear();
            idKeyWordPlayer.clear();

        }
    }

    //METODO QUE GENERA UNA PALABRA ALEATORIA DEL ARCHIVO DE PALABRAS
    private void getRandomWord() throws IOException {
        InputStream is=getAssets().open("palabras5.txt", AssetManager.ACCESS_RANDOM);
        int nPalabras=is.available()/(NUM_COL_LET+1);
        int ran = new Random().nextInt(nPalabras);
        int skip=(ran*(NUM_COL_LET+1))-NUM_COL_LET-1;
        is.skip(skip);
        byte b[]=new byte[NUM_COL_LET];
        is.read(b);
        wordGame=new String(b).toUpperCase(Locale.ROOT);
        for (char c : wordGame.toCharArray()) {
            charsGame.add(String.valueOf(c));
        }
    }

    //METODO QUE REINICIA EL JUEGO
    private void restartGame(){
         thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newGame();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ;
        };
        thread.start();
    }

    //METODO QUE GENERA UN JUEGO NUEVO
    private void newGame(){
        //LIMPIAMOS E INDICAMOS LAS FILAS Y COLUMNAS A LOS GRID LAYOUT
        gridLayoutLetras.removeAllViewsInLayout();
        gridLayoutLetras.setColumnCount(NUM_COL_LET);
        gridLayoutLetras.setRowCount(NUM_FIL_LET);
        gridLayoutTeclado.removeAllViewsInLayout();
        gridLayoutTeclado.setColumnCount(NUM_COL_KEY);
        gridLayoutTeclado.setRowCount(NUM_FIL_KEY);

        //RESETEAMOS LAS VARIABLES
        posLetter=0;
        completeLetterOfWord=0;
        wordGame="";
        charsGame.clear();
        charPlayer.clear();
        wordPlayer="";
        idKeyWordPlayer.clear();
        comprobaciones=0;
        blockKeyboard=false;

        //CREAMOS EL PANEL Y EL TECLADO
        createPanel();
        createKeyboard();

        //GENERAMOS LA PALABRA ALEATORIA
        try {
            getRandomWord();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}