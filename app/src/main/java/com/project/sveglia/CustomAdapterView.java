package com.project.sveglia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pat on 27/06/18.
 */

public class CustomAdapterView extends RecyclerView.Adapter <CustomAdapterView.MyViewHolder> {

    static ArrayList<DataModelView> dataSet;
    boolean [] repetitions_day;
    DB_Manager db;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView timeSveglia;
        TextView nomeSveglia;
        TextView dataSveglia;
        TextView destinazione;
        Switch on_off;
        CardView lun;
        CardView mar;
        CardView mer;
        CardView gio;
        CardView ven;
        CardView sab;
        CardView dom;
        RelativeLayout giorni_ripetizioni;
        CardView sveglia;
        ImageView img_destinazione;
        ImageView img_nome_sveglia;
        ImageView img_data_sveglia;


        public MyViewHolder(View itemView) {
            super(itemView);

            timeSveglia=(TextView)itemView.findViewById(R.id.time_sveglia);
            nomeSveglia=(TextView)itemView.findViewById(R.id.nome_sveglia);
            on_off=(Switch)itemView.findViewById(R.id.on_off);
            lun=(CardView)itemView.findViewById(R.id.Lun_Circle);
            mar=(CardView)itemView.findViewById(R.id.Mar_Circle);
            mer=(CardView)itemView.findViewById(R.id.Mer_Circle);
            gio=(CardView)itemView.findViewById(R.id.Gio_Circle);
            ven=(CardView)itemView.findViewById(R.id.Ven_Circle);
            sab=(CardView)itemView.findViewById(R.id.Sab_Circle);
            dom=(CardView)itemView.findViewById(R.id.Dom_Circle);
            giorni_ripetizioni=(RelativeLayout)itemView.findViewById(R.id.giorni_ripetizione);
            sveglia=(CardView) itemView.findViewById(R.id.visualizzatore_sveglia);
            dataSveglia=(TextView)itemView.findViewById(R.id.Data_Sveglia_ID);
            destinazione=(TextView)itemView.findViewById(R.id.info_Maps_Sveglia_ID);
            img_destinazione=(ImageView)itemView.findViewById(R.id.imageView);
            img_data_sveglia =(ImageView)itemView.findViewById(R.id.ImageView_Calendar);
            img_nome_sveglia=(ImageView)itemView.findViewById(R.id.imageView_Name);
        }
    }


    public CustomAdapterView(ArrayList<DataModelView> dataSet, DB_Manager db, Context context){
        this.dataSet=dataSet;
        this.db=db;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_sveglia, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        TextView time_sveglia = holder.timeSveglia;
        TextView nome_sveglia = holder.nomeSveglia;
        TextView data_sveglia = holder.dataSveglia;
        TextView destinazione_sveglia = holder.destinazione;
        final Switch on_off = holder.on_off;
        CardView lun = holder.lun;
        CardView mar = holder.mar;
        CardView mer = holder.mer;
        CardView gio = holder.gio;
        CardView ven = holder.ven;
        CardView sab = holder.sab;
        CardView dom = holder.dom;
        final CardView sveglia = holder.sveglia;
        ImageView img_destinazione = holder.img_destinazione;
        ImageView img_data_sveglia = holder.img_data_sveglia;
        ImageView img_nome_sveglia = holder.img_nome_sveglia;

        Bitmap image_calendar = BitmapFactory.decodeResource(context.getResources(), R.drawable.icons8_calendar_24);
        Bitmap image_name = BitmapFactory.decodeResource(context.getResources(), R.drawable.icons8_autograph_24);
        Bitmap image_google_maps = BitmapFactory.decodeResource(context.getResources(), R.drawable.google_maps);

        img_data_sveglia.setImageBitmap(image_calendar);
        img_nome_sveglia.setImageBitmap(image_name);
        img_destinazione.setImageBitmap(image_google_maps);

        img_data_sveglia.setColorFilter(context.getResources().getColor(R.color.DefaultColorText));
        img_nome_sveglia.setColorFilter(context.getResources().getColor(R.color.DefaultColorText));
        img_destinazione.setColorFilter(context.getResources().getColor(R.color.DefaultColorText));

        time_sveglia.setTextColor(R.color.DefaultColorText);

        ArrayList<String> allVisibleAlarm = db.getAllVisibleAlarm();
        String visible_alarm = allVisibleAlarm.get(position);

        if(dataSet.get(position).getId()==999999999 ){
            sveglia.setVisibility(View.INVISIBLE);
        }else if(visible_alarm.equals("0")){
            sveglia.setVisibility(View.GONE);
        }else{
            sveglia.setVisibility(View.VISIBLE);
        }

        final int id = dataSet.get(position).getId();

        time_sveglia.setText(dataSet.get(position).getTime());
        nome_sveglia.setText(dataSet.get(position).getNome_sveglia());
        on_off.setChecked(dataSet.get(position).isOn_off());
        RelativeLayout giorni_ripetizioni = holder.giorni_ripetizioni;

        if (dataSet.get(position).getTravelTo()){
            data_sveglia.setText("Impostata per il: "+dataSet.get(position).getDataSveglia());
            destinazione_sveglia.setText(dataSet.get(position).getTo());
            giorni_ripetizioni.setVisibility(View.GONE);
            data_sveglia.setVisibility(View.VISIBLE);
            destinazione_sveglia.setVisibility(View.VISIBLE);
            img_destinazione.setVisibility(View.VISIBLE);
        }else{
            giorni_ripetizioni.setVisibility(View.VISIBLE);
            data_sveglia.setVisibility(View.GONE);
            img_data_sveglia.setVisibility(View.GONE);
            destinazione_sveglia.setVisibility(View.GONE);
            img_destinazione.setVisibility(View.GONE);

        }


        repetitions_day = dataSet.get(position).getRepetitions_day();

        boolean repetitions= false;

        for(int j=0;j<7;j++){
            if(repetitions_day[j]){
                repetitions=true;
            }
        }

        if(dataSet.get(position).isOn_off()) {
            sveglia.setBackgroundResource(R.drawable.card_lista_sveglie_attiva);
            giorni_ripetizioni = holder.giorni_ripetizioni;
            if(repetitions){
                giorni_ripetizioni.setVisibility(View.VISIBLE);

                if (repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_active);
                if(!repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_nonattivo);
                if (repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_active);
                if(!repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_nonattivo);
                if (repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_active);
                if(!repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_nonattivo);
                if (repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_active);
                if(!repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_nonattivo);
                if (repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_active);
                if(!repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_nonattivo);
                if (repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_active);
                if(!repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_nonattivo);
                if (repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_active);
                if(!repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_nonattivo);
            }
            else if(!repetitions){
                giorni_ripetizioni.setVisibility(View.GONE);

            }
        }
        if (!dataSet.get(position).isOn_off()){
            sveglia.setBackgroundResource(R.drawable.card_lista_sveglie_non_attiva);
            giorni_ripetizioni = holder.giorni_ripetizioni;

            if(repetitions){
                giorni_ripetizioni.setVisibility(View.VISIBLE);

                if (repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                if(!repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                if (repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                if(!repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                if (repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                if(!repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                if (repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                if(!repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                if (repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                if(!repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                if (repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                if(!repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                if (repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                if(!repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
            }
            else if(!repetitions){
                giorni_ripetizioni = holder.giorni_ripetizioni;
                giorni_ripetizioni.setVisibility(View.GONE);

            }
        }



        boolean finalRepetitions = repetitions;
        on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                repetitions_day = dataSet.get(position).getRepetitions_day();
                RelativeLayout giorni_ripetizioni = holder.giorni_ripetizioni;

                if (on_off.isChecked()){
                    db.SetOn_Off(id,true);
                    dataSet.get(position).on_off=true;

                    ArrayList<String>time_str=db.getAllTimeView();
                    Long time_long = Long.parseLong(time_str.get(position));
                    Cancel_Alarm_Class.cancel_Alarm(id,context,db,true);

                    //System.out.println("isFrom_bed_to_car_added() in dataset = " + dataSet.get(position).isFrom_bed_to_car_added());

                    SetAlarmManager.SetAlarmManager(context,time_long,dataSet.get(position).getId_suoneria(),
                            dataSet.get(position).getRitarda(),dataSet.get(position).getNome_sveglia(),
                            dataSet.get(position).getRepetitions_day(),
                            dataSet.get(position).getTravelTo(),dataSet.get(position).getPosizione_suoneria(),
                            dataSet.get(position).getFrom(),dataSet.get(position).getTo(),
                            dataSet.get(position).getMezzo(), null, dataSet.get(position).isFrom_bed_to_car_added(), "1");

                    sveglia.setBackgroundResource(R.drawable.card_lista_sveglie_attiva);

                    if(finalRepetitions){
                        giorni_ripetizioni.setVisibility(View.VISIBLE);

                        if (repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_active);
                        if(!repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_nonattivo);
                        if (repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_active);
                        if(!repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_nonattivo);
                        if (repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_active);
                        if(!repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_nonattivo);
                        if (repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_active);
                        if(!repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_nonattivo);
                        if (repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_active);
                        if(!repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_nonattivo);
                        if (repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_active);
                        if(!repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_nonattivo);
                        if (repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_active);
                        if(!repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_nonattivo);
                    }


                }
                if (!on_off.isChecked()){
                    db.SetOn_Off(id,false);
                    dataSet.get(position).on_off=false;

                    Cancel_Alarm_Class.cancel_Alarm(id,context,db,false);


                    sveglia.setBackgroundResource(R.drawable.card_lista_sveglie_non_attiva);

                    if(finalRepetitions){
                        giorni_ripetizioni.setVisibility(View.VISIBLE);

                        if (repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                        if(!repetitions_day[0]) lun.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                        if (repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                        if(!repetitions_day[1]) mar.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                        if (repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                        if(!repetitions_day[2]) mer.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                        if (repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                        if(!repetitions_day[3]) gio.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                        if (repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                        if(!repetitions_day[4]) ven.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                        if (repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                        if(!repetitions_day[5]) sab.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                        if (repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_active_with_opacity);
                        if(!repetitions_day[6]) dom.setBackgroundResource(R.drawable.color_day_nonattivo_with_opacity);
                    }

                }
            }
        });


        sveglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Add_Alarm.class);
                intent.putExtra("isModifyAlarm",true);
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });


        sveglia.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                if(position!=dataSet.size()-1){
                    Intent intent = new Intent(context,Conferma_elimina.class);
                    intent.putExtra("time_sveglia", dataSet.get(position).getTime());
                    intent.putExtra("id",id);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static int getPosition(int id){
        int position = 9999;

        for(int i=0; i<dataSet.size(); i++){
            if(id == dataSet.get(i).getId()) {
                position = i;
            }
        }

        return position;
    }


    }

