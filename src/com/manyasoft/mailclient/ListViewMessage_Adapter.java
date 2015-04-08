package com.manyasoft.mailclient;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewMessage_Adapter extends ArrayAdapter<Object> {

	Context context;
	private ArrayList<message> message;
	
	public ListViewMessage_Adapter(Context context, ArrayList<message> message) {
		super(context, R.layout.item_mail_list);
		this.context = context;
		this.message = message;
	}
	
	public int getCount() {
		return message.size();
	}
	
	
	private static class PlaceHolder {

		TextView contacto;
		TextView texto;
		TextView fecha;
		LinearLayout item;

		public static PlaceHolder generate(View convertView) {
			PlaceHolder placeHolder = new PlaceHolder();
			placeHolder.contacto = (TextView) convertView.findViewById(R.id.txt_contacto);
			placeHolder.texto = (TextView) convertView.findViewById(R.id.txt_resumen);
			placeHolder.fecha = (TextView) convertView.findViewById(R.id.txt_fecha);
			placeHolder.item=(LinearLayout) convertView.findViewById(R.id.item);
			return placeHolder;
		}

	}
	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		PlaceHolder placeHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_mail_list, null);
			placeHolder = PlaceHolder.generate(convertView);
			convertView.setTag(placeHolder);
		} else {
			placeHolder = (PlaceHolder) convertView.getTag();
		}
		placeHolder.contacto.setText(titleize(message.get(position).getContacto()));
		placeHolder.texto.setText(titleize(message.get(position).getTexto()));
		placeHolder.fecha.setText(titleize(message.get(position).getFecha()));
		if(message.get(position).getEstado().equals("n")) placeHolder.item.setBackgroundColor(Color.parseColor("#cecece"));
		else placeHolder.item.setBackgroundColor(Color.parseColor("#ffffff"));
		
		return (convertView);
	}
	
	private String titleize(String source){
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
