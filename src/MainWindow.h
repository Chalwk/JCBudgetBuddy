// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QMainWindow>
#include <QTabWidget>

#include "widgets/DashboardWidget.h"
#include "widgets/IncomeWidget.h"
#include "widgets/WeeklyBillsWidget.h"
#include "widgets/MonthlyBillsWidget.h"
#include "widgets/InvoicesWidget.h"
#include "widgets/PlanWidget.h"
#include "widgets/SavingsGoalsWidget.h"
#include "widgets/ExpensesWidget.h"

class MainWindow : public QMainWindow
{
    Q_OBJECT
public:
    explicit MainWindow(QWidget *parent = nullptr);

private slots:
    void refreshAll();
    void goToIncomeTab();

private:
    void updateDashboard();

    QWidget *m_central{};
    DashboardWidget *m_dashboard{};
    QTabWidget *m_tabs{};
    IncomeWidget *m_incomeWidget{};
    WeeklyBillsWidget *m_weeklyBillsWidget{};
    MonthlyBillsWidget *m_monthlyBillsWidget{};
    InvoicesWidget *m_invoicesWidget{};
    PlanWidget *m_planWidget{};
    SavingsGoalsWidget *m_savingsGoalsWidget{};
    ExpensesWidget *m_expensesWidget{};
};