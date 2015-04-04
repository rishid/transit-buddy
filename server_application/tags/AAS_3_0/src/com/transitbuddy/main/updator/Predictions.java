package com.transitbuddy.main.updator;

import java.util.ArrayList;

public class Predictions
{
	public String mAgency;
	public String mRouteTag;
	public ArrayList<Direction> mDirections = new ArrayList<Predictions.Direction>();

	public class Direction
	{
		public String mHeadsign;
		public String mDirectionName; // inbound / outbound
		public ArrayList<Stop> mStops = new ArrayList<Stop>();

		public class Stop
		{
			public String mStopTag;
			public String mStopTitle;
			public ArrayList<Prediction> mPredictions = new ArrayList<Prediction>();

			public class Prediction
			{
				public String mArrivalTime;
				public String mVehicle;

				public String toString()
				{
					String s = new String();
					s += "    - arrival: " + mArrivalTime + "\n";
					s += "      vehicle: " + mVehicle + "\n";
					return s;
				}
			}

			public String toString()
			{
				String s = new String();
				s += "  - tag: " + mStopTag + "\n";
				s += "    title: " + mStopTitle + "\n";
				s += "    predictions:\n";
				for (Prediction p : mPredictions)
				{
					s += p;
				}
				return s;
			}
		}

		public String toString()
		{
			String s = new String();
			s += "- headsign: " + mHeadsign + "\n";
			s += "  direction_name: " + mDirectionName + "\n";
			s += "  stops:\n";
			for (Stop st : mStops)
			{
				s += st;
			}
			return s;
		}
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nagency: " + mAgency + "\nroute: " + mRouteTag + "\n");
		sb.append("directions:\n");
		for (Direction d : mDirections)
		{
			sb.append(d);
		}
		return sb.toString();
	}
}
