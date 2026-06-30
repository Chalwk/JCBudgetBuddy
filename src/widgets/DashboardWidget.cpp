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
    root->setContentsMargins(0, 0, 0, 0);
    root->setSpacing(8);

    auto* titleRow = new QHBoxLayout;
    auto* titleLabel = new QLabel("Dashboard Overview");
    titleLabel->setObjectName("sectionTitle");
    auto* manageButton = new QPushButton("Manage Income");
    connect(manageButton, &QPushButton::clicked, this, &DashboardWidget::manageIncomeRequested);
    titleRow->addWidget(titleLabel);
    titleRow->addStretch();
    titleRow->addWidget(manageButton);

    auto* cardsLayout = new QHBoxLayout;
    cardsLayout->setSpacing(10);
    cardsLayout->addWidget(makeCard("Weekly Income", m_weeklyIncomeValue));
    cardsLayout->addWidget(makeCard("Weekly Expenses", m_weeklyExpensesValue));
    cardsLayout->addWidget(makeCard("Remaining Balance", m_remainingValue));
    cardsLayout->addWidget(makeCard("Monthly Average", m_monthlyAverageValue));

    for (int i = 0; i < cardsLayout->count(); ++i) {
        QLayoutItem* item = cardsLayout->itemAt(i);
        if (item->widget()) {
            item->widget()->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Preferred);
        }
    }

    root->addLayout(titleRow);
    root->addLayout(cardsLayout);
}

void DashboardWidget::setStats(double weeklyIncome, double weeklyExpenses, double remaining, double monthlyAverage, int /*activeIncomeStreams*/) {
    m_weeklyIncomeValue->setText(QString("$%1").arg(weeklyIncome, 0, 'f', 2));
    m_weeklyExpensesValue->setText(QString("$%1").arg(weeklyExpenses, 0, 'f', 2));
    m_remainingValue->setText(QString("$%1").arg(remaining, 0, 'f', 2));
    m_monthlyAverageValue->setText(QString("$%1").arg(monthlyAverage, 0, 'f', 2));

    const QString balanceColor = remaining >= 0.0 ? "#107c10" : "#c50f1f";
    m_remainingValue->setStyleSheet(QString("font-size: 24px; font-weight: bold; color: %1;").arg(balanceColor));
}