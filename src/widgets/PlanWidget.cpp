#include "widgets/PlanWidget.h"

#include <QMessageBox>
#include <QStandardItem>

PlanWidget::PlanWidget(QWidget* parent)
    : BaseTableWidget(parent) {
    setupTable({ "Item Name", "Total Cost", "Deposit", "Weekly Payment", "Timeframe" });
    setupButtons("Add Plan", "Edit", "Delete");

    auto* analyzeButton = new QPushButton("Analyze Selected", this);
    auto* row = qobject_cast<QHBoxLayout*>(layout()->itemAt(1)->layout());
    row->insertWidget(3, analyzeButton);

    connect(addButton(), &QPushButton::clicked, this, &PlanWidget::addPlan);
    connect(editButton(), &QPushButton::clicked, this, &PlanWidget::editPlan);
    connect(deleteButton(), &QPushButton::clicked, this, &PlanWidget::deletePlan);
    connect(analyzeButton, &QPushButton::clicked, this, &PlanWidget::analyzePlan);
    connect(table(), &QTableView::doubleClicked, this, &PlanWidget::onDoubleClicked);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &PlanWidget::refresh);

    refresh();
}

bool PlanWidget::persistChanges() {
    QString error;
    if (!DataManager::instance().save(&error)) {
        QMessageBox::critical(this, "Save Error", error);
        return false;
    }
    emit DataManager::instance().dataChanged();
    return true;
}

void PlanWidget::refresh() {
    model()->removeRows(0, model()->rowCount());
    const auto& list = DataManager::instance().data().plans;
    for (const auto& plan : list) {
        QList<QStandardItem*> row;
        row << new QStandardItem(plan.name);
        row << new QStandardItem(QString("$%1").arg(plan.totalCost, 0, 'f', 2));
        row << new QStandardItem(QString("$%1").arg(plan.deposit, 0, 'f', 2));
        row << new QStandardItem(QString("$%1").arg(plan.weeklyPayment, 0, 'f', 2));
        row << new QStandardItem(plan.timeframeString());
        model()->appendRow(row);
    }
    table()->resizeColumnsToContents();
}

void PlanWidget::addPlan() {
    PlanDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        DataManager::instance().data().plans.append(dialog.resultData());
        if (persistChanges()) refresh();
    }
}

void PlanWidget::editPlan() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().plans;
    if (row < 0 || row >= list.size()) return;
    PlanDialog dialog(this, &list[row]);
    if (dialog.exec() == QDialog::Accepted) {
        list[row] = dialog.resultData();
        if (persistChanges()) refresh();
    }
}

void PlanWidget::deletePlan() {
    const int row = selectedRow();
    auto& list = DataManager::instance().data().plans;
    if (row < 0 || row >= list.size()) return;
    if (QMessageBox::question(this, "Delete Plan Item", "Delete the selected plan item?") == QMessageBox::Yes) {
        list.removeAt(row);
        if (persistChanges()) refresh();
    }
}

void PlanWidget::showAnalysisForRow(int row) {
    auto& list = DataManager::instance().data().plans;
    if (row < 0 || row >= list.size()) return;
    PlanAnalysisDialog dialog(this);
    dialog.setPlan(list[row]);
    dialog.exec();
}

void PlanWidget::analyzePlan() {
    showAnalysisForRow(selectedRow());
}

void PlanWidget::onDoubleClicked(const QModelIndex& index) {
    showAnalysisForRow(index.row());
}
