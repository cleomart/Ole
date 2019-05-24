package com.corey.ole;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;

public class LandlordRepairAdapter extends RecyclerView.Adapter<LandlordRepairAdapter.LandlordRepairView> {

    private ArrayList<Repair> mData;
    private AddPolicyNoteAdapter.OnItemClickListener mListener;
    private int currentPosition;
    private Context context;

    public LandlordRepairAdapter(ArrayList<Repair> data, Context context) {
        mData = data;
        this.context = context;
    }
    @Override
    public void onBindViewHolder(LandlordRepairAdapter.LandlordRepairView holder, int position) {
        currentPosition = position;
        Repair repair  = mData.get(position);
        holder.bindView(repair);
    }

    @Override
    public LandlordRepairAdapter.LandlordRepairView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.landlord_repair_cell, parent, false);
        return new LandlordRepairAdapter.LandlordRepairView(v, mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AddPolicyNoteAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    class LandlordRepairView extends RecyclerView.ViewHolder {
        TextView message;
        TextView name;
        TextView email;
        TextView room;
        Button scheduleButton;
        EditText month;
        EditText day;
        EditText year;
        EditText hour;
        EditText minute;
        EditText repairer;
        AlertDialog.Builder builder;
        Repair currentRepair;
        RadioGroup amPmRadio;
        ArrayList<Repair> tenantRepairs;
        AlertDialog  dialog;
        CheckBox resolveBox;
        int repairPosition;


        public LandlordRepairView(View itemView, final AddPolicyNoteAdapter.OnItemClickListener listener) {
            super(itemView);
            message = itemView.findViewById(R.id.landlord_repair_message);
            name = itemView.findViewById(R.id.landlord_repair_name);
            email = itemView.findViewById(R.id.landlord_repair_email);
            room = itemView.findViewById(R.id.landlord_repair_room);
            scheduleButton = itemView.findViewById(R.id.landlord_repair_schedule_button);
            resolveBox = itemView.findViewById(R.id.checkbox_resolve);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void bindView(Repair repair) {
            currentRepair = repair;
            message.setText("Request: " + repair.getRequest());
            message.setTypeface(null, Typeface.BOLD);
            name.setText("From: " + repair.getName());
            email.setText("Email: " + repair.getEmail());
            room.setText("Room No: " + repair.getRoom());
            if (repair.getDateSchedule() !=  null) {
                scheduleButton.setText("Schedule Details");
            }

            if (repair.isFixed()) {
                resolveBox.setChecked(true);
            }

            resolveBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findRepairPositionInFirebase();
                }
            });


            scheduleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(context);
                    View view = LayoutInflater.from(context).inflate(R.layout.schedule_repair, null);

                    month = view.findViewById(R.id.schedule_repair_month);
                    day = view.findViewById(R.id.schedule_repair_day);
                    hour = view.findViewById(R.id.schedule_repair_hour);
                    year = view.findViewById(R.id.schedule_repair_year);
                    minute = view.findViewById(R.id.schedule_repair_minute);
                    repairer = view.findViewById(R.id.schedule_repair_repairer);
                    amPmRadio = view.findViewById(R.id.schedule_repair_am_pm_field);

                    if (currentRepair.getDateSchedule() != null) {
                        Date date = currentRepair.getDateSchedule();
                        int hours = date.getHours();
                        if (date.getHours() >= 12) {
                            amPmRadio.check(R.id.pmRadioButton);
                            hours -= 12;
                        } else {
                            amPmRadio.check(R.id.amRadioButton);

                        }
                        String rep = currentRepair.getRepairer();
                        month.setText(Integer.toString(date.getMonth()));
                        day.setText(Integer.toString(date.getDate()));
                        year.setText(Integer.toString(date.getYear()));
                        hour.setText(Integer.toString(hours));
                        minute.setText(Integer.toString(date.getMinutes()));
                        repairer.setText(rep);
                    }
                    builder.setTitle("Schedule");
                    builder.setView(view);
                    builder.setCancelable(false).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton(R.string.save, null);
                    dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Button button =  dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (month.getText().toString().isEmpty() || day.getText().toString().isEmpty() ||
                                            year.getText().toString().isEmpty() || hour.getText().toString().isEmpty() || minute.getText().toString().isEmpty()
                                            || repairer.getText().toString().isEmpty() || amPmRadio.getCheckedRadioButtonId() == -1) {
                                        Toast.makeText(context, "Every field is required.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {

                                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference ref = database.getReference("users/" + currentRepair.getTenantId() + "/repairs");

                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                GenericTypeIndicator<ArrayList<Repair>> g  = new GenericTypeIndicator<ArrayList<Repair>>() {};
                                                tenantRepairs = dataSnapshot.getValue(g);

                                                for (int i = 0; i < tenantRepairs.size(); i++) {
                                                    if (tenantRepairs.get(i).getId().equals(currentRepair.getId())) {
                                                        repairPosition = i;
                                                        saveRepairSchedule();
                                                        return;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                System.out.println("The read failed: " + databaseError.getCode());
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            });
        }

        private void findRepairPositionInFirebase () {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users/" + currentRepair.getTenantId() + "/repairs");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<Repair>> g  = new GenericTypeIndicator<ArrayList<Repair>>() {};
                    tenantRepairs = dataSnapshot.getValue(g);

                    for (int i = 0; i < tenantRepairs.size(); i++) {
                        if (tenantRepairs.get(i).getId().equals(currentRepair.getId())) {
                            repairPosition = i;
                            saveRepairResolve();
                            return;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });


        }

        private void  saveRepairResolve () {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");
            DatabaseReference repairRef = ref.child(currentRepair.getTenantId()).child("repairs").child(Integer.toString(repairPosition));
            repairRef.child("fixed").setValue(resolveBox.isChecked());
        }

        private void saveRepairSchedule () {
            int y = Integer.parseInt(year.getText().toString());
            int m = Integer.parseInt(month.getText().toString());
            int d = Integer.parseInt(day.getText().toString());
            int h = Integer.parseInt(hour.getText().toString());
            int min = Integer.parseInt(minute.getText().toString());
            if (amPmRadio.getCheckedRadioButtonId() == R.id.pmRadioButton)
                h += 12;
            Date date = new Date(y, m, d, h, min, 0);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");
            DatabaseReference repairRef = ref.child(currentRepair.getTenantId()).child("repairs").child(Integer.toString(repairPosition));
            repairRef.child("dateSchedule").setValue(date);
            repairRef.child("repairer").setValue(repairer.getText().toString());

            dialog.cancel();
        }
    }

}
