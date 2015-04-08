package com.manyasoft.mailclient;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.Toast;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
	
	public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table message(contacto text, texto text, fecha text, estado text," +
        			" categoria text)");
        db.execSQL("create table dicSpam(word text, cant text)");
        db.execSQL("create table dicPrin(word text, cant text)");
        db.execSQL("create table total(word text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        db.execSQL("drop table if exists message");
        db.execSQL("drop table if exists dicSpam");
        db.execSQL("drop table if exists dicSrin");
        db.execSQL("drop table if exists total");
        db.execSQL("create table message(contacto text, texto text, fecha text, estado text," +
    			" categoria text)");
        db.execSQL("create table dicSpam(word text, cant text)");
        db.execSQL("create table dicPrin(word text, cant text)");
        db.execSQL("create table total(word text)");
        

        
    }
    
    public Cursor getSelect(String query){
    	SQLiteDatabase bd =this.getWritableDatabase();
    	Cursor filas = bd.rawQuery(query, null);
    	//bd.close();
    	return filas;
    }
    
    public void insertInTable(String contacto,String mensaje,String cat){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	
    	ContentValues registroMessage = new ContentValues();
		registroMessage.put("contacto", contacto);
		registroMessage.put("texto", mensaje);
		registroMessage.put("fecha", getDate());
		registroMessage.put("estado", "n");
		registroMessage.put("categoria", cat);
    	bd.insert("message", null, registroMessage);
    	//bd.close();	
    }
    public String getDate(){
    	SimpleDateFormat cod_sf = new SimpleDateFormat("yyyyMMddhhmmss");
		String fecha = cod_sf.format(new Date());
    	return fecha;
    }
    
    public void executeSQL(String query){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	bd.execSQL(query);
    }
    
    public boolean checkTablaMisdatos(){
        SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor filas = bd.rawQuery("select * from message", null); 
    	if(filas.moveToFirst()){
    		return false;//hay datos
    	}
    	//bd.close();
    	return true;
    }
    
    
    //CUENTA EL NUMERO TOTAL DE PALABRAS
    public int counttotaltable(){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor mCount= bd.rawQuery("select count(*) from total", null);
    	mCount.moveToFirst();
    	int count= Integer.parseInt(mCount.getString(0));
    	return count;
    }
    public int countwordspam(){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor mCount= bd.rawQuery("select count(*) from dicSpam", null);
    	mCount.moveToFirst();
    	int count= Integer.parseInt(mCount.getString(0));
    	return count;
    }
    public int countwordprin(){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor mCount= bd.rawQuery("select count(*) from dicPrin", null);
    	mCount.moveToFirst();
    	int count= Integer.parseInt(mCount.getString(0));
    	return count;
    }
    
    
    
    //NUMERO DE MENSAJES
    public int counttotalspam(){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor mCount= bd.rawQuery("select count(texto) from message where categoria='s'", null);
    	mCount.moveToFirst();
    	int count= Integer.parseInt(mCount.getString(0));
    	return count;
    }
    
    public int counttotalprin(){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor mCount= bd.rawQuery("select count(texto) from message where categoria='p'", null);
    	mCount.moveToFirst();
    	int count= Integer.parseInt(mCount.getString(0));
    	return count;
    }
    
    
    
    
    //NUMERO DE VECES QUE UNA PALABRA SE REPITE
    public int searchrepewordspam(String cadena){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor filas = bd.rawQuery("select * from dicSpam where word='"+cadena+"'", null); 
    	if(filas.moveToFirst()){
    		return Integer.parseInt(filas.getString(1));//hay datos
    	}
    	//bd.close();
    	return 0;
    }
    
    public int searchrepewordprin(String cadena){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor filas = bd.rawQuery("select * from dicPrin where word='"+cadena+"'", null); 
    	if(filas.moveToFirst()){
    		return Integer.parseInt(filas.getString(1));//hay datos
    	}
    	//bd.close();
    	return 0;
    }
    
    
    //insertar mas palabras en los diccionarios
    
    public void insertdicprin(String texto){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor filas = bd.rawQuery("select * from dicPrin where word='"+texto+"'", null); 
    	if(filas.moveToFirst()){
    		executeSQL("update dicPrin set cant='"+(String.valueOf(1+Integer.parseInt(filas.getString(1))) ) +"' where word='"+texto+"'");
    	}else{
    		executeSQL("insert INTO dicPrin VALUES ('"+texto+"','"+String.valueOf(1)+"')");
    	}
    }
    
    public void insertdicspam(String texto){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor filas = bd.rawQuery("select * from dicSpam where word='"+texto+"'", null); 
    	if(filas.moveToFirst()){
    		executeSQL("update dicSpam set cant='"+(String.valueOf(1+Integer.parseInt(filas.getString(1))) ) +"' where word='"+texto+"'");
    	}else{
    		executeSQL("insert INTO dicSpam VALUES ('"+texto+"','"+String.valueOf(1)+"')");
    	}
    }
    
    public void insertdictotal(String texto){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	Cursor filas = bd.rawQuery("select * from total where word='"+texto+"'", null); 
    	if(filas.moveToFirst()){
    	
    	}else{
    		executeSQL("insert INTO total VALUES ('"+texto+"')");
    	}
    }
    
    
    
    
    
    public void defaultAnalizerSpam(String cadena){
    	String[] texto=cadena.toLowerCase().replace(",", "").replace(":", "").replace(".", "").replace(";", "").replace("_", "").replace("-", "").split("\\s+");
        SQLiteDatabase bd = this.getWritableDatabase();
        for(int e=0;e<texto.length;e++){
	        Cursor filas = bd.rawQuery("select * from dicSpam where word='"+texto[e]+"'", null); 
	    	if(filas.moveToFirst()){
	    		executeSQL("update dicSpam set cant='"+(String.valueOf(1+Integer.parseInt(filas.getString(1))) ) +"' where word='"+texto[e]+"'");
	    	}else{
	    		
	    		executeSQL("insert INTO dicSpam VALUES ('"+texto[e]+"','"+String.valueOf(1)+"')");
	    	}
        }
    	
    }
    
    public void defaultAnalizerPrin(String cadena){
    	String[] texto=cadena.toLowerCase().replace(",", "").replace(":", "").replace(".", "").replace(";", "").replace("_", "").replace("-", "").split("\\s+");
        SQLiteDatabase bd = this.getWritableDatabase();
        for(int e=0;e<texto.length;e++){
	        Cursor filas = bd.rawQuery("select * from dicPrin where word='"+texto[e]+"'", null); 
	    	if(filas.moveToFirst()){
	    		executeSQL("update dicPrin set cant='"+(String.valueOf(1+Integer.parseInt(filas.getString(1))) ) +"' where word='"+texto[e]+"'");
	    	}else{
	    		
	    		executeSQL("insert INTO dicPrin VALUES ('"+texto[e]+"','"+String.valueOf(1)+"')");
	    	}
        }
    	
    }
    
    public void dictionaryTotal(){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	bd.execSQL("INSERT INTO total SELECT word FROM dicPrin ");
    	try {
			bd.execSQL("INSERT INTO total SELECT word FROM dicSpam WHERE word NOT IN (SELECT word FROM dicPrin)");
		} catch (SQLException e) {
			Toast t = Toast.makeText( null, "error "+e.getMessage(), Toast.LENGTH_LONG);
			t.show();
		}
    		  
    	
    }
    
    public void insertProducts(){
    	SQLiteDatabase bd = this.getWritableDatabase();
    	//bd.execSQL("INSERT INTO message VALUES ('no-reply@redhat.com','Este correo electrónico se envía para validar la dirección de correo electrónico que ha suministrado para su ingreso a Red Hat. Su ingreso Red Hat, junto con una suscripción Red Hat activa, le brindan acceso a las capacidades de administración de sistema a través de la Red Red Hat . Para asegurar la seguridad de la información de la cuenta asociada con su ingreso Red Hat, por favor tómese un momento para hacer clic en el vínculo a continuación y verificar que tenemos la dirección correcta de correo electrónico. Si no confirma su dirección de correo electrónico, su ingreso a Red Hat será deshabilitado . Para confirmar su dirección de correo electrónico, por favor visite la siguiente URL:','31 de agosto','n','p')");
    	//bd.execSQL("INSERT INTO message VALUES ('matricula@ucvlima.edu.pe','ficha de matricula','31 de agosto','n','p')");
    	//bd.execSQL("INSERT INTO message VALUES ('chr_omar1107@hotmail.com','proyecto de gestion de datos , turno mañana UCV Sistemas','31 de agosto','n','p')");
    	bd.execSQL("INSERT INTO message VALUES ('chr_omar1107@hotmail.com','No se olviden de entregarme la ficha completa con los datos de sus empresas elegidas en el curso de plataformas tecnológicas, impreso En el campus está el formato, en donde hay 2 hojas en si, a mi solo me tienen que dar la primera hoja donde dice nombre de la empresa, curso, integrantes.. toda esa nota, la segunda hoja lo suben al campus completa ya que esa sería su primer avance del informe','31 de agosto','n','p')");
    	defaultAnalizerPrin("chr_omar1107@hotmail.com No se olviden de entregarme la ficha completa con los datos de sus empresas elegidas en el curso de plataformas tecnológicas, impreso En el campus está el formato, en donde hay 2 hojas en si, a mi solo me tienen que dar la primera hoja donde dice nombre de la empresa, curso, integrantes.. toda esa nota, la segunda hoja lo suben al campus completa ya que esa sería su primer avance del informe");
    	bd.execSQL("INSERT INTO message VALUES ('PlayStation@eu.playstationmail.net',' Aviso importante: próximos cambios en tu cuenta PSN','31 de agosto','n','p')");
    	defaultAnalizerPrin("PlayStation@eu.playstationmail.net Aviso importante: próximos cambios en tu cuenta PSN");
    	bd.execSQL("INSERT INTO message VALUES ('rchavez@adiestra.pe','Estimado participante: Para comunicarle que la clase del curso de LINUX correspondiente a este domingo 01 de setiembre será suspendida. Las clases se reanudan de manera habitual el domingo 08. Extendemos las disculpas del caso y esperamos su gentil comprensión. Cordialmente,me despido','31 de agosto','n','p')");
    	defaultAnalizerPrin("rchavez@adiestra.pe Estimado participante: Para comunicarle que la clase del curso de LINUX correspondiente a este domingo 01 de setiembre será suspendida. Las clases se reanudan de manera habitual el domingo 08. Extendemos las disculpas del caso y esperamos su gentil comprensión. Cordialmente,me despido");
    	bd.execSQL("INSERT INTO message VALUES ('tutoriaschumpitaz_ucv@hotmail.com','Apreciados estudiantes: Siendo al TUTORIA un servicio de acompañamiento al estudiante por lo que considera al estudiante como eje importante  de la universidad queremos garantizar su formación académica ,personal y profesional por lo que sírvase llenar el documento adjunto y devolverlo por este medio  lo mas pronto posible  para canalizar sus observaciones y encontrar soluciones asertadas.','31 de agosto','n','p')");
    	defaultAnalizerPrin("tutoriaschumpitaz_ucv@hotmail.com Apreciados estudiantes: Siendo al TUTORIA un servicio de acompañamiento al estudiante por lo que considera al estudiante como eje importante  de la universidad queremos garantizar su formación académica ,personal y profesional por lo que sírvase llenar el documento adjunto y devolverlo por este medio  lo mas pronto posible  para canalizar sus observaciones y encontrar soluciones asertadas.");
    	bd.execSQL("INSERT INTO message VALUES ('tutoriaschumpitaz_ucv@hotmail.com','Apreciados estudiantes: Llenar los siguientes datos URGENTE. Los necesitan en TUTORIA.','31 de agosto','n','p')");
    	defaultAnalizerPrin("tutoriaschumpitaz_ucv@hotmail.com Apreciados estudiantes: Llenar los siguientes datos URGENTE. Los necesitan en TUTORIA.");
    	bd.execSQL("INSERT INTO message VALUES ('clon_love_02@hotmail.com','trabajo tecnologia de informacion','31 de agosto','n','p')");
    	defaultAnalizerPrin("clon_love_02@hotmail.com trabajo tecnologia de informacion");
    	
    	
    	bd.execSQL("INSERT INTO message VALUES ('noticias@winkalmail.com','tu dosis de diversión diaria -divertido -teen -inspirador -imposible nada -tremendo golll -ver más fotos','29 de agosto','n','s')");
    	defaultAnalizerSpam("noticias@winkalmail.com tu dosis de diversión diaria -divertido -teen -inspirador -imposible nada -tremendo golll -ver más fotos");
    	bd.execSQL("INSERT INTO message VALUES ('noticias@winkalmail.com','hola, ruega porque hoy es viernes no te inviten a beber','29 de agosto','n','s')");
    	defaultAnalizerSpam("noticias@winkalmail.com hola, ruega porque hoy es viernes no te inviten a beber");
    	bd.execSQL("INSERT INTO message VALUES ('alcampo@customertrademovils.com','valida tu tarjeta regalo alcampo. responde a la encuesta y gana dinero válido en los supermercados de tu preferencia','29 de agosto','n','s')");
    	defaultAnalizerSpam("alcampo@customertrademovils.com valida tu tarjeta regalo alcampo. responde a la encuesta y gana dinero válido en los supermercados de tu preferencia");
    	bd.execSQL("INSERT INTO message VALUES ('info@sea.ancor.es','disfruta de un portatil totalmente regalada, informate ya aquí','29 de agosto','n','s')");
    	defaultAnalizerSpam("info@sea.ancor.es disfruta de un portatil totalmente regalada, informate ya aquí");
    	bd.execSQL("INSERT INTO message VALUES ('info@iaho.ancor.es','contrata ono y podras llevarte un televisor sony 3D','29 de agosto','n','s')");
    	defaultAnalizerSpam("info@iaho.ancor.es contrata ono y podras llevarte un televisor sony 3D");
    	bd.execSQL("INSERT INTO message VALUES ('info@a.tacticaloffermaker.info','curso de programacion android, aprovecha esta oportunidad y cambia tu vida','29 de agosto','n','s')");
    	defaultAnalizerSpam("info@a.tacticaloffermaker.info curso de programacion android, aprovecha esta oportunidad y cambia tu vida");
    	bd.execSQL("INSERT INTO message VALUES ('win@botoffer.com','usted es el ganador del sorteo de un auto. ingrese susdatos','29 de agosto','n','s')");
    	defaultAnalizerSpam("win@botoffer.com usted es el ganador del sorteo de un auto. ingrese susdatos");
    	bd.execSQL("INSERT INTO message VALUES ('playstation@es.playstationmail.net','gana el paquete psvita','29 de agosto','n','s')");
    	defaultAnalizerSpam("playstation@es.playstationmail.net gana el paquete psvita");
    	bd.execSQL("INSERT INTO message VALUES ('Facebook','saluda a Juan mex por su cuumpleaños','15 de agosto','n','s')");
    	defaultAnalizerSpam("Facebook saluda a Juan mex por su cuumpleaños");
    	bd.execSQL("INSERT INTO message VALUES ('Facebook','estas son las personas que quizas conoscas, agrégalas','10 de jul','n','s')");
    	defaultAnalizerSpam("Facebook estas son las personas que quizas conoscas, agrégalas");
    	bd.execSQL("INSERT INTO message VALUES ('Twitter','Arstechnica te está siguiendo','18 de julio','n','s')");
    	defaultAnalizerSpam("Twitter Arstechnica te está siguiendo");
    	
    	dictionaryTotal();
    }
}
