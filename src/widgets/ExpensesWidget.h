// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include "widgets/BaseTableWidget.h"
#include "dialogs/ItemDialogs.h"

class ExpensesWidget : public BaseTableWidget
{
    Q_OBJECT
public:
    explicit ExpensesWidget(QWidget *parent = nullptr);

    void refresh() override;

private slots:
    void addExpense();
    void editExpense();
    void deleteExpense();
    void onDoubleClicked(const QModelIndex &index);
    void deleteAllExpenses();

private:
    bool persistChanges();
};