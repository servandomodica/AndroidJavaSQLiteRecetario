package com.example.recetas;

import java.io.File;
import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainActivity extends Activity {
	EditText et1, et2;
	ListView lv1;
	ImageView iv1;
	TextView tv1, tv2;
	ViewPager vp1;
	RelativeLayout pagina1;
	RelativeLayout pagina2;
	RelativeLayout pagina3;
	ArrayList<String>lista1;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		vp1 = (ViewPager) findViewById(R.id.view);
		vp1.setAdapter(new MainPageAdapter());
		
		//El error fue mio al no hacer ejercicios con el control ViewPager
		//El ViewPager esta implementado de tal forma que solo se carguen las páginas que se van visualizando, por lo que
		//las variables pagina1, pagina2 y pagina3 no estan cargadas en memoria.
		//Posiblemente debería haber implementado el ejercicio para que sea mas facil de desarrollar y no mas eficiente
		//como esta implementado:
		/*pagina1 = (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.pagina1, null);
 		 pagina2 = (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.pagina2, null);
		 pagina3 = (RelativeLayout) LayoutInflater.from(
				MainActivity.this).inflate(R.layout.pagina3, null);
		 Esto nos facilitaria cargar los controles visuales en cualquier momento sin tener en cuenta si alguna de las paginas no se haya cargado en memoria.		
				*/
		
		}
	
	
	public void guardar (View v) {
		et1 = (EditText) pagina1.findViewById(R.id.editText1);
		et2 = (EditText) pagina1.findViewById(R.id.editText2);
		
		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "base1", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		
		ContentValues registro = new ContentValues();		
		registro.put("titulo", et1.getText().toString());
		registro.put("descripcion", et2.getText().toString());
		
		bd.insert("recetas", null, registro);
		bd.close();
		
		et1.setText("");
		et2.setText("");
		Toast.makeText(this, "Se almaceno la receta correctamente", Toast.LENGTH_LONG).show();
		cargarRecetas();
	}
	
	
	public void borrar(View v) {
		et1 = (EditText) pagina1.findViewById(R.id.editText1);
		et2 = (EditText) pagina1.findViewById(R.id.editText2);
		
		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "base1", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();

		Cursor registro = bd.rawQuery("select descripcion from recetas where titulo='" + et1.getText().toString() + "'", null);
		
		if (registro.moveToFirst()) {
			String eliminar = "DELETE FROM recetas WHERE titulo='" + et1.getText().toString() + "'";
			bd.execSQL(eliminar);		    
			bd.close();
			    
			Toast.makeText(this, "Se elimino la receta correctamente", Toast.LENGTH_LONG).show();
			et1.setText("");
			et2.setText("");
		} else {
			Toast.makeText(this, "No existe una receta con ese titulo", Toast.LENGTH_LONG).show();
			bd.close();
		}
		//cargarRecetas();
	}
	
	
	public void recuperar (View v) {
		et1 = (EditText) pagina1.findViewById(R.id.editText1);
		et2 = (EditText) pagina1.findViewById(R.id.editText2);
		
		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "base1", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		
		Cursor registro = bd.rawQuery("select descripcion from recetas where titulo='" + et1.getText().toString() + "'", null);
		
		if (registro.moveToFirst()) {
			et2.setText(registro.getString(0));
			Toast.makeText(this, "Se recupero la receta correctamente", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "No existe una receta con ese titulo", Toast.LENGTH_LONG).show();
			bd.close();
		}		
	}
	
	
	public void tomarFoto (View v) {
		et1 = (EditText) pagina1.findViewById(R.id.editText1);
		et2 = (EditText) pagina1.findViewById(R.id.editText2);
		
		if (et1.getText().toString().length()==0) {
			Toast.makeText(this, "Debe ingresar un nombre de receta", Toast.LENGTH_LONG).show();
		} else {
	    	Intent intento1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	
	    	File foto=new File(getExternalFilesDir(null), et1.getText().toString());
	    	
	    	intento1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto));
	    	startActivity(intento1);	    	
		}
	}
	
	
	private void cargarRecetas() {
		lv1 = (ListView) pagina2.findViewById(R.id.listView1);	
		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "base1", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		
		lista1=new ArrayList<String>(); //Debe ser global para luego recuperar en el onItemClick el nombre de la receta y pasar a mostrar  una receta en particular
		
		Cursor registro = bd.rawQuery("select titulo,descripcion from recetas", null); //order by titulo asc
			
		while (registro.moveToNext())
		{
			lista1.add(registro.getString(0));
		}
		ArrayAdapter<String>adaptador1=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista1);
		
		lv1.setAdapter(adaptador1);
		
		lv1.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				vp1.setCurrentItem(2); //Cambio a la tercera pagina
		        mostrarUnaReceta(lista1.get(arg2));	
			}			
		});
	}

	
	
	private void mostrarUnaReceta(String nombre) {
		//Muestra la receta seleccionada del ListView (borre los otros datos del ListView para que sea mas facil resolver este punto)
		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "base1", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		
		Cursor registro = bd.rawQuery("select titulo,descripcion from recetas where titulo='" + nombre + "'", null);
		
		if (registro.moveToFirst()) {
			 iv1 = (ImageView) pagina3.findViewById(R.id.imageView1);
		     tv1 = (TextView) pagina3.findViewById(R.id.textView1);
		     tv2 = (TextView) pagina3.findViewById(R.id.textView2);
		     tv1.setText("Receta:"+registro.getString(0));
		     tv2.setText("Descripcion:\n\n"+registro.getString(1));
		     
		     Bitmap bitmap1=BitmapFactory.decodeFile(getExternalFilesDir(null)+"/"+nombre);
		     iv1.setImageBitmap(bitmap1);		     
		}				
		
	}
	
	class MainPageAdapter extends PagerAdapter {


		public int getCount() {
			return 3;
		}

		public Object instantiateItem(ViewGroup collection, int position) {
			View page = null;
			switch (position) {
			case 0:
				if (pagina1 == null) {
					pagina1 = (RelativeLayout) LayoutInflater.from(
							MainActivity.this).inflate(R.layout.pagina1, null);  //Con esto se lee el archivo XML de la interfaz visual

				}
				page = pagina1;
				break;
			case 1:
				if (pagina2 == null) {
					pagina2 = (RelativeLayout) LayoutInflater.from(
							MainActivity.this).inflate(R.layout.pagina2, null);

				}
				cargarRecetas();  //Recien cuando el operador cambie a la segunda pagina procedemos a cargar las recetas en el ListView
				page = pagina2;
				break;
			case 2:
				if (pagina3 == null) {
					pagina3 = (RelativeLayout) LayoutInflater.from(
							MainActivity.this).inflate(R.layout.pagina3, null);

				}				
				page = pagina3;
				break;
			}
			((ViewPager) collection).addView(page, 0);
			return page;
		}

		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}
	}

}
