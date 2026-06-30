#pragma once

#include "widgets/BaseTableWidget.h"
#include "dialogs/ItemDialogs.h"

class IncomeWidget : public BaseTableWidget {
    Q_OBJECT
public:
    explicit IncomeWidget(QWidget* parent = nullptr);

    void refresh() override;

private slots:
    void addIncome();
    void editIncome();
    void deleteIncome();
    void onDoubleClicked(const QModelIndex& index);

private:
    bool persistChanges();
};
