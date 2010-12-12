package com.aconsapart.redtxtads;

import com.admob.android.ads.AdManager;

import net.sf.andhsli.hotspotlogin.SimpleCrypto;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;



public class Main extends Activity
{
 	EditText message; //Edit text that contains the message to be encrypted
	EditText password; //Edit text that contains the password.
	EditText Contact;
	Button send_button;
	static PendingIntent sentIntent;
	String contact;
 //   static EditText contactInfo;
	public String encryptedMessage;
	private static final String SMS_RECEIVER = "SmsReceiver";
	private static final int DIALOG_MESSAGE_ID_1 = 0;

    // Request code for the contact picker activity
    private static final int PICK_CONTACT_REQUEST = 1;
	private static final int MANUAL_ENTRY_ID_2 = 1;
	private static final int DIALOG_MESSAGE_ID_3 = 2;
	

    /**
     * An SDK-specific instance of {@link ContactAccessor}.  The activity does not need
     * to know what SDK it is running in: all idiosyncrasies of different SDKs are
     * encapsulated in the implementations of the ContactAccessor class.
     */
    private final ContactAccessor mContactAccessor = ContactAccessor.getInstance();
    /**
     * Click handler for the Pick Contact button.  Invokes a contact picker activity.
     * The specific intent used to bring up that activity differs between versions
     * of the SDK, which is why we delegate the creation of the intent to ContactAccessor.
     */
    protected void pickContact() {
        startActivityForResult(mContactAccessor.getPickContactIntent(), PICK_CONTACT_REQUEST);
    }

    /**
     * Invoked when the contact picker activity is finished. The {@code contactUri} parameter
     * will contain a reference to the contact selected by the user. We will treat it as
     * an opaque URI and allow the SDK-specific ContactAccessor to handle the URI accordingly.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            loadContactInfo(data.getData());
        }
    }

    /**
     * Load contact information on a background thread.
     */
    private void loadContactInfo(Uri contactUri) {

        /*
         * We should always run database queries on a background thread. The database may be
         * locked by some process for a long time.  If we locked up the UI thread while waiting
         * for the query to come back, we might get an "Application Not Responding" dialog.
         */
        AsyncTask<Uri, Void, ContactInfo> task = new AsyncTask<Uri, Void, ContactInfo>() {

            @Override
            protected ContactInfo doInBackground(Uri... uris) {
                return mContactAccessor.loadContact(getContentResolver(), uris[0]);
            }
            @Override
            protected void onPostExecute(ContactInfo result) {
                bindView(result);
            }
        };

        task.execute(contactUri);
    }
    /**
     * Displays contact information: name and phone number.
     */
    protected void bindView(ContactInfo contactInfo) {
       
       contact = contactInfo.getPhoneNumber().toString();
    }
   
        @Override
 	public void onCreate(Bundle savedInstanceState)
	{
        	//Ad code
        	/*
        	AdManager.setTestDevices( new String[] { AdManager.TEST_EMULATOR,	// Android emulator 
        			"E83D20734F72FB3108F104ABC0FFC738", // My T-Mobile G1 Test Phone
        	} );
        	*/
        	/*
        	 * message, password,contat are the plaintext versions of the messages
        	 * encrypted text
        	 */
		super.onCreate(savedInstanceState);
		sentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Main.class), 0); //Registers the intent to send the message.
		setContentView(R.layout.main); //Sets the layout to the main.xml layout
		message = (EditText) findViewById(R.id.Message); //Edit text for the message
	//	contactInfo = (EditText) findViewById(R.id.Number); //Edit text for the contact info
	//	contactInfo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		password = (EditText) findViewById(R.id.Password);
	    // Install a click handler on the Pick Contact button
	    Button pickContact = (Button)findViewById(R.id.pick_contact_button);
		pickContact.setOnClickListener(new OnClickListener() {

	            public void onClick(View v) {
	                pickContact();
	            }
	        });
		send_button = (Button) findViewById(R.id.Send);
		Button manualEntry = (Button)findViewById(R.id.manual_entry_button);
		manualEntry.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				showDialog(MANUAL_ENTRY_ID_2);
			}
		});
		send_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				

				//Puts an encrypted message given a plaintext message and a password.
				//validateAndSendSms(contactInfo.getText().toString(), encryptedMessage);
				if(contact == null || message.getText().toString() == "" || password.getText().toString() == ""){
					showDialog(DIALOG_MESSAGE_ID_3);
				}
				else{
				      try {
						encryptedMessage = SimpleCrypto.encrypt(password.getText().toString(), message.getText().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				      
				      validateAndSendSms(contact, encryptedMessage);
				showDialog(DIALOG_MESSAGE_ID_1);
				
				}
			}
		});
		
	}
     
	/* Start menu item code
	 * 
	 */
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
			return true;
		case R.id.decrypt:
			startActivity(new Intent(this, Decrypt.class));
			return true;
		case R.id.encrypt:
			startActivity(new Intent(this, Main.class));
			return true;
		}
		return false;
	}

	
	public static void validateAndSendSms(String originatingAddress, String message)
	{
		if (!PhoneNumberUtils.isWellFormedSmsAddress(originatingAddress))
		{
			Log.w(SMS_RECEIVER, "isWellFormedSmsAddress FALSE");
			return;
		}
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(originatingAddress, null, message, Main.sentIntent, null);

		Log.w(SMS_RECEIVER, "message sent: " + message + " to " + originatingAddress);
	}

	@Override
	protected AlertDialog onCreateDialog(int id){
		AlertDialog dialog = null;
		switch(id){
		case DIALOG_MESSAGE_ID_1:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Sent message:");
			builder.setMessage("Sent message:" + encryptedMessage + "To:" + contact);
			Log.i("AlertDialog", "Informed user of sent message");
			dialog = builder.create();
			break;
		case MANUAL_ENTRY_ID_2:
			AlertDialog.Builder manualEntry = new AlertDialog.Builder(this);
			manualEntry.setTitle("Please enter the phone number you wish to contact:");
			manualEntry.setMessage("Number:");
			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			manualEntry.setView(input);
			manualEntry.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  // Do something with value!
				  contact = input.getText().toString();
				  Log.w("Contact", "Entered value is" + contact);
					}
				});
			dialog = manualEntry.create();
			break;
		case DIALOG_MESSAGE_ID_3:
			AlertDialog.Builder error = new AlertDialog.Builder(this);
			error.setTitle("Error");
			error.setMessage("Please check your input and try again.");
			dialog = error.create();
			break;
		}
		return dialog;
	}
}
	
