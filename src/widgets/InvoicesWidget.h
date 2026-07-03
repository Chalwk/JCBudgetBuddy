// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include "widgets/BaseTableWidget.h"
#include "dialogs/ItemDialogs.h"
#include "delegates/ButtonDelegate.h"

class InvoicesWidget : public BaseTableWidget {
    Q_OBJECT
public:
    explicit InvoicesWidget(QWidget* parent = nullptr);

    void refresh() override;

private slots:
    void addInvoice();
    void editInvoice();
    void deleteInvoice();
    void onDoubleClicked(const QModelIndex& index);
    void viewPayments(const QModelIndex& index);

private:
    bool persistChanges();
};
