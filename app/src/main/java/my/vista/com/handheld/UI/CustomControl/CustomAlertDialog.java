package my.vista.com.handheld.UI.CustomControl;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import my.vista.com.handheld.R;

public class CustomAlertDialog {
	public static void Show(final Context context, String title, String message,int type)
    {
    	Builder builder = new Builder(context, R.style.AppTheme_Dialog);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	if( type == 0)
    	{
    		builder.setPositiveButton("OK", null);
    	}
    	if(type == 1)
    	{
    		builder.setPositiveButton("OK", null);
    		builder.setNegativeButton("Cancel",null);
    	}
    	if( type == 2)
    	{
    		builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                	
                }
    		});
    		
    		builder.setNegativeButton("No",null);
    	}
    	if(type == 3)
    	{
    		builder.setPositiveButton("OK", null);
    		builder.setCancelable(true);
    	}
    	builder.show();
    }
}
