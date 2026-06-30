#include "widgets/DashboardWidget.h"

#include <QGridLayout>
#include <QFrame>
#include <QVBoxLayout>
#include <QHBoxLayout>

static QFrame* makeCard(const QString& title, QLabel*& valueLabel) {
    auto* card = new QFrame;
    card->setObjectName("dashboardCard");
    card->setFrameShape(QFrame::StyledPanel);
    auto* layout = new QVBoxLayout(card);
    auto* titleLabel = new QLabel(title);
    titleLabel->setObjectName("dashboardCardTitle");
    valueLabel = new QLabel("0");
    valueLabel->setObjectName("dashboardCardValue");
    layout->addWidget(titleLabel);
    layout->addWidget(valueLabel);
    layout->addStretch();
    return card;
}

DashboardWidget::DashboardWidget(QWidget* parent)
    : QWidget(parent) {
    auto* root = new QVBoxLayout(this);
    auto* titleRow = new QHBoxLayout;
    auto* titleLabel = new QLabel("Dashboard Overview");
    titleLabel->setObjectName("sectionTitle");
    auto* manageButton = new QPushButton("Manage Income");
    connect(manageButton, &QPushButton::clicked, this, &DashboardWidget::manageIncomeRequested);
    titleRow->addWidget(titleLabel);
    titleRow->addStretch();
    titleRow->addWidget(manageButton);

    auto* grid = new QGridLayout;
    grid->addWidget(makeCard("Weekly Income", m_weeklyIncomeValue), 0, 0);
    grid->addWidget(makeCard("Weekly Expenses", m_weeklyExpensesValue), 0, 1);
    grid->addWidget(makeCard("Remaining Balance", m_remainingValue), 1, 0);
    grid->addWidget(makeCard("Monthly Average", m_monthlyAverageValue), 1, 1);
    grid->addWidget(makeCard("Active Income Streams", m_activeIncomeValue), 2, 0, 1, 2);

    root->addLayout(titleRow);
    root->addLayout(grid);
}

void DashboardWidget::setStats(double weeklyIncome, double weeklyExpenses, double remaining, double monthlyAverage, int activeIncomeStreams) {
    m_weeklyIncomeValue->setText(QString("$%1").arg(weeklyIncome, 0, 'f', 2));
    m_weeklyExpensesValue->setText(QString("$%1").arg(weeklyExpenses, 0, 'f', 2));
    m_remainingValue->setText(QString("$%1").arg(remaining, 0, 'f', 2));
    m_monthlyAverageValue->setText(QString("$%1").arg(monthlyAverage, 0, 'f', 2));
    m_activeIncomeValue->setText(QString::number(activeIncomeStreams));

    const QString balanceColor = remaining >= 0.0 ? "#107c10" : "#c50f1f";
    m_remainingValue->setStyleSheet(QString("font-size: 24px; font-weight: bold; color: %1;").arg(balanceColor));
}
