package com.voronoi.puzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarDialog 
{
	private final Activity 			activity_;
	private SeekBar 				seekBar_;
	private TextView 				seekBarValue_;
	private int						selectedValue_;
	private AlertDialog.Builder 	dlgBldr_;
	private static final int        TEXT_SIZE = 25;
	

	public SeekBarDialog(Activity activity)
	{
		this.activity_ = activity;
		
		initControls();
		initBldr();
		initButtonListeners();
	}

	private void initControls() {
		seekBarValue_	= new TextView( activity_ );
		seekBarValue_.setGravity(Gravity.CENTER_HORIZONTAL);
		seekBarValue_.setTextSize( TEXT_SIZE );
		seekBar_ 		= new SeekBar( activity_ );
		
		seekBar_.setOnSeekBarChangeListener( 
				new SeekBar.OnSeekBarChangeListener()
				{ 

					@Override 
					public void onProgressChanged(
							SeekBar seekBar, int progress, boolean fromUser) 
					{ 
						seekBarValue_.setText(String.valueOf(progress)); 
						selectedValue_ = progress;
					}

					@Override 
					public void onStartTrackingTouch(SeekBar seekBar) 
					{ 
					} 

					@Override 
					public void onStopTrackingTouch(SeekBar seekBar) 
					{ 
					} 
				});
	}

	private void initButtonListeners() {
		DialogInterface.OnClickListener defaultListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		};
		
		setOkListener		( defaultListener );
		setCancelListener	( defaultListener );
	}
	
	private void initBldr()
	{
		dlgBldr_ = new AlertDialog.Builder( activity_ ); 
		dlgBldr_.setMessage("Please, set tiles count."); 

		LinearLayout layout = new LinearLayout( activity_ );
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		params.setMargins(20, 20, 20, 20);
		params.gravity = Gravity.CENTER_HORIZONTAL;

		layout.addView(seekBarValue_, 	params);
		layout.addView(seekBar_, 		params);

		dlgBldr_.setView(layout);
	}

	public void setMax( int value )
	{
		seekBar_.setMax(value);
	}
	
	public int getValue()
	{
		return selectedValue_;
	}
	
	public void setOkListener( OnClickListener listener )
	{
		dlgBldr_.setPositiveButton("OK", listener );
	}
	
	public void setCancelListener( OnClickListener listener )
	{
		dlgBldr_.setNegativeButton("Cancel", listener );
	}

	public void show()
	{
		dlgBldr_.show();
	}
}
