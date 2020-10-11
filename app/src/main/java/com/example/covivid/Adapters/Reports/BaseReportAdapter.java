package com.example.covivid.Adapters.Reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covivid.Model.BaseCovidReport;
import com.example.covivid.R;

import java.util.List;

public class BaseReportAdapter extends RecyclerView.Adapter<BaseReportAdapter.ReportViewHolder>
{
    Context context;
    List<BaseCovidReport> reports;

    public BaseReportAdapter(Context context, List<BaseCovidReport> reports)
    {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View layout;
        layout = LayoutInflater
                .from(context)
                .inflate(R.layout.base_report_item, parent, false);

        return new ReportViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position)
    {
        holder.coronaPic.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));

        holder.countryTxt.setText(reports.get(position).getCountry());
        holder.casesTxt.setText(String.valueOf(reports.get(position).getCases()));
        holder.statusTxt.setText(reports.get(position).getStatus());
        holder.coronaPic.setImageResource(R.drawable.covid_img);
    }

    @Override
    public int getItemCount()
    {
        return reports.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder
    {
        TextView countryTxt, casesTxt, statusTxt;
        ImageView coronaPic;
        RelativeLayout container;
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.base_report_container);
            countryTxt = itemView.findViewById(R.id.base_report_item_title);
            casesTxt = itemView.findViewById(R.id.base_report_item_description);
            statusTxt = itemView.findViewById(R.id.base_report_item_status);
            coronaPic = itemView.findViewById(R.id.base_report_img);
        }
    }
}
