package com.pasumangkasa.freemusicdownloadtubity.adapter;

import android.content.Context;
import android.database.Cursor;
import androidx.cursoradapter.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasumangkasa.freemusicdownloadtubity.R;
import com.pasumangkasa.freemusicdownloadtubity.constants.PasumConstants;

import java.util.ArrayList;

/**
 * 
 *
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.pasumangkasa.com
 * @Project:AndroidCloundMusicPlayer
 * @Date:Dec 14, 2014 
 *
 */
public class SuggestionAdapter extends CursorAdapter implements PasumConstants {
	public static final String TAG = SuggestionAdapter.class.getSimpleName();

	private ArrayList<String> mListItems;

	
	public SuggestionAdapter(Context context, Cursor c, ArrayList<String> items) {
		super(context, c, false);
		this.mListItems =items;
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView mTextView = view.findViewById(R.id.tv_name_options);
		mTextView.setText(mListItems.get(cursor.getPosition()));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if(cursor.getPosition()>=0 && cursor.getPosition()<mListItems.size()){
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	View view = inflater.inflate(R.layout.item_suggestion, parent, false);
        	TextView mTextView =view.findViewById(R.id.tv_name_options);
        	mTextView.setText(Html.fromHtml(mListItems.get(cursor.getPosition())));
        	return view;
        }
        return null;
	}
	


}
