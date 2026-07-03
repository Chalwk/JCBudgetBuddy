// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include "widgets/BaseTableWidget.h"
#include "dialogs/ItemDialogs.h"

class WeeklyBillsWidget : public BaseTableWidget {
    Q_OBJECT
public:
    explicit WeeklyBillsWidget(QWidget* parent = nullptr);

    void refresh() override;

private slots:
    void addBill();
    void editBill();
    void deleteBill();
    void onDoubleClicked(const QModelIndex& index);

private:
    bool persistChanges();
};
