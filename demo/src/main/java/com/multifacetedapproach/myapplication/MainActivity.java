package com.multifacetedapproach.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.multifacetedapproach.mfamessageview.Listeners.OnMFAClickListener;
import com.multifacetedapproach.mfamessageview.Listeners.OnMFALongClickListener;
import com.multifacetedapproach.mfamessageview.MFAMessage;
import com.multifacetedapproach.mfamessageview.MFAMessageAdapter;
import com.multifacetedapproach.mfamessageview.MFAMessageView;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
      OnMFAClickListener, OnMFALongClickListener, TextWatcher
{
   private MFAMessageView _mfaMessageView;
   private EditText _editText;
   private TextView _sendText;
   private TextView _receiveText;
   private static final int SEND_PHOTO = 1111;
   private static final int RECEIVE_PHOTO = 2222;
   private String _receiverName = "";
   private boolean _barVisible = true;
   /**
    * ATTENTION: This was auto-generated to implement the App Indexing API.
    * See https://g.co/AppIndexing/AndroidStudio for more information.
    */
   private GoogleApiClient client;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      // Example of how you can implement your own custom font.
      Typeface font = Typeface.createFromAsset(getAssets(), "AvenirLTStd-Book.otf");
      // Core class for using this library.
      _mfaMessageView = (MFAMessageView) findViewById(R.id.mfa_message_view);
      // Set your custom font in your MFAMessageView if desired
      if (_mfaMessageView != null)
      {
         _mfaMessageView.setCustomFont(font);
         _mfaMessageView.setOnMFAClickListener(this);
         _mfaMessageView.setOnMFALongClickListener(this);
      }
      // EditText used for adding text messages.
      _editText = (EditText) findViewById(R.id.edit_text);
      if (_editText != null) _editText.addTextChangedListener(this);
      // Pressing this will add _editText's content as a sent message.
      _sendText = (TextView) findViewById(R.id.send_text);
      if (_sendText != null) _sendText.setOnClickListener(this);

      // Pressing this will add _editText's content as a sent message.
      _receiveText = (TextView) findViewById(R.id.receive_text);
      if (_receiveText != null) _receiveText.setOnClickListener(this);
      // Disable the buttons as no text exists in onCreate
      configureButtons(true);
      // Pressing this will take you to your photo gallery and if a photo is selected,
      // will add it as a sent message.
      final Button sendPhoto = (Button) findViewById(R.id.send_photo);
      // Pressing this will take you to your photo gallery and if a photo is selected,
      // will add it as a received message.
      final Button receivePhoto = (Button) findViewById(R.id.receive_photo);

      if (sendPhoto != null)
      {
         sendPhoto.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View view)
            {
               Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
               photoPickerIntent.setType("image/*");
               startActivityForResult(photoPickerIntent, SEND_PHOTO);
            }
         });
      }
      if (receivePhoto != null)
      {
         receivePhoto.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View view)
            {
               Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
               photoPickerIntent.setType("image/*");
               startActivityForResult(photoPickerIntent, RECEIVE_PHOTO);
            }
         });
      }
      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data)
   {
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == RESULT_OK && _mfaMessageView != null)
      {
         // Provided requestCodes for distinguishing between a sent and received photo
         if (requestCode == SEND_PHOTO || requestCode == RECEIVE_PHOTO)
         {
            try
            {
               Uri selectedImage = data.getData();
               Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
               // Don't bother adding the message if the Bitmap is null.
               if (bmp == null) return;
               SimpleDateFormat sdf = _mfaMessageView.getSimpleDateFormat();

               MFAMessage message = new MFAMessage();
               if (_editText != null)
               {
                  message.setMessage(_editText.getText().toString());
                  // Reset the text
                  _editText.setText("");
               }
               String time = sdf.format(new Date(System.currentTimeMillis()));
               message.setTimestamp(time);
               // If true the message should be viewed as sent.
               message.setIsSender(requestCode == SEND_PHOTO);
               // Add the bitmap using the method setMessageImg to include an image in the
               // message.
               message.setMessage(bmp);

               if (requestCode == RECEIVE_PHOTO) message.setName(_receiverName);
               _mfaMessageView.addMessage(message);
            }
            catch (FileNotFoundException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public void onStart()
   {
      super.onStart();

      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      client.connect();
      Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "Main Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app URL is correct.
            Uri.parse("android-app://com.multifacetedapproach.myapplication/http/host/path")
      );
      AppIndex.AppIndexApi.start(client, viewAction);
   }

   @Override
   public void onStop()
   {
      super.onStop();

      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "Main Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app URL is correct.
            Uri.parse("android-app://com.multifacetedapproach.myapplication/http/host/path")
      );
      AppIndex.AppIndexApi.end(client, viewAction);
      client.disconnect();
   }

   @Override
   public void onClick(View view)
   {
      // Some error occurred if either of these are null
      if (_editText == null || _mfaMessageView == null) return;
      // Text to be set as the message
      String text = _editText.getText().toString();
      // You can set your own SimpleDateFormat for the timestamps
      // or use our default format.
      SimpleDateFormat sdf = _mfaMessageView.getSimpleDateFormat();
      // To use this library properly, your message must use the
      // MFAMessage object as it's model
      MFAMessage message = new MFAMessage();
      // Example using the current time the button was pressed as the timestamp
      String time = sdf.format(new Date(System.currentTimeMillis()));
      // Add the timestamp as a string to the message
      message.setTimestamp(time);
      // Set the message's text
      message.setMessage(text);
      // Reset the text
      _editText.setText("");
      // Distinguish if the message should viewed as sent or received.
      switch (view.getId())
      {
         case R.id.send_text:
         {
            // Set to true if you want the message added on the right-hand side.
            message.setIsSender(true);
            break;
         }
         case R.id.receive_text:
         {
            // Set to false if you want the message added on the left-hand side
            message.setIsSender(false);
            message.setName(_receiverName);
            break;
         }
      }
      // Add the message to your MFAMessageView class object.
      _mfaMessageView.addMessage(message);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle item selection
      switch (item.getItemId())
      {
         case R.id.clear:
         {
            if (_mfaMessageView != null)
            {
               _mfaMessageView.clearAllMessages();
            }
            return true;
         }
         case R.id.set_name:
         {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Add Receiver Name");
            ad.setMessage("Sets the name associated with future received messages.");
            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.MATCH_PARENT,
                  LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            ad.setView(input); // uncomment this line
            ad.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
               public void onClick(DialogInterface dialog, int which)
               {
                  // continue with delete
                  if (_mfaMessageView != null && input.getText() != null)
                  {
                     _receiverName = input.getText().toString();
                  }
               }
            });
            ad.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
            {
               public void onClick(DialogInterface dialog, int which)
               {
                  // do nothing
               }
            });
            //ad.setIcon(android.R.drawable.ic_dialog_alert);
            ad.show();
            return true;
         }
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   @Override
   public void onClick(MFAMessageAdapter.ViewHolder holder, @NonNull MFAMessage message, int position)
   {
      // Message was an image
      if (message.getMessageImg() != null)
      {
         displaySelectedImage(message.getMessageImg());
      }
      else // Message was a text
      {
         Toast.makeText(MainActivity.this, "Clicked at index "+position+" message: "
               +message.getMessage(), Toast.LENGTH_LONG).show();
      }
   }

   @Override
   public void onLongClick(MFAMessageAdapter.ViewHolder holder, @NonNull MFAMessage message, int position)
   {
      if (_mfaMessageView != null) _mfaMessageView.removeMessageAtPosition(position);
   }

   /**
    * Displays the image full screen
    * @param bmp Bitmap associated with the image
    */
   private void displaySelectedImage(Bitmap bmp)
   {
      _barVisible = true;
      final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
      dialog.setCancelable(true);
      View popView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            .inflate(R.layout.full_picture_layout, null, false);

      final LinearLayout bottomBar = (LinearLayout) popView.findViewById(R.id.bottom_bar);
      final ImageView selectedPicture = (ImageView) popView.findViewById(R.id.picture);
      final Button backButton = (Button) popView.findViewById(R.id.backButton);

      selectedPicture.setImageBitmap(bmp);

      selectedPicture.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            float translateY = (_barVisible)?bottomBar.getHeight():0;
            bottomBar.animate().translationY(translateY).setInterpolator(new AccelerateInterpolator()).start();
            _barVisible = !_barVisible;
         }
      });

      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setContentView(popView);
      dialog.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT));

      dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);

      backButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            dialog.dismiss();
         }
      });

      dialog.show();
   }

   @Override
   public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
   {

   }

   @Override
   public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
   {

   }

   @Override
   public void afterTextChanged(Editable editable)
   {
      // Do nothing if any of these is null for some reason.
      if (_editText == null || _sendText == null || _receiveText == null) return;

      // No text exists in our EditText
      if (_editText.getText().length() == 0)
      {
         // Disable ability to send or receive text if the text is empty
         configureButtons(true);
      }
      else
      {
         configureButtons(true);
      }
   }

   /**
    * Helper method for configure the send and receive button
    * appearance and events
    * @param enable true if button is to be enabled, false otherwise
    */
   private void configureButtons(boolean enable)
   {
      if (_sendText == null || _receiveText == null) return;

      if (enable)
      {
         // Prevents resetting values repeatedly
         if (!_sendText.isEnabled())
         {
            _sendText.setEnabled(true);
            _sendText.setAlpha(1.0f);
            _sendText.setTextColor(getColor(MainActivity.this, R.color.light_blue));
         }
         if (!_receiveText.isEnabled())
         {
            _receiveText.setEnabled(true);
            _receiveText.setAlpha(1.0f);
            _receiveText.setTextColor(getColor(MainActivity.this, R.color.light_blue));
         }
      }
      else
      {
         // Prevents resetting values repeatedly
         if (_sendText.isEnabled())
         {
            _sendText.setEnabled(false);
            _sendText.setAlpha(0.5f);
            _sendText.setTextColor(getColor(MainActivity.this, R.color.gray));
         }
         if (_receiveText.isEnabled())
         {
            _receiveText.setEnabled(false);
            _receiveText.setAlpha(0.5f);
            _receiveText.setTextColor(getColor(MainActivity.this, R.color.gray));
         }
      }
   }

   private static int getColor(Context context, int id)
   {
      final int version = Build.VERSION.SDK_INT;
      if (version >= 23)
      {
         return ContextCompat.getColor(context, id);
      }
      else
      {
         return context.getResources().getColor(id);
      }
   }
}
