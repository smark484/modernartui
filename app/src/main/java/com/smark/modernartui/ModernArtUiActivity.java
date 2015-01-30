package com.smark.modernartui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class ModernArtUiActivity extends Activity {

	static private final String MOMA_URL = "http://www.moma.org";
	
	private SeekBar mSeekBar = null;
	private ArrayList<ColoredViewInfo> mColoredViewInfos = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modern_art_ui);
        
        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        mSeekBar.setMax(255);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				for(int v = 0; v < mColoredViewInfos.size(); v++)
				{
					adjustColor(v, arg1);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}});
        
        mColoredViewInfos = new ArrayList<ColoredViewInfo>();
        ViewGroup root = (ViewGroup) this.findViewById(R.id.ColoredViewGroup);
        
        // cache the colored views together with their initial color values.
        findColoredViews(root);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.modern_art_ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_moreInfo) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Click below to learn more!")
            .setTitle("Inspired by the work of artists...")
            .setPositiveButton("Visit MoMA", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent browseMomaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MOMA_URL));
					startActivity(browseMomaIntent);
				}})
				.setNegativeButton("Not Now", new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}});
        	
        	builder.create().show();
        	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void findColoredViews(ViewGroup root)
    {
        for(int c = 0; c < root.getChildCount(); c++)
        {
        	View view = root.getChildAt(c);
        	if (view instanceof ViewGroup)
        	{
        		findColoredViews((ViewGroup)view);
        	} else
        	{
        		mColoredViewInfos.add(new ColoredViewInfo(view, ((ColorDrawable)view.getBackground()).getColor()));
        	}
        }    	
    }
    
    private void adjustColor(int viewIdx, int progress)
    {
    	ColoredViewInfo viewInfo =  mColoredViewInfos.get(viewIdx);
    	int c = viewInfo.getInitialColor();
        int averageRgb = (Color.red(c) + Color.green(c) + Color.blue(c)) / 3;
        if(Color.red(c) == Color.green(c) && Color.green(c) == Color.blue(c))
        {
        	// it's a shade of grey - don't change it!
        	return;
        }
    	int red = newColorValue(Color.red(c), progress, averageRgb);
    	int green = newColorValue(Color.green(c), progress, averageRgb);
    	int blue = newColorValue(Color.blue(c), progress, averageRgb);
    	viewInfo.getView().setBackgroundColor(Color.argb(Color.alpha(c), red, green, blue));
    }
    
    private int newColorValue(int colorvalue, int progress, int averageRgb)
    {
    	return (int) (colorvalue + (colorvalue - averageRgb) * changeRate(colorvalue, progress));
    }
    
    private double changeRate(int value, int progress)
    {
    	// reduce change rate when close to limits
    	return progress / (1 + Math.abs(value - 128));
    }
    
    class ColoredViewInfo {
    	private View mView;
    	private int mInitialColor;
    	
    	public ColoredViewInfo(View view, int initialColor)
    	{
    		mView = view;
    		mInitialColor = initialColor;
    	}
    	
    	public View getView() { return mView; }
    	
    	public int getInitialColor() { return mInitialColor; }
    }
}
