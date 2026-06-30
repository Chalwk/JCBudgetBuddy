// Copyright (c) 2026 Jericho Crosby (Chalwk).
// Licensed under the GPL License.

#pragma once

#include <QWidget>
#include <QLabel>
#include <QPushButton>

class DashboardWidget : public QWidget {
    Q_OBJECT
public:
    explicit DashboardWidget(QWidget* parent = nullptr);

    void setStats(double weeklyIncome, double weeklyExpenses, double remaining, double monthlyAverage, int activeIncomeStreams);

signals:
    void manageIncomeRequested();

private:
    QLabel* m_weeklyIncomeValue{};
    QLabel* m_weeklyExpensesValue{};
    QLabel* m_remainingValue{};
    QLabel* m_monthlyAverageValue{};
};