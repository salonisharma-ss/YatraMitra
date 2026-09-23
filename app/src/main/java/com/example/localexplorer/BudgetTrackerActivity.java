package com.example.localexplorer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetTrackerActivity extends AppCompatActivity {

    private EditText inputAmount;
    private EditText inputCategory;
    private EditText inputPlace;
    private Button btnAddExpense;
    private Button btnClear;
    private Button btnBack;
    private TextView totalBudget;
    private LinearLayout expensesList;
    private BudgetDatabase budgetDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_tracker);

        budgetDB = new BudgetDatabase(this);

        inputAmount = findViewById(R.id.inputAmount);
        inputCategory = findViewById(R.id.inputCategory);
        inputPlace = findViewById(R.id.inputPlace);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnClear = findViewById(R.id.btnClear);
        btnBack = findViewById(R.id.btnBudgetBack);
        totalBudget = findViewById(R.id.totalBudget);
        expensesList = findViewById(R.id.expensesList);

        btnAddExpense.setOnClickListener(v -> addExpense());
        btnClear.setOnClickListener(v -> clearAll());
        btnBack.setOnClickListener(v -> finish());

        displayExpenses();
    }

    private void addExpense() {
        String amountStr = inputAmount.getText().toString().trim();
        String category = inputCategory.getText().toString().trim();
        String place = inputPlace.getText().toString().trim();

        if (amountStr.isEmpty() || category.isEmpty() || place.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            BudgetExpense expense = new BudgetExpense();
            expense.setAmount(amount);
            expense.setCategory(category);
            expense.setPlace(place);
            expense.setDate(new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(new Date()));

            budgetDB.addExpense(expense);

            inputAmount.setText("");
            inputCategory.setText("");
            inputPlace.setText("");

            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
            displayExpenses();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayExpenses() {
        List<BudgetExpense> expenses = budgetDB.getAllExpenses();
        expensesList.removeAllViews();

        double total = 0;

        if (expenses.isEmpty()) {
            TextView noExpenses = new TextView(this);
            noExpenses.setText("No expenses yet");
            noExpenses.setPadding(16, 16, 16, 16);
            expensesList.addView(noExpenses);
        } else {
            for (BudgetExpense expense : expenses) {
                total += expense.getAmount();

                LinearLayout expenseCard = new LinearLayout(this);
                expenseCard.setOrientation(LinearLayout.VERTICAL);
                expenseCard.setPadding(12, 12, 12, 12);
                expenseCard.setBackgroundResource(R.drawable.place_card_bg);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 10);
                expenseCard.setLayoutParams(params);

                // Place name
                TextView placeView = new TextView(this);
                placeView.setText(expense.getPlace());
                placeView.setTextSize(16);
                placeView.setTextColor(0xFF1976D2);
                placeView.setTypeface(null, android.graphics.Typeface.BOLD);
                expenseCard.addView(placeView);

                // Category and amount
                TextView detailsView = new TextView(this);
                detailsView.setText(expense.getCategory() + " • ₹" + expense.getAmount());
                detailsView.setTextSize(14);
                detailsView.setTextColor(0xFF666666);
                detailsView.setPadding(0, 4, 0, 0);
                expenseCard.addView(detailsView);

                // Date
                TextView dateView = new TextView(this);
                dateView.setText(expense.getDate());
                dateView.setTextSize(12);
                dateView.setTextColor(0xFF999999);
                dateView.setPadding(0, 4, 0, 0);
                expenseCard.addView(dateView);

                // Delete button
                Button deleteBtn = new Button(this);
                deleteBtn.setText("Delete");
                deleteBtn.setBackgroundColor(0xFFE91E63);
                deleteBtn.setTextColor(0xFFFFFFFF);
                LinearLayout.LayoutParams deleteBtnParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                deleteBtnParams.setMargins(0, 8, 0, 0);
                deleteBtn.setLayoutParams(deleteBtnParams);

                final BudgetExpense expenseToDelete = expense;
                deleteBtn.setOnClickListener(v -> {
                    budgetDB.deleteExpense(expenseToDelete);
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                    displayExpenses();
                });

                expenseCard.addView(deleteBtn);
                expensesList.addView(expenseCard);
            }
        }

        totalBudget.setText("Total Spent: ₹" + String.format("%.2f", total));
    }

    private void clearAll() {
        budgetDB.clearAll();
        Toast.makeText(this, "All expenses cleared", Toast.LENGTH_SHORT).show();
        displayExpenses();
    }
}