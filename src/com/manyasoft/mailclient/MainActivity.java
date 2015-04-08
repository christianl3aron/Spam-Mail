package com.manyasoft.mailclient;


import java.util.ArrayList;






import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

		public Button btnprin;
		public Button btnspam;
		
		private AdminSQLiteOpenHelper admin_cl;
		public ArrayList<message> Array_mess = new ArrayList<message>();
		public ArrayList<message> Array_prin = new ArrayList<message>();
		public ArrayList<message> Array_temp = new ArrayList<message>();
		public ArrayList<String> dictio_spam = new ArrayList<String>();
		public ArrayList<Integer> spam_flag_cant = new ArrayList<Integer>();
		public ArrayList<String> dictio_prin = new ArrayList<String>();
		public ArrayList<Integer> prin_flag_cant = new ArrayList<Integer>();
		public String[] texto;
		
		private ListViewMessage_Adapter adapter;
		private Tools tools;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        admin_cl=new AdminSQLiteOpenHelper(this,"administracion", null, 1);
        tools = new Tools(this.getApplicationContext());
        if(admin_cl.checkTablaMisdatos()) admin_cl.insertProducts();
        
        btnprin=(Button) findViewById(R.id.btn_prinipal);
        btnspam=(Button) findViewById(R.id.btn_spam);
        btnprin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				fillmess("p");
			}
        });
        btnspam.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				fillmess("s");
			}
        });
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.new_mess:{
        	buidRegisterDialog(null,false);
        }break;
        }
		return true;
	}
    
    private void inicializarListView(final ArrayList<message> Array_intern) {	
		ListView lista = (ListView) findViewById(R.id.lista);
 		adapter = new ListViewMessage_Adapter(this, Array_intern);
 		lista.setAdapter(adapter);
 		lista.setOnItemClickListener(new OnItemClickListener() {  
            public void onItemClick(AdapterView<?> parent, View view, final int position,long id) {
					buidRegisterDialog(Array_intern.get(position),true);
					if(Array_intern.get(position).getEstado().equals("n")){
						admin_cl.executeSQL("update message set estado='l' where texto='"+Array_intern.get(position).getTexto()+"'");
					}
					fillmess(Array_intern.get(position).getCategoria());
				        	                                   
              }});
	}
    
    public void fillmess(String cat){
		Array_mess.clear();
        try {
	        	Cursor filas = admin_cl.getSelect("select * from message where categoria='"+cat+"'");
	        	if(filas.moveToFirst()){
	            	 do {
	            		 message mess_temp = new message(); 
	            		 mess_temp.setContacto(filas.getString(0));
	            		 mess_temp.setTexto(filas.getString(1));
	            		 mess_temp.setFecha(filas.getString(2));
	            		 mess_temp.setEstado(filas.getString(3));
	            		 mess_temp.setCategoria(filas.getString(4));
            	         Array_mess.add(mess_temp);
	                 } while (filas.moveToNext());	            	 	            		            	
	        	} inicializarListView(Array_mess);	
		} catch (Exception e) {Toast.makeText(this, "fallo en SELECT !!", Toast.LENGTH_SHORT).show();} 
    }
    
    public int gettotalcat(String cat){
		Array_mess.clear();
        try {
	        	Cursor filas = admin_cl.getSelect("select * from message where categoria='"+cat+"'");
	        	if(filas.moveToFirst()){
	            	 do {
	            		 message mess_temp = new message(); 
	            		 mess_temp.setContacto(filas.getString(0));
	            		 mess_temp.setTexto(filas.getString(1));
	            		 mess_temp.setFecha(filas.getString(2));
	            		 mess_temp.setEstado(filas.getString(3));
	            		 mess_temp.setCategoria(filas.getString(4));
            	         Array_mess.add(mess_temp);
	                 } while (filas.moveToNext());	            	 	            		            	
	        	} 
		} catch (Exception e) {Toast.makeText(this, "fallo en SELECT !!", Toast.LENGTH_SHORT).show();} 
        return Array_mess.size();
    }
    
    private void buidRegisterDialog(final message mess,boolean hide){
    	
    	final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_mail);
		dialog.setCancelable(true);
		
		final EditText txtcontacto=(EditText) dialog.findViewById(R.id.contacto);
		final EditText txtmessage=(EditText) dialog.findViewById(R.id.mensaje);
		Button btncreate=(Button) dialog.findViewById(R.id.button1);
		if(hide){
			btncreate.setVisibility(View.INVISIBLE);
			txtcontacto.setText(mess.getContacto());
			txtmessage.setText(mess.getTexto());
		}
		else{
		btncreate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int total_spam=admin_cl.counttotalspam();
				int total_prin=admin_cl.counttotalprin();
				int total_mess= total_spam+total_prin;
				double prob_spam= (double) total_spam/total_mess;
				double prob_prin= (double) total_prin/total_mess;
				int total_words=admin_cl.counttotaltable();
				int total_words_spam=admin_cl.countwordspam();
				int total_words_prin=admin_cl.countwordprin();
				double prob_spam_total=prob_spam;
				double prob_prin_total=prob_prin;
				
				/*try {
					tools.showShortMessage("total SPAM: "+ String.valueOf(total_spam));
					tools.showShortMessage("total PRIN: "+ String.valueOf(total_prin));
					tools.showShortMessage("mensaje totales: "+String.valueOf(total_mess));
					tools.showShortMessage("proba SPAM: "+String.valueOf(prob_spam));
					tools.showShortMessage("prob PRIN: "+String.valueOf(prob_prin));
					tools.showShortMessage("palabras totales"+String.valueOf(total_words));
					tools.showShortMessage("palabras totalse SPAM"+String.valueOf(total_words_spam));
					tools.showShortMessage("palabras totales PRIN"+String.valueOf(total_words_prin));
					tools.showShortMessage("proba SPAM sumando: "+String.valueOf(prob_spam_total));
				} catch (Exception e) {
					tools.showShortMessage("Error en calculo:  "+e.getMessage());
				}*/
				
				String[] texto=(txtcontacto.getText().toString()+" "+txtmessage.getText().toString()).toLowerCase().replace(",", "").replace(":", "").replace(".", "").replace(";", "").replace("_", "").replace("-", "").split("\\s+");
				for(int i=0;i<texto.length;i++){
					double probw= (double)(admin_cl.searchrepewordspam(texto[i])+1)/(total_words_spam+total_words);	
					prob_spam_total=prob_spam_total*probw;
				}
				
				tools.showLongMessage("probabilida final SPAM "+String.valueOf(prob_spam_total));
				
				
				for(int i=0;i<texto.length;i++){
					double probw= (double)(admin_cl.searchrepewordprin(texto[i])+1)/(total_words_prin+total_words);	
					prob_prin_total=prob_prin_total*probw;
				}
				
				tools.showLongMessage("probabilida final PRIM "+String.valueOf(prob_prin_total));
				String cat;
				
				if(prob_prin_total>=prob_spam_total){
					tools.showLongMessage("clase PRINCIPAL");
					cat="p";
					for(int i=0;i<texto.length;i++){
						admin_cl.insertdicprin(texto[i]);
						admin_cl.insertdictotal(texto[i]);
					}
				}else{
					tools.showLongMessage("clase SPAM");
					cat="s";
					for(int i=0;i<texto.length;i++){
						admin_cl.insertdicspam(texto[i]);
						admin_cl.insertdictotal(texto[i]);
					}
				}
				
				
				try {
					
					admin_cl.insertInTable(txtcontacto.getText().toString().toLowerCase(),txtmessage.getText().toString().toLowerCase(),cat);
					tools.showShortMessage("mensaje GUARDADO!");
				} catch (Exception e) {
					tools.showShortMessage("Error de almacenamiento de mensaje: "+e.getMessage());
				}
				
				
				dialog.dismiss();
				//toma el valor del ultimo creado
				fillmess(cat);
				//no se ´puede porque cuando esntra aca , el mess es null
				//fillmess(mess.getCategoria());			
			}
		});}
	
		dialog.show();
    }

}
