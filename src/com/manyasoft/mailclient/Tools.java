package com.manyasoft.mailclient;

import android.content.Context;
import android.widget.Toast;

public class Tools {
	
	private Context contexto;
	
	public Tools(Context contexto){
		this.contexto = contexto;
	}
	
	public Context getContexto(){
		return this.contexto;
	}
	
	// Metodo publico que muestra un mensaje corto.
	public void showShortMessage(String mensaje){
		Toast.makeText(getContexto(), mensaje, Toast.LENGTH_SHORT).show();
	}
	
	// Metodo publico que muestra un mensaje largo.
	public void showLongMessage(String mensaje){
		Toast.makeText(getContexto(), mensaje, Toast.LENGTH_LONG).show();
	}
	
	//Letras capitales en mayusculas.
	public String titleize(String source){
        boolean cap = true;
        char[]  out = source.toCharArray();
        int i, len = source.length();
        for(i=0; i<len; i++){
            if(Character.isWhitespace(out[i])){
                cap = true;
                continue;
            }
            if(cap){
                out[i] = Character.toUpperCase(out[i]);
                cap = false;
            }
        }
        return new String(out);
    }
}

