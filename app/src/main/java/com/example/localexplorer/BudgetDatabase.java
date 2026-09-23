package com.example.localexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BudgetDatabase {
    private static final String PREF_NAME = "budget_pref";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public BudgetDatabase(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    private String getUserKey() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : "guest";
    }

    public void addExpense(BudgetExpense expense) {
        List<BudgetExpense> expenses = getAllExpenses();
        expenses.add(0, expense);
        saveExpenses(expenses);
    }

    public List<BudgetExpense> getAllExpenses() {
        String json = sharedPreferences.getString("expenses_" + getUserKey(), "[]");
        Type type = new TypeToken<List<BudgetExpense>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void deleteExpense(BudgetExpense expense) {
        List<BudgetExpense> expenses = getAllExpenses();
        expenses.removeIf(e ->
                e.getAmount() == expense.getAmount() &&
                        e.getPlace().equals(expense.getPlace())
        );
        saveExpenses(expenses);
    }

    public void clearAll() {
        sharedPreferences.edit().remove("expenses_" + getUserKey()).apply();
    }

    private void saveExpenses(List<BudgetExpense> expenses) {
        String json = gson.toJson(expenses);
        sharedPreferences.edit().putString("expenses_" + getUserKey(), json).apply();
    }
}
