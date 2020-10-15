package com.example.covivid.Adapters.Reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covivid.Activities.BaseReportActivity;
import com.example.covivid.Contracts.IItemClickListener;
import com.example.covivid.Model.BaseCovidReport;
import com.example.covivid.R;
import com.example.covivid.Utils.Common;

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

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position)
    {
        holder.coronaPic.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));

        holder.countryTxt.setText(reports.get(position).getCountry());
        holder.casesTxt.setText(
                String.format("%s: %d",
                        context.getResources().getString(R.string.cases),
                        reports.get(position).getCases()));
        holder.statusTxt.setText(
                String.format("%s: %s",
                        context.getResources().getString(R.string.status),
                        reports.get(position).getStatus()));
        holder.coronaPic.setImageResource(R.drawable.covid_img);
        Common.baseReport = reports.get(position);
        holder.setClickListener(
                view -> context.startActivity(new Intent(context, BaseReportActivity.class)));
    }

    @Override
    public int getItemCount()
    {
        return reports.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView countryTxt, casesTxt, statusTxt;
        ImageView coronaPic;
        RelativeLayout container;
        IItemClickListener clickListener;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.base_report_container);
            countryTxt = itemView.findViewById(R.id.base_report_item_title);
            casesTxt = itemView.findViewById(R.id.base_report_item_description);
            statusTxt = itemView.findViewById(R.id.base_report_item_status);
            coronaPic = itemView.findViewById(R.id.base_report_img);

            itemView.setOnClickListener(this);
        }

        public void setClickListener(IItemClickListener clickListener)
        {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View view)
        {
            clickListener.onClick(view);
        }
    }
}
