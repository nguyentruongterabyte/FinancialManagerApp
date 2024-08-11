package com.example.financialmanagerapp.model.mapper;

import com.example.financialmanagerapp.model.Budget;
import com.example.financialmanagerapp.model.BudgetDetail;
import com.example.financialmanagerapp.model.DTO.BudgetDTO;
import com.example.financialmanagerapp.model.DTO.BudgetDetailDTO;
import com.example.financialmanagerapp.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class BudgetMapper {
    public static BudgetDTO toBudgetDTO(Budget budget) {
        BudgetDTO budgetDTO = new BudgetDTO();
        budgetDTO.set_name(budget.get_name());
        budgetDTO.set_amount(budget.get_amount());
        budgetDTO.set_color(budget.get_color());
        budgetDTO.set_period(budget.get_period());
        budgetDTO.set_account_id(Utils.currentUser.getId());
        List<BudgetDetail> budgetDetails = budget.getBudget_details();
        List<BudgetDetailDTO> budgetDetailDTOs = new ArrayList<>();
        for (BudgetDetail budgetDetail : budgetDetails) {
            budgetDetailDTOs.add(toBudgetDetailDTO(budgetDetail));
        }
        // Convert list to JSON string
        Gson gson = new Gson();
        String budgetDetailsJSON = gson.toJson(budgetDetailDTOs);
        budgetDTO.setBudgetDetails(budgetDetailsJSON);
        return budgetDTO;
    }

    private static BudgetDetailDTO toBudgetDetailDTO(BudgetDetail budgetDetail) {
        BudgetDetailDTO budgetDetailDTO = new BudgetDetailDTO();
        budgetDetailDTO.set_category_id(budgetDetail.get_category_id());
        return budgetDetailDTO;
    }
}
