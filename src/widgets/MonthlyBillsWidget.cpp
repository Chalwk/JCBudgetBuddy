#include "widgets/MonthlyBillsWidget.h"

#include <QMessageBox>
#include <QStandardItem>

MonthlyBillsWidget::MonthlyBillsWidget(QWidget* parent)
    : BaseTableWidget(parent) {
    setupTable({ "Name", "Amount", "Frequency", "Payment Day", "Payment Method", "Notes" });
    setupButtons("Add Monthly Bill", "Edit", "Delete");

    connect(addButton(), &QPushButton::clicked, this, &MonthlyBillsWidget::addBill);
    connect(editButton(), &QPushButton::clicked, this, &MonthlyBillsWidget::editBill);
    connect(deleteButton(), &QPushButton::clicked, this, &MonthlyBillsWidget::deleteBill);
    connect(table(), &QTableView::doubleClicked, this, &MonthlyBillsWidget::onDoubleClicked);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &MonthlyBillsWidget::refresh);

    refresh();
}

bool MonthlyBillsWidget::persistChanges() {
    QString error;
    if (!DataManager::instance().save(&error)) {
        QMessageBox::critical(this, "Save Error", error);
        return false;
    }
    emit DataManager::instance().dataChanged();
    return true;
}

void MonthlyBillsWidget::refresh() {
    model()->removeRows(0, model()->rowCount());
    const auto& list = DataManager::instance().data().monthlyBills;
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
        row << new QStandardItem(paymentMethodToString(bill.paymentMethod));
        row << new QStandardItem(bill.notes);
        model()->appendRow(row);
    }
    table()->resizeColumnsToContents();
}

void MonthlyBillsWidget::addBill() {
    BillDialog dialog(true, this);
    if (dialog.exec() == QDialog::Accepted) {
        DataManager::instance().data().monthlyBills.append(dialog.resultData());
        if (persistChanges()) refresh();
    }
}

void MonthlyBillsWidget::editBill() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().monthlyBills;
    if (row < 0 || row >= list.size()) return;
    BillDialog dialog(true, this, &list[row]);
    if (dialog.exec() == QDialog::Accepted) {
        list[row] = dialog.resultData();
        if (persistChanges()) refresh();
    }
}

void MonthlyBillsWidget::deleteBill() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().monthlyBills;
    if (row < 0 || row >= list.size()) return;
    if (QMessageBox::question(this, "Delete Bill", "Delete the selected monthly bill?") == QMessageBox::Yes) {
        list.removeAt(row);
        if (persistChanges()) refresh();
    }
}

void MonthlyBillsWidget::onDoubleClicked(const QModelIndex&) {
    editBill();
}