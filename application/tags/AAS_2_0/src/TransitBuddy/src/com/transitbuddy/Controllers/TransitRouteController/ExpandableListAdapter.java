package com.transitbuddy.Controllers.TransitRouteController;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.transitbuddy.*;

public class ExpandableListAdapter extends BaseExpandableListAdapter 
{
	private Context mContext;

    private ArrayList<String> mGroups;

    private ArrayList<ArrayList<String>> mChildren;

    public ExpandableListAdapter(Context context, 
    							 ArrayList<String> groups,
            					 ArrayList<ArrayList<String>> children) 
    {
        mContext  = context;
        mGroups   = groups;
        mChildren = children;
    }

    /**
     * A general add method, that allows you to a group and the associated children
     * 
     * @param group The group the children are associated to.
     * @param children A list of the children nodes.
     */
    public void addGroup(String group, ArrayList<String> children) 
    {
        if (!mGroups.contains(group)) 
        {
            mGroups.add(group);
        }
        
        mChildren.add(children);
    }
    
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) 
    {
        return mChildren.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) 
    {
        return childPosition;
    }
    
    // Return a child view. You can load your custom layout here.
    @Override
    public View getChildView(int groupPosition, 
    						 int childPosition, 
    						 boolean isLastChild,
    						 View convertView, 
    						 ViewGroup parent) 
    {
        String child = (String) getChild(groupPosition, childPosition);
        if (convertView == null) 
        {
            LayoutInflater infalInflater = 
            	(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_layout, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tvChild);
        tv.setText("   " + child);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) 
    {
        return mChildren.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) 
    {
        return mGroups.get(groupPosition);
    }

    @Override
    public int getGroupCount() 
    {
        return mGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) 
    {
        return groupPosition;
    }

    // Return a group view. You can load your custom layout here.
    @Override
    public View getGroupView(int groupPosition, 
    						 boolean isExpanded, 
    						 View convertView,
    						 ViewGroup parent) 
    {
        String group = (String) getGroup(groupPosition);
        
        if (convertView == null) 
        {
            LayoutInflater infalInflater = 
            	(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_layout, null);
        }
        
        TextView tv = (TextView) convertView.findViewById(R.id.tvGroup);
        tv.setText(group);
        return convertView;
    }

    @Override
    public boolean hasStableIds() 
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) 
    {
        return true;
    }

}