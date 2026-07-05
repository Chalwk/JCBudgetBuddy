// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#include "MainWindow.h"
#include "data/DataManager.h"

#include <QVBoxLayout>
#include <QLabel>
#include <QMessageBox>
#include <QFrame>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    setWindowTitle("JCBudgetBuddy");
    resize(1280, 860);

    m_central = new QWidget(this);
    setCentralWidget(m_central);

    auto *root = new QVBoxLayout(m_central);
    root->setContentsMargins(8, 8, 8, 8);
    root->setSpacing(6);

    auto *headerFrame = new QFrame;
    headerFrame->setObjectName("headerFrame");
    auto *headerLayout = new QVBoxLayout(headerFrame);
    headerLayout->setContentsMargins(12, 8, 12, 8);
    auto *title = new QLabel("JCBudgetBuddy");
    title->setObjectName("appTitle");
    auto *subtitle = new QLabel("Personal finance tracking, invoices, and affordability planning");
    subtitle->setObjectName("appSubtitle");
    headerLayout->addWidget(title);
    headerLayout->addWidget(subtitle);

    m_dashboard = new DashboardWidget;
    m_tabs = new QTabWidget;
    m_incomeWidget = new IncomeWidget;
    m_weeklyBillsWidget = new WeeklyBillsWidget;
    m_monthlyBillsWidget = new MonthlyBillsWidget;
    m_invoicesWidget = new InvoicesWidget;
    m_planWidget = new PlanWidget;
    m_savingsGoalsWidget = new SavingsGoalsWidget;
    m_expensesWidget = new ExpensesWidget;

    m_tabs->addTab(m_incomeWidget, "Income");
    m_tabs->addTab(m_weeklyBillsWidget, "Weekly Bills");
    m_tabs->addTab(m_monthlyBillsWidget, "Monthly Bills");
    m_tabs->addTab(m_invoicesWidget, "Invoices");
    m_tabs->addTab(m_planWidget, "Plans");
    m_tabs->addTab(m_savingsGoalsWidget, "Savings Goals");
    m_tabs->addTab(m_expensesWidget, "Non‑Bill Expenses");

    root->addWidget(headerFrame);
    root->addWidget(m_dashboard);
    root->addWidget(m_tabs, 1);

    connect(m_dashboard, &DashboardWidget::manageIncomeRequested, this, &MainWindow::goToIncomeTab);
    connect(&DataManager::instance(), &DataManager::dataChanged, this, &MainWindow::refreshAll);

    if (!DataManager::instance().lastError().isEmpty())
    {
        QMessageBox::warning(this, "Load Warning", DataManager::instance().lastError());
    }

    updateDashboard();
}

void MainWindow::goToIncomeTab()
{
    m_tabs->setCurrentWidget(m_incomeWidget);
}

void MainWindow::refreshAll()
{
    m_incomeWidget->refresh();
    m_weeklyBillsWidget->refresh();
    m_monthlyBillsWidget->refresh();
    m_invoicesWidget->refresh();
    m_planWidget->refresh();
    m_savingsGoalsWidget->refresh();
    m_expensesWidget->refresh();
    updateDashboard();
}

void MainWindow::updateDashboard()
{
    const auto &userData = DataManager::instance().data();

    double weeklyIncome = 0.0;
    int activeIncomeStreams = 0;
    for (const auto &income : userData.incomes)
    {
        weeklyIncome += income.weeklyAmount();
        if (income.isCurrentlyActive())
        {
            ++activeIncomeStreams;
        }
    }

    double weeklyExpenses = 0.0;
    for (const auto &bill : userData.weeklyBills)
    {
        weeklyExpenses += bill.weeklyAmount();
    }
    for (const auto &bill : userData.monthlyBills)
    {
        weeklyExpenses += bill.weeklyAmount();
    }

    const double remaining = weeklyIncome - weeklyExpenses;
    const double monthlyAverage = remaining * 4.0;

    double totalExpenses = 0.0;
    for (const auto &exp : userData.expenses)
    {
        totalExpenses += exp.amount;
    }

    m_dashboard->setStats(weeklyIncome, weeklyExpenses, remaining, monthlyAverage,
                          activeIncomeStreams, totalExpenses);
}