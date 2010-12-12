package com.aconsapart.redtxtads;



import net.sf.andhsli.hotspotlogin.SimpleCrypto;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Decrypt extends Activity{
	static EditText PasswordToDecrypt;
	static EditText MessageToDecrypt;
	static Button DecryptButton;

	public String decryptedMessage;
	static final int DIALOG_MESSAGE_ID = 0;
	protected static final int DIALOG_MESSAGE_ID_2 = 1;
	/**
	 * @param args
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decrypt);
		PasswordToDecrypt = (EditText) findViewById(R.id.PasswordToDecrypt);
		MessageToDecrypt = (EditText) findViewById(R.id.MessageToDecrypt);
		DecryptButton = (Button) findViewById(R.id.DecryptButton);
		DecryptButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{

				
				if(PasswordToDecrypt.getText().toString().equals("") || MessageToDecrypt.getText().toString().equals("")){
					showDialog(DIALOG_MESSAGE_ID_2);
				}
				else{

				try {
					decryptedMessage = 	SimpleCrypto.decrypt(PasswordToDecrypt.getText().toString(), MessageToDecrypt.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				showDialog(DIALOG_MESSAGE_ID);
				}
			}
		});
	}
	@Override
	protected AlertDialog onCreateDialog(int id){
		AlertDialog dialog = null;
		switch(id){
		case DIALOG_MESSAGE_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Decrypted Message:" + decryptedMessage);
			dialog = builder.create();
			break;
		case DIALOG_MESSAGE_ID_2:
			AlertDialog.Builder error = new AlertDialog.Builder(this);
			error.setTitle("Error");
			error.setMessage("Please check your input and try again.");
			dialog = error.create();
			break;	
		}
		return dialog;
	}
@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.about:
			startActivity(new Intent(this, About.class));
			Log.i("Start", "Started about activity");
			return true;
		case R.id.encrypt:
			startActivity(new Intent(this, Main.class));
			return true;

		case R.id.decrypt:
			startActivity(new Intent(this, Decrypt.class));
			return true;
		}
		
		return false;
	}

}
