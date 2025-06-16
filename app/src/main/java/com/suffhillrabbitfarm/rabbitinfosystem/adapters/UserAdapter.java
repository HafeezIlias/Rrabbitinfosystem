package com.suffhillrabbitfarm.rabbitinfosystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.models.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserModel> userList;
    private Context context;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserModel user, int position);

        void onUserActionClick(UserModel user, int position);
    }

    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);

        // Basic user info
        holder.txtUserName.setText(user.getUserName() != null ? user.getUserName() : "Unknown User");
        holder.txtUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
        holder.txtAccountType
                .setText(user.getAccountType() != null ? formatAccountType(user.getAccountType()) : "User");
        holder.txtPhoneNumber.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "No phone");

        // Statistics
        holder.txtRabbitCount.setText(String.valueOf(user.getRabbitCount()));
        holder.txtTotalRabbits.setText(String.valueOf(user.getTotalRabbitsEver()));

        // Join date
        holder.txtJoinDate.setText(formatDate(user.getCreatedAt()));

        // Last activity
        if (user.getLastRabbitAdded() != null && !user.getLastRabbitAdded().equals("null")) {
            holder.txtLastActivity.setText(formatRelativeTime(user.getLastRabbitAdded()));
        } else {
            holder.txtLastActivity.setText("No rabbits added");
        }

        // Status indicator (green if has rabbits, gray if none)
        if (user.getRabbitCount() > 0) {
            holder.statusIndicator.setBackgroundResource(R.drawable.status_indicator_active);
        } else {
            holder.statusIndicator.setBackgroundResource(R.drawable.status_indicator_inactive);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user, position);
            }
        });

        holder.btnUserActions.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserActionClick(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateUserList(List<UserModel> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    public void filterUsers(String query) {
        // This would implement search functionality
        // For now, just refresh the list
        notifyDataSetChanged();
    }

    private String formatAccountType(String accountType) {
        if (accountType == null)
            return "User";

        switch (accountType.toLowerCase()) {
            case "manager_account":
                return "Manager";
            case "staff_account":
                return "Staff";
            case "admin":
                return "Admin";
            default:
                return "User";
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null)
            return "Unknown";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Try another format
            try {
                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                Date date = inputFormat2.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e2) {
                return dateStr.substring(0, Math.min(dateStr.length(), 10));
            }
        }
    }

    private String formatRelativeTime(String dateStr) {
        if (dateStr == null || dateStr.equals("null"))
            return "Never";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = format.parse(dateStr);
            Date now = new Date();

            long diffInMillis = now.getTime() - date.getTime();
            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);

            if (diffInDays == 0) {
                return "Today";
            } else if (diffInDays == 1) {
                return "Yesterday";
            } else if (diffInDays < 7) {
                return diffInDays + " days ago";
            } else if (diffInDays < 30) {
                return (diffInDays / 7) + " weeks ago";
            } else {
                return (diffInDays / 30) + " months ago";
            }
        } catch (ParseException e) {
            return "Unknown";
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserEmail, txtAccountType, txtPhoneNumber;
        TextView txtRabbitCount, txtTotalRabbits, txtJoinDate, txtLastActivity;
        View statusIndicator;
        ImageView btnUserActions;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            txtAccountType = itemView.findViewById(R.id.txtAccountType);
            txtPhoneNumber = itemView.findViewById(R.id.txtPhoneNumber);
            txtRabbitCount = itemView.findViewById(R.id.txtRabbitCount);
            txtTotalRabbits = itemView.findViewById(R.id.txtTotalRabbits);
            txtJoinDate = itemView.findViewById(R.id.txtJoinDate);
            txtLastActivity = itemView.findViewById(R.id.txtLastActivity);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            btnUserActions = itemView.findViewById(R.id.btnUserActions);
        }
    }
}
