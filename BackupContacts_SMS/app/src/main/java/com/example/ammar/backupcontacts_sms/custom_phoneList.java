package com.example.ammar.backupcontacts_sms;

import java.util.List;

//import com.squareup.picasso.Picasso;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class custom_phoneList extends ArrayAdapter<Contacts_NameEmailNumber> implements OnClickListener
{
	List<Contacts_NameEmailNumber> list;
	SharedPreferences sharedPreferences ;
	
	
	Context context;
	public custom_phoneList(Context context, List<Contacts_NameEmailNumber> list)
	{
		super(context, R.layout.custom_phonelist,list);
		this.context=context;
		this.list=list;
		
	}
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) 
	{
		
		
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		    convertView=inflater.inflate(R.layout.custom_phonelist, parent,false);
		    holder.Name=(TextView) convertView.findViewById(R.id.Name);
		    holder.MobileNumber=(TextView) convertView.findViewById(R.id.mobileNumber);
		    /*holder.flag = (ImageView) convertView.findViewById(R.id.flagImage);*/
		  
		   

			
			
		    convertView.setTag(holder);
		    convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getContext(), "aaa", Toast.LENGTH_SHORT).show();
					
				}
			});
		  
		    
		}else{
			holder=(ViewHolder) convertView.getTag();
			
		}
		
	//	Log.e("custom_list", list.get(position).getCountry() + "" );

		
		holder.Name.setText(list.get(position).getName());
		holder.MobileNumber.setText(list.get(position).getNumber()+"");
		
		
		return convertView;
	}
	static class ViewHolder 
	{
		ImageView flag;
		TextView Name, MobileNumber;
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	
}
