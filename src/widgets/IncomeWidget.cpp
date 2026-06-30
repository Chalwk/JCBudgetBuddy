#include "widgets/IncomeWidget.h"

#include <QMessageBox>
#include <QStandardItem>

IncomeWidget::IncomeWidget(QWidget* parent)
    : BaseTableWidget(parent) {
    setupTable({ "Name", "Amount", "Frequency", "Start Date", "End Date", "Active", "Notes" });
    setupButtons("Add Income Stream", "Edit", "Delete");

    connect(addButton(), &QPushButton::clicked, this, &IncomeWidget::addIncome);
    connect(editButton(), &QPushButton::clicked, this, &IncomeWidget::editIncome);
    connect(deleteButton(), &QPushButton::clicked, this, &IncomeWidget::deleteIncome);
    connect(table(), &QTableView::doubleClicked, this, &IncomeWidget::onDoubleClicked);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &IncomeWidget::refresh);

    refresh();
}

bool IncomeWidget::persistChanges() {
    QString error;
    if (!DataManager::instance().save(&error)) {
        QMessageBox::critical(this, "Save Error", error);
        return false;
    }
    emit DataManager::instance().dataChanged();
    return true;
}

void IncomeWidget::refresh() {
    model()->removeRows(0, model()->rowCount());
    const auto& list = DataManager::instance().data().incomes;
    for (const auto& income : list) {
        QList<QStandardItem*> row;
        auto* nameItem = new QStandardItem(income.name);
        QFont font = nameItem->font();
        font.setBold(true);
        nameItem->setFont(font);
        row << nameItem;
        row << new QStandardItem(QString("$%1").arg(income.amount, 0, 'f', 2));
        row << new QStandardItem(incomeFrequencyToString(income.frequency));
        row << new QStandardItem(formatDate(income.startDate));
        row << new QStandardItem(formatOptionalDate(income.endDate));
        row << new QStandardItem(income.isCurrentlyActive() ? "Active" : "Ended");
        row << new QStandardItem(income.notes);
        model()->appendRow(row);
    }
    table()->resizeColumnsToContents();
}

void IncomeWidget::addIncome() {
    IncomeDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        DataManager::instance().data().incomes.append(dialog.resultData());
        if (persistChanges()) refresh();
    }
}

void IncomeWidget::editIncome() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().incomes;
    if (row < 0 || row >= list.size()) return;
    IncomeDialog dialog(this, &list[row]);
    if (dialog.exec() == QDialog::Accepted) {
        list[row] = dialog.resultData();
        if (persistChanges()) refresh();
    }
}

void IncomeWidget::deleteIncome() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().incomes;
    if (row < 0 || row >= list.size()) return;
    if (QMessageBox::question(this, "Delete Income", "Delete the selected income stream?") == QMessageBox::Yes) {
        list.removeAt(row);
        if (persistChanges()) refresh();
    }
}

void IncomeWidget::onDoubleClicked(const QModelIndex&) {
    editIncome();
}