#pragma once

#include "widgets/BaseTableWidget.h"
#include "dialogs/ItemDialogs.h"

class MonthlyBillsWidget : public BaseTableWidget {
    Q_OBJECT
public:
    explicit MonthlyBillsWidget(QWidget* parent = nullptr);

    void refresh() override;

private slots:
    void addBill();
    void editBill();
    void deleteBill();
    void onDoubleClicked(const QModelIndex& index);

private:
    bool persistChanges();
};
