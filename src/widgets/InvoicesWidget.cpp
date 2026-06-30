// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "widgets/InvoicesWidget.h"
#include <QMessageBox>
#include <QColor>
#include <QBrush>
#include <QStandardItem>
#include <QHeaderView>

InvoicesWidget::InvoicesWidget(QWidget* parent)
    : BaseTableWidget(parent) {
    setupTable({ "Invoice Number", "Total", "Balance", "Payments" });
    setupButtons("Add Invoice", "Edit", "Delete");

    auto* header = table()->horizontalHeader();
    header->setStretchLastSection(false);
    header->setSectionResizeMode(3, QHeaderView::Fixed);
    header->resizeSection(3, 150);

    auto* delegate = new ButtonDelegate("View Payments", this);
    table()->setItemDelegateForColumn(3, delegate);
    connect(delegate, &ButtonDelegate::clicked, this, &InvoicesWidget::viewPayments);

    connect(addButton(), &QPushButton::clicked, this, &InvoicesWidget::addInvoice);
    connect(editButton(), &QPushButton::clicked, this, &InvoicesWidget::editInvoice);
    connect(deleteButton(), &QPushButton::clicked, this, &InvoicesWidget::deleteInvoice);
    connect(table(), &QTableView::doubleClicked, this, &InvoicesWidget::onDoubleClicked);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &InvoicesWidget::refresh);
    connect(this, &BaseTableWidget::rowsMoved,
            this, [this](int from, int to) {
                auto& list = DataManager::instance().data().invoices;
                if (from < 0 || from >= list.size() ||
                    to < 0 || to >= list.size() || from == to)
                    return;
                list.move(from, to);
                if (persistChanges()) refresh();
            });
    refresh();
}

bool InvoicesWidget::persistChanges() {
    QString error;
    if (!DataManager::instance().save(&error)) {
        QMessageBox::critical(this, "Save Error", error);
        return false;
    }
    emit DataManager::instance().dataChanged();
    return true;
}

void InvoicesWidget::refresh() {
    model()->removeRows(0, model()->rowCount());
    const auto& list = DataManager::instance().data().invoices;
    for (const auto& invoice : list) {
        const double balance = invoice.balance();
        QList<QStandardItem*> row;
        auto* numberItem = new QStandardItem(invoice.number);
        QFont font = numberItem->font();
        font.setBold(true);
        numberItem->setFont(font);
        row << numberItem;
        row << new QStandardItem(QString("$%1").arg(invoice.total, 0, 'f', 2));
        auto* balanceItem = new QStandardItem(QString("$%1").arg(balance, 0, 'f', 2));
        if (balance <= 0.0) {
            balanceItem->setForeground(QBrush(QColor("#107c10")));
        } else {
            balanceItem->setForeground(QBrush(QColor("#c57d00")));
        }
        if (balance < 0.0) {
            balanceItem->setForeground(QBrush(QColor("#c50f1f")));
        }
        row << balanceItem;
        auto* buttonItem = new QStandardItem("View Payments");
        row << buttonItem;
        model()->appendRow(row);
    }
    table()->resizeColumnsToContents();
    auto* header = table()->horizontalHeader();
    header->setSectionResizeMode(3, QHeaderView::Fixed);
    header->resizeSection(3, 150);
}

void InvoicesWidget::addInvoice() {
    InvoiceDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        DataManager::instance().data().invoices.append(dialog.resultData());
        if (persistChanges()) refresh();
    }
}

void InvoicesWidget::editInvoice() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().invoices;
    if (row < 0 || row >= list.size()) return;
    InvoiceDialog dialog(this, &list[row]);
    if (dialog.exec() == QDialog::Accepted) {
        const QList<Payment> payments = list[row].payments;
        list[row] = dialog.resultData();
        list[row].payments = payments;
        if (persistChanges()) refresh();
    }
}

void InvoicesWidget::deleteInvoice() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().invoices;
    if (row < 0 || row >= list.size()) return;
    if (QMessageBox::question(this, "Delete Invoice", "Delete the selected invoice?") == QMessageBox::Yes) {
        list.removeAt(row);
        if (persistChanges()) refresh();
    }
}

void InvoicesWidget::onDoubleClicked(const QModelIndex&) {
    editInvoice();
}

void InvoicesWidget::viewPayments(const QModelIndex& index) {
    const int row = index.row();
    auto& list = DataManager::instance().data().invoices;
    if (row < 0 || row >= list.size()) return;
    PaymentsDialog dialog(&list[row], this);
    if (dialog.exec() == QDialog::Accepted) {
        if (persistChanges()) refresh();
    }
}