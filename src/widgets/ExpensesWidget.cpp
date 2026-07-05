// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "widgets/ExpensesWidget.h"

#include <QMessageBox>
#include <QStandardItem>
#include <QHBoxLayout>

ExpensesWidget::ExpensesWidget(QWidget *parent)
    : BaseTableWidget(parent)
{
    setupTable({"Date", "Description", "Amount", "Category"});
    setupButtons("Add Expense", "Edit", "Delete");

    QHBoxLayout *buttonLayout = qobject_cast<QHBoxLayout *>(layout()->itemAt(1)->layout());
    if (buttonLayout)
    {
        QPushButton *deleteAllBtn = new QPushButton("Delete All", this);
        buttonLayout->insertWidget(3, deleteAllBtn);
        connect(deleteAllBtn, &QPushButton::clicked, this, &ExpensesWidget::deleteAllExpenses);
    }

    connect(addButton(), &QPushButton::clicked, this, &ExpensesWidget::addExpense);
    connect(editButton(), &QPushButton::clicked, this, &ExpensesWidget::editExpense);
    connect(deleteButton(), &QPushButton::clicked, this, &ExpensesWidget::deleteExpense);
    connect(table(), &QTableView::doubleClicked, this, &ExpensesWidget::onDoubleClicked);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &ExpensesWidget::refresh);
    connect(this, &BaseTableWidget::rowsMoved,
            this, [this](int from, int to)
            {
                auto& list = DataManager::instance().data().expenses;
                if (from < 0 || from >= list.size() ||
                    to < 0 || to >= list.size() || from == to)
                    return;
                list.move(from, to);
                if (persistChanges()) refresh(); });
    refresh();
}

bool ExpensesWidget::persistChanges()
{
    QString error;
    if (!DataManager::instance().save(&error))
    {
        QMessageBox::critical(this, "Save Error", error);
        return false;
    }
    emit DataManager::instance().dataChanged();
    return true;
}

void ExpensesWidget::refresh()
{
    model()->removeRows(0, model()->rowCount());
    const auto &list = DataManager::instance().data().expenses;
    for (const auto &e : list)
    {
        QList<QStandardItem *> row;
        auto *dateItem = new QStandardItem(e.date.toString("dd MMM yyyy"));
        row << dateItem;
        auto *descItem = new QStandardItem(e.description);
        QFont font = descItem->font();
        font.setBold(true);
        descItem->setFont(font);
        row << descItem;
        row << new QStandardItem(QString("$%1").arg(e.amount, 0, 'f', 2));
        row << new QStandardItem(e.category);
        model()->appendRow(row);
    }
    table()->resizeColumnsToContents();
}

void ExpensesWidget::addExpense()
{
    ExpenseDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted)
    {
        DataManager::instance().data().expenses.append(dialog.resultData());
        if (persistChanges())
            refresh();
    }
}

void ExpensesWidget::editExpense()
{
    const int row = selectedRow();
    auto &list = DataManager::instance().data().expenses;
    if (row < 0 || row >= list.size())
        return;
    ExpenseDialog dialog(this, &list[row]);
    if (dialog.exec() == QDialog::Accepted)
    {
        list[row] = dialog.resultData();
        if (persistChanges())
            refresh();
    }
}

void ExpensesWidget::deleteExpense()
{
    const int row = selectedRow();
    auto &list = DataManager::instance().data().expenses;
    if (row < 0 || row >= list.size())
        return;
    if (QMessageBox::question(this, "Delete Expense", "Delete the selected expense?") == QMessageBox::Yes)
    {
        list.removeAt(row);
        if (persistChanges())
            refresh();
    }
}

void ExpensesWidget::deleteAllExpenses()
{
    auto &list = DataManager::instance().data().expenses;
    if (list.isEmpty())
        return;

    if (QMessageBox::question(this, "Delete All Expenses",
                              "Are you sure you want to delete ALL non‑bill expenses? This action cannot be undone.",
                              QMessageBox::Yes | QMessageBox::No) == QMessageBox::Yes)
    {
        list.clear();
        if (persistChanges())
            refresh();
    }
}

void ExpensesWidget::onDoubleClicked(const QModelIndex &)
{
    editExpense();
}