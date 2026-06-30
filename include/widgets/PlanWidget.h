#pragma once

#include "widgets/BaseTableWidget.h"
#include "dialogs/ItemDialogs.h"

class PlanWidget : public BaseTableWidget {
    Q_OBJECT
public:
    explicit PlanWidget(QWidget* parent = nullptr);

    void refresh() override;

private slots:
    void addPlan();
    void editPlan();
    void deletePlan();
    void analyzePlan();
    void onDoubleClicked(const QModelIndex& index);

private:
    bool persistChanges();
    void showAnalysisForRow(int row);
};
