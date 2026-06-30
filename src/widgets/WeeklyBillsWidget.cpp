// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "widgets/WeeklyBillsWidget.h"

#include <QMessageBox>
#include <QStandardItem>

WeeklyBillsWidget::WeeklyBillsWidget(QWidget* parent)
    : BaseTableWidget(parent) {
    setupTable({ "Name", "Amount", "Frequency", "Payment Day", "Payment Method", "Notes" });
    setupButtons("Add Weekly Bill", "Edit", "Delete");

    connect(addButton(), &QPushButton::clicked, this, &WeeklyBillsWidget::addBill);
    connect(editButton(), &QPushButton::clicked, this, &WeeklyBillsWidget::editBill);
    connect(deleteButton(), &QPushButton::clicked, this, &WeeklyBillsWidget::deleteBill);
    connect(table(), &QTableView::doubleClicked, this, &WeeklyBillsWidget::onDoubleClicked);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &WeeklyBillsWidget::refresh);
    connect(this, &BaseTableWidget::rowsMoved,
            this, [this](int from, int to) {
                auto& list = DataManager::instance().data().weeklyBills;
                if (from < 0 || from >= list.size() ||
                    to < 0 || to >= list.size() || from == to)
                    return;
                list.move(from, to);
                if (persistChanges()) refresh();
            });
    refresh();
}

bool WeeklyBillsWidget::persistChanges() {
    QString error;
    if (!DataManager::instance().save(&error)) {
        QMessageBox::critical(this, "Save Error", error);
        return false;
    }
    emit DataManager::instance().dataChanged();
    return true;
}

void WeeklyBillsWidget::refresh() {
    model()->removeRows(0, model()->rowCount());
    const auto& list = DataManager::instance().data().weeklyBills;
    for (const auto& bill : list) {
        QList<QStandardItem*> row;
        auto* nameItem = new QStandardItem(bill.name);
        QFont font = nameItem->font();
        font.setBold(true);
        nameItem->setFont(font);
        row << nameItem;
        row << new QStandardItem(QString("$%1").arg(bill.amount, 0, 'f', 2));
        row << new QStandardItem(billFrequencyToString(bill.frequency));
        row << new QStandardItem(bill.paymentDay);
        row << new QStandardItem("manual");
        row << new QStandardItem(bill.notes);
        model()->appendRow(row);
    }
    table()->resizeColumnsToContents();
}

void WeeklyBillsWidget::addBill() {
    BillDialog dialog(false, this);
    if (dialog.exec() == QDialog::Accepted) {
        DataManager::instance().data().weeklyBills.append(dialog.resultData());
        if (persistChanges()) refresh();
    }
}

void WeeklyBillsWidget::editBill() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().weeklyBills;
    if (row < 0 || row >= list.size()) return;
    BillDialog dialog(false, this, &list[row]);
    if (dialog.exec() == QDialog::Accepted) {
        list[row] = dialog.resultData();
        list[row].paymentMethod = BillPaymentMethod::Manual;
        if (persistChanges()) refresh();
    }
}

void WeeklyBillsWidget::deleteBill() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().weeklyBills;
    if (row < 0 || row >= list.size()) return;
    if (QMessageBox::question(this, "Delete Bill", "Delete the selected weekly bill?") == QMessageBox::Yes) {
        list.removeAt(row);
        if (persistChanges()) refresh();
    }
}

void WeeklyBillsWidget::onDoubleClicked(const QModelIndex&) {
    editBill();
}